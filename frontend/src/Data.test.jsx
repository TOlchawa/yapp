import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import { Provider } from 'react-redux';
import { createAppStore } from './store.js';
import Data from './Data.jsx';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

function renderWithStore(ui) {
  const store = createAppStore({
    user: { userInfo: { id: 'u1' }, currentView: null },
  });
  return render(<Provider store={store}>{ui}</Provider>);
}

describe('Data view', () => {
  afterEach(() => vi.restoreAllMocks());
  it('shows Data title and selector', () => {
    renderWithStore(<Data />);
    expect(screen.getByRole('heading', { name: 'Data' })).toBeInTheDocument();
    expect(screen.getAllByRole('button', { name: 'Back' })).toHaveLength(2);
    expect(screen.getByRole('combobox')).toBeInTheDocument();
  });

  it('loads item IDs on mount', async () => {
    const ids = ['a1', 'a2'];
    const pending = new Promise(() => {});
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementation(() => pending);
    renderWithStore(<Data />);
    expect(screen.getByRole('combobox')).toHaveValue('items');
    for (const id of ids) {
      expect(await screen.findByText(id)).toBeInTheDocument();
    }
    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/items/user`,
      expect.objectContaining({
        method: 'POST',
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
  });

  it('switches to questions collection', async () => {
    const ids = ['q1', 'q2'];
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve([]) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      );
    renderWithStore(<Data />);
    const select = screen.getByRole('combobox');
    fireEvent.change(select, { target: { value: 'questions' } });
    for (const id of ids) {
      expect(await screen.findByText(id)).toBeInTheDocument();
    }
    expect(global.fetch).toHaveBeenLastCalledWith(
      `${BACKEND_URL}/question/ids/all`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
  });

  it('switches to friends collection', async () => {
    const relations = [
      { id: 'r1', friendId: 'f1', type: 'FRIENDS' },
      { id: 'r2', friendId: 'f2', type: 'BEST_FRIENDS' },
    ];
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve([]) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(relations) })
      );
    renderWithStore(<Data />);
    const select = screen.getByRole('combobox');
    fireEvent.change(select, { target: { value: 'friends' } });
    for (const r of relations) {
      const text = `${r.friendId} - ${r.type}`;
      expect(await screen.findByText(text)).toBeInTheDocument();
    }
    expect(global.fetch).toHaveBeenLastCalledWith(
      `${BACKEND_URL}/friend?userId=u1`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
  });
});

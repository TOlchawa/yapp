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
    const select = screen.getByRole('combobox');
    expect(select).toBeInTheDocument();
    expect(select).toHaveValue('items');
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

  it('switches to redis collection', async () => {
    const ids = ['d1', 'd2'];
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
    fireEvent.change(select, { target: { value: 'redis' } });
    for (const id of ids) {
      expect(await screen.findByText(id)).toBeInTheDocument();
    }
    expect(global.fetch).toHaveBeenLastCalledWith(
      `${BACKEND_URL}/data/ids`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
  });

  it('loads redis data when id clicked', async () => {
    const ids = ['d1'];
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve([]) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, text: () => Promise.resolve('text data') })
      );
    renderWithStore(<Data />);
    fireEvent.change(screen.getByRole('combobox'), {
      target: { value: 'redis' },
    });
    const btn = await screen.findByRole('button', { name: 'd1' });
    fireEvent.click(btn);
    const textarea = await screen.findByRole('textbox');
    expect(textarea).toHaveValue('text data');
    expect(global.fetch).toHaveBeenLastCalledWith(
      `${BACKEND_URL}/data?id=d1`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
  });

  it('shows format label for redis data', async () => {
    const ids = ['d1'];
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve([]) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, text: () => Promise.resolve('{"a":1}') })
      );
    renderWithStore(<Data />);
    fireEvent.change(screen.getByRole('combobox'), {
      target: { value: 'redis' },
    });
    const btn = await screen.findByRole('button', { name: 'd1' });
    fireEvent.click(btn);
    expect(await screen.findByText('JSON')).toBeInTheDocument();
  });

  it('detects jpeg format from binary data', async () => {
    const ids = ['d1'];
    const jpeg = '\xFF\xD8\xFF\xE0\x00\x10JFIF';
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve([]) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, text: () => Promise.resolve(jpeg) })
      );
    renderWithStore(<Data />);
    fireEvent.change(screen.getByRole('combobox'), {
      target: { value: 'redis' },
    });
    const btn = await screen.findByRole('button', { name: 'd1' });
    fireEvent.click(btn);
    expect(await screen.findByText('JPEG')).toBeInTheDocument();
  });
});

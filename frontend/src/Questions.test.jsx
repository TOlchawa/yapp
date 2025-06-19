import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import { Provider } from 'react-redux';
import { createAppStore } from './store.js';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';
import Questions from './Questions.jsx';

function renderWithStore(ui) {
  const store = createAppStore({
    user: { userInfo: { id: 'u1' }, currentView: null },
  });
  return render(<Provider store={store}>{ui}</Provider>);
}

describe('Questions view', () => {
  afterEach(() => vi.restoreAllMocks());

  it('shows Questions title', () => {
    renderWithStore(<Questions />);
    expect(
      screen.getByRole('heading', { name: 'Questions' })
    ).toBeInTheDocument();
    expect(screen.getAllByRole('button', { name: 'Back' })).toHaveLength(2);
  });

  it('loads questions on mount', async () => {
    const ids = ['q1', 'q2'];
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      );
    renderWithStore(<Questions />);
    for (const id of ids) {
      expect(await screen.findByText(id)).toBeInTheDocument();
    }
    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/question/ids/all`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
  });

  it('opens details view on click', async () => {
    const ids = ['q1'];
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({
          ok: true,
          json: () => Promise.resolve({ question: 'q' }),
        })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve([]) })
      );
    renderWithStore(<Questions />);
    const item = await screen.findByText('q1');
    fireEvent.click(item);
    await screen.findByRole('heading', { name: 'Question details' });
    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/question/detail?id=q1`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
  });
});

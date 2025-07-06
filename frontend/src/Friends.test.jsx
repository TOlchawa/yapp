import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import { Provider } from 'react-redux';
import { createAppStore } from './store.js';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';
import Friends from './Friends.jsx';

function renderWithStore(ui) {
  const store = createAppStore({
    user: { userInfo: { id: 'u1' }, currentView: null },
  });
  return render(<Provider store={store}>{ui}</Provider>);
}

describe('Friends component', () => {
  afterEach(() => vi.restoreAllMocks());

  it('shows title', () => {
    renderWithStore(<Friends />);
    expect(
      screen.getByRole('heading', { level: 1, name: 'Friends' })
    ).toBeInTheDocument();
    expect(screen.getAllByRole('button', { name: 'Back' })).toHaveLength(2);
  });

  it('loads friends on mount', async () => {
    const list = [
      { id: 'r1', friendId: 'f1', type: 'FRIENDS' },
      { id: 'r2', friendId: 'f2', type: 'BEST_FRIENDS' },
    ];
    global.fetch = vi
      .fn()
      .mockResolvedValue({ ok: true, json: () => Promise.resolve(list) });
    renderWithStore(<Friends />);
    for (const f of list) {
      expect(
        await screen.findByText(`${f.friendId} - ${f.type}`)
      ).toBeInTheDocument();
    }
    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/friend?userId=u1`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
  });
});

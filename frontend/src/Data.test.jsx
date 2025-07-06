import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import { Provider } from 'react-redux';
import { createAppStore } from './store.js';
import Data from './Data.jsx';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

function renderWithStore(ui) {
  const store = createAppStore({ user: { userInfo: { id: 'u1' }, currentView: null } });
  return render(<Provider store={store}>{ui}</Provider>);
}

describe('Data view', () => {
  it('shows Data title', () => {
    renderWithStore(<Data />);
    expect(screen.getByRole('heading', { name: 'Data' })).toBeInTheDocument();
    expect(screen.getAllByRole('button', { name: 'Back' })).toHaveLength(2);
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
});

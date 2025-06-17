import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import Search from './Search.jsx';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

describe('Search view', () => {
  it('shows Search title', () => {
    render(<Search />);
    expect(screen.getByRole('heading', { name: 'Search' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Back' })).toBeInTheDocument();
  });

  it('loads item IDs on mount', async () => {
    const ids = ['1', '2'];
    global.fetch = vi.fn(() =>
      Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
    );
    render(<Search />);
    await screen.findByText('1');
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

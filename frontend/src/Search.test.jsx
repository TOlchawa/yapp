import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import Search from './Search.jsx';
import { BACKEND_URL } from './config.js';

describe('Search view', () => {
  it('shows Search title', () => {
    render(<Search />);
    expect(screen.getByRole('heading', { name: 'Search' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Back' })).toBeInTheDocument();
  });

  it('fetches items on search', async () => {
    const items = [{ id: '1', name: 'Item1' }];
    global.fetch = vi.fn(() =>
      Promise.resolve({ ok: true, json: () => Promise.resolve(items) })
    );
    render(<Search />);
    fireEvent.change(screen.getByTestId('search-input'), {
      target: { value: 'tag1' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Search' }));
    await screen.findByText('Item1');
    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/items/tags`,
      expect.any(Object)
    );
  });
});

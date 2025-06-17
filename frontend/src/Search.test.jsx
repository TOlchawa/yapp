import { render, screen, fireEvent } from '@testing-library/react';
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
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementation(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve({}) })
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

  it('loads item details and shows barcode icon', async () => {
    const ids = ['123'];
    const item = { name: 'Beer', description: { barcode: '789' } };
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(item) })
      );
    render(<Search />);
    await screen.findByText('Beer');
    expect(global.fetch).toHaveBeenLastCalledWith(
      `${BACKEND_URL}/item?id=123`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
    expect(screen.getByLabelText('barcode')).toBeInTheDocument();
  });

  it('opens details view on item click without refetch', async () => {
    const ids = ['123'];
    const item = {
      name: 'Beer',
      description: { barcode: '789', pictures: [{ picture: 'img' }] },
    };
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(item) })
      );
    render(<Search />);
    const itemEl = await screen.findByText('Beer');
    global.fetch.mockClear();
    fireEvent.click(itemEl);
    await screen.findByRole('heading', { name: 'Item details' });
    expect(global.fetch).not.toHaveBeenCalled();
    expect(screen.getByText('Barcode: 789')).toBeInTheDocument();
  });
});

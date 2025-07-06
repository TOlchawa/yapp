import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import Search from './Search.jsx';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

describe('Search view', () => {
  it('shows Search title', () => {
    render(<Search />);
    expect(screen.getByRole('heading', { name: 'Search' })).toBeInTheDocument();
    expect(screen.getAllByRole('button', { name: 'Back' })).toHaveLength(2);
  });

  it('loads item IDs on mount', async () => {
    const ids = ['1', '2'];
    const pending = new Promise(() => {});
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementation(() => pending);
    render(<Search />);
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
      `${BACKEND_URL}/item/123`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
    expect(screen.getByLabelText('barcode')).toBeInTheDocument();
  });

  it('falls back to noname when item name missing', async () => {
    const ids = ['321'];
    const item = { description: { barcode: '456' } };
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(item) })
      );
    render(<Search />);
    expect(await screen.findByText('noname')).toBeInTheDocument();
    expect(screen.getByLabelText('barcode')).toBeInTheDocument();
  });

  it('opens details view on item click without refetch', async () => {
    const ids = ['123'];
    const item = {
      name: 'Beer',
      description: { barcode: '789', pictures: [{ id: 'pic1' }] },
    };
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(item) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({
          ok: true,
          arrayBuffer: () => Promise.resolve(new Uint8Array([3]).buffer),
        })
      );
    render(<Search />);
    const itemEl = await screen.findByText('Beer');
    global.fetch.mockClear();
    fireEvent.click(itemEl);
    await screen.findByAltText('Item');
    expect(global.fetch).toHaveBeenCalledTimes(1);
    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/data?id=pic1`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
    expect(screen.getByText('789')).toBeInTheDocument();
  });

  it('deletes item from details view', async () => {
    const ids = ['del1'];
    const item = { name: 'ToDelete', description: {} };
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(ids) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(item) })
      )
      .mockImplementationOnce(() => Promise.resolve({ ok: true }));

    render(<Search />);
    const itemEl = await screen.findByText('ToDelete');
    fireEvent.click(itemEl);
    await screen.findByRole('button', { name: 'Delete' });

    fireEvent.click(screen.getByRole('button', { name: 'Delete' }));

    await screen.findByRole('heading', { name: 'Search' });
    expect(global.fetch).toHaveBeenLastCalledWith(
      `${BACKEND_URL}/item/del1`,
      expect.objectContaining({
        method: 'DELETE',
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
    expect(screen.queryByText('ToDelete')).not.toBeInTheDocument();
  });
});

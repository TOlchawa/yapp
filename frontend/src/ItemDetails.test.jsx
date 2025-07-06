import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import ItemDetails from './ItemDetails.jsx';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

describe('ItemDetails', () => {
  afterEach(() => vi.restoreAllMocks());

  it('shows provided info and loads pictures', async () => {
    const info = {
      name: 'Beer',
      description: {
        barcode: '123',
        note: 'note',
        pictures: [{ id: 'img1' }, { id: 'img2' }],
      },
    };
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({
          ok: true,
          arrayBuffer: () => Promise.resolve(new Uint8Array([1]).buffer),
        })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({
          ok: true,
          arrayBuffer: () => Promise.resolve(new Uint8Array([2]).buffer),
        })
      );
    render(<ItemDetails id="1" info={info} />);
    expect(await screen.findAllByAltText('Item')).toHaveLength(2);
    expect(global.fetch).toHaveBeenNthCalledWith(
      1,
      `${BACKEND_URL}/data?id=img1`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
    expect(global.fetch).toHaveBeenNthCalledWith(
      2,
      `${BACKEND_URL}/data?id=img2`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
    expect(screen.getByText('Beer')).toBeInTheDocument();
    expect(screen.getByLabelText('barcode')).toBeInTheDocument();
    expect(screen.getByText('note')).toBeInTheDocument();
  });

  it('fetches info when not provided', async () => {
    const item = {
      name: 'Wine',
      description: { barcode: '789', pictures: [{ id: 'img2' }] },
    };
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(item) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({
          ok: true,
          arrayBuffer: () => Promise.resolve(new Uint8Array([2]).buffer),
        })
      );
    render(<ItemDetails id="2" />);
    await screen.findByAltText('Item');
    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/item/2`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
    expect(global.fetch).toHaveBeenLastCalledWith(
      `${BACKEND_URL}/data?id=img2`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
  });
});

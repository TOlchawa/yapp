import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import ItemDetails from './ItemDetails.jsx';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

describe('ItemDetails', () => {
  afterEach(() => vi.restoreAllMocks());

  it('shows provided info without fetching', () => {
    const info = {
      name: 'Beer',
      description: { barcode: '123', note: 'note', pictures: [{ picture: 'img' }] },
    };
    render(<ItemDetails id="1" info={info} />);
    expect(screen.getByText('Beer')).toBeInTheDocument();
    expect(screen.getByLabelText('barcode')).toBeInTheDocument();
    expect(screen.getByText('note')).toBeInTheDocument();
    expect(screen.getByAltText('Item')).toBeInTheDocument();
  });

  it('fetches info when not provided', async () => {
    const item = {
      name: 'Wine',
      description: { barcode: '789', pictures: [{ picture: 'img' }] },
    };
    global.fetch = vi.fn(() =>
      Promise.resolve({ ok: true, json: () => Promise.resolve(item) })
    );
    render(<ItemDetails id="2" />);
    await screen.findByText('Wine');
    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/item?id=2`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
  });
});

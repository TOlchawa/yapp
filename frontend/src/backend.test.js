import { vi } from 'vitest';
import { backendFetch } from './backend.js';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

describe('backendFetch', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('adds basic auth header', async () => {
    global.fetch = vi.fn(() => Promise.resolve({ ok: true }));
    await backendFetch('/test');
    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/test`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
  });
});

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
    const qs = [
      { id: 'q1', description: 'short' },
      { id: 'q2', description: 'a'.repeat(140) },
    ];
    global.fetch = vi.fn(() =>
      Promise.resolve({ ok: true, json: () => Promise.resolve(qs) })
    );
    renderWithStore(<Questions />);
    await screen.findByText('short');
    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/question?userId=u1`,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
    expect(screen.getByText('short')).toBeInTheDocument();
    expect(screen.getByText(`${'a'.repeat(125)}...`)).toBeInTheDocument();
  });

  it('opens details view on click', async () => {
    const qs = [{ id: 'q1', description: 'short' }];
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve(qs) })
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
    const item = await screen.findByText('short');
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

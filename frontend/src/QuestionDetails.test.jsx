import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { Provider } from 'react-redux';
import { vi } from 'vitest';
import { createAppStore } from './store.js';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';
import QuestionDetails from './QuestionDetails.jsx';

function renderWithStore(ui) {
  const store = createAppStore({
    user: { userInfo: { id: 'u1' }, currentView: null },
  });
  return render(<Provider store={store}>{ui}</Provider>);
}

describe('QuestionDetails', () => {
  afterEach(() => vi.restoreAllMocks());

  it('opens popup and posts answer', async () => {
    global.fetch = vi
      .fn()
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve({ question: 'q' }) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve([]) })
      )
      .mockImplementationOnce(() =>
        Promise.resolve({ ok: true, json: () => Promise.resolve('a1') })
      );

    renderWithStore(<QuestionDetails id="q1" />);

    await screen.findByText('q');

    fireEvent.click(screen.getByRole('button', { name: /answer question/i }));
    expect(screen.getByTestId('answer-popup')).toBeInTheDocument();

    fireEvent.change(screen.getByRole('textbox'), { target: { value: 'ans' } });
    fireEvent.click(screen.getByRole('button', { name: 'Submit' }));

    await waitFor(() => {
      expect(screen.queryByTestId('answer-popup')).not.toBeInTheDocument();
    });

    expect(global.fetch).toHaveBeenLastCalledWith(
      `${BACKEND_URL}/answer`,
      expect.objectContaining({
        method: 'POST',
        headers: expect.objectContaining({
          Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
        }),
      })
    );
    expect(screen.getByText('ans')).toBeInTheDocument();
  });
});

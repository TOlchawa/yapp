import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import { Provider } from 'react-redux';
import { createAppStore } from './store.js';
import { BACKEND_URL } from './config.js';
import Ask from './Ask.jsx';

function renderWithStore(ui) {
  const store = createAppStore({ user: { userInfo: { id: 'u1' }, currentView: null } });
  return render(<Provider store={store}>{ui}</Provider>);
}

describe('Ask view', () => {
  it('shows the Ask title', () => {
    renderWithStore(<Ask />);
    expect(screen.getByRole('heading', { name: 'Ask' })).toBeInTheDocument();
    expect(screen.getAllByRole('button', { name: 'Back' })).toHaveLength(2);
  });

  it('shows a textarea and Ask button', () => {
    renderWithStore(<Ask />);
    expect(screen.getByTestId('ask-form')).toBeInTheDocument();
    expect(screen.getByTestId('ask-textarea')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Ask' })).toBeInTheDocument();
  });

  it('calls backend and shows message', async () => {
    global.fetch = vi.fn(() =>
      Promise.resolve({ ok: true, json: () => Promise.resolve('id1') })
    );
    renderWithStore(<Ask />);
    fireEvent.change(screen.getByTestId('ask-textarea'), { target: { value: 'q' } });
    fireEvent.click(screen.getByRole('button', { name: 'Ask' }));

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith(
        `${BACKEND_URL}/question`,
        expect.objectContaining({ method: 'POST' })
      );
      expect(screen.getByText(/question added/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: 'Update' })).toBeInTheDocument();
    });
  });

  it('smooths text and applies result', async () => {
    global.fetch = vi.fn(() =>
      Promise.resolve({ ok: true, text: () => Promise.resolve('fixed') })
    );
    renderWithStore(<Ask />);
    fireEvent.change(screen.getByTestId('ask-textarea'), {
      target: { value: 'orig' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Smooth' }));

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith(
        `${BACKEND_URL}/ai/smooth`,
        expect.objectContaining({ method: 'POST' })
      );
      expect(screen.getByTestId('smooth-popup')).toBeInTheDocument();
      expect(screen.getByRole('button', { name: 'Apply' })).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole('button', { name: 'Apply' }));
    expect(screen.queryByTestId('smooth-popup')).not.toBeInTheDocument();
    expect(screen.getByTestId('ask-textarea').value).toBe('fixed');
  });
});

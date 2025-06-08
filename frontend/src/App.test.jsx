import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import App from './App.jsx';
import { BACKEND_URL } from './config.js';

describe('App login flow', () => {
  it('changes title after successful login', async () => {
    const mockResponse = {
      user: { nickname: 'nickname1', email: 'user@example.com', id: '1' },
      jwtToken: 'token',
    };
    global.fetch = vi.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockResponse),
      })
    );

    render(<App />);

    fireEvent.change(screen.getByTestId('email-input'), {
      target: { value: 'user@example.com' },
    });
    fireEvent.change(screen.getByTestId('password-input'), {
      target: { value: 'secret' },
    });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(screen.getByRole('heading', { name: 'Welcome' })).toBeInTheDocument();
      expect(screen.getByTestId('user-nickname')).toHaveTextContent('nickname1');
      expect(screen.queryByTestId('email-input')).not.toBeInTheDocument();
    });
  });
});

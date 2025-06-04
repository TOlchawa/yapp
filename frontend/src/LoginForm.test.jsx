import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import LoginForm from './LoginForm.jsx';

describe('LoginForm', () => {
  it('renders email and password inputs', () => {
    render(<LoginForm />);
    expect(screen.getByTestId('email-input')).toBeInTheDocument();
    expect(screen.getByTestId('password-input')).toBeInTheDocument();
  });

  it('calls fetch with credentials and shows message', async () => {
    const mockResponse = {
      user: { nickname: 'Nick', email: 'user@example.com', id: '1' },
      jwtToken: 'token',
    };
    global.fetch = vi.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockResponse),
      })
    );

    render(<LoginForm />);

    fireEvent.change(screen.getByTestId('email-input'), {
      target: { value: 'user@example.com' },
    });
    fireEvent.change(screen.getByTestId('password-input'), {
      target: { value: 'secret' },
    });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith(
        'http://localhost:9090/user',
        expect.objectContaining({
          method: 'POST',
        })
      );
      expect(screen.getByText(/Hello Nick/)).toBeInTheDocument();
    });
  });
});

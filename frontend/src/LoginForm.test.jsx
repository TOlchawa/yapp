import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import LoginForm from './LoginForm.jsx';
import { BACKEND_URL } from './config.js';

describe('LoginForm', () => {
  it('renders email and password inputs', () => {
    render(<LoginForm />);
    expect(screen.getByTestId('email-input')).toBeInTheDocument();
    expect(screen.getByTestId('password-input')).toBeInTheDocument();
  });

  it('calls fetch with credentials and shows user info', async () => {
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
        `${BACKEND_URL}/user`,
        expect.objectContaining({
          method: 'POST',
        })
      );
      expect(screen.getByText(/Nickname: Nick/)).toBeInTheDocument();
      expect(screen.getByText(/Email: user@example.com/)).toBeInTheDocument();
      expect(screen.getByText(/ID: 1/)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: 'Add' })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: 'Compare' })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: 'Ask' })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: 'Questions' })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: 'Friends' })).toBeInTheDocument();
    });
  });

  it('shows proper view when a button is clicked', async () => {
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
      fireEvent.click(screen.getByRole('button', { name: 'Add' }));
      expect(screen.getByRole('heading', { name: 'Add' })).toBeInTheDocument();

      fireEvent.click(screen.getByRole('button', { name: 'Compare' }));
      expect(screen.getByRole('heading', { name: 'Compare' })).toBeInTheDocument();

      fireEvent.click(screen.getByRole('button', { name: 'Ask' }));
      expect(screen.getByRole('heading', { name: 'Ask' })).toBeInTheDocument();

      fireEvent.click(screen.getByRole('button', { name: 'Questions' }));
      expect(screen.getByRole('heading', { name: 'Questions' })).toBeInTheDocument();

      fireEvent.click(screen.getByRole('button', { name: 'Friends' }));
      expect(screen.getByRole('heading', { name: 'Friends' })).toBeInTheDocument();
    });
  });
});

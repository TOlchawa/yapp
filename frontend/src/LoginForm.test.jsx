import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import LoginForm from './LoginForm.jsx';
import { BACKEND_URL } from './config.js';

describe('LoginForm', () => {
  afterEach(() => vi.restoreAllMocks());
  it('renders email and password inputs', () => {
    render(<LoginForm />);
    expect(screen.getByTestId('email-input')).toBeInTheDocument();
    expect(screen.getByTestId('password-input')).toBeInTheDocument();
    expect(screen.getByTestId('remember-checkbox')).toBeInTheDocument();
  });

  it('calls fetch with credentials and shows user info', async () => {
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

    render(<LoginForm />);

    fireEvent.change(screen.getByTestId('email-input'), {
      target: { value: 'user@example.com' },
    });
    fireEvent.change(screen.getByTestId('password-input'), {
      target: { value: 'secret' },
    });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await screen.findByRole('button', { name: 'Add' });

    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/user`,
      expect.objectContaining({
        method: 'POST',
      })
    );
    expect(screen.queryByTestId('email-input')).not.toBeInTheDocument();
    expect(screen.queryByTestId('password-input')).not.toBeInTheDocument();
    expect(screen.getByText(/Nickname: nickname1/)).toBeInTheDocument();
    expect(screen.getByText(/Email: user@example.com/)).toBeInTheDocument();
    expect(screen.getByText(/ID: 1/)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Add' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Compare' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Ask' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Questions' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Friends' })).toBeInTheDocument();
  });


  it('stores credentials in cookies when remember is checked', async () => {
    const mockResponse = {
      user: { nickname: 'name', email: 'user@example.com', id: '1' },
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
    fireEvent.click(screen.getByTestId('remember-checkbox'));
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(document.cookie).toMatch(/email=user%40example.com/);
      expect(document.cookie).toMatch(/password=secret/);
    });
  });

  it('prefills inputs from cookies on load', () => {
    document.cookie = 'email=foo%40example.com';
    document.cookie = 'password=bar';
    render(<LoginForm />);
    expect(screen.getByTestId('email-input')).toHaveValue('foo@example.com');
    expect(screen.getByTestId('password-input')).toHaveValue('bar');
  });


  it(
    'shows proper view when a button is clicked',
    { timeout: 10000 },
    async () => {
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

      await screen.findByRole('button', { name: 'Add' });

      fireEvent.click(screen.getByRole('button', { name: 'Add' }));
      await screen.findByRole('heading', { name: 'Add' });
      console.log('.');

      fireEvent.click(screen.getByRole('button', { name: 'Compare' }));
      await screen.findByRole('heading', { name: 'Compare' });
      console.log('.');

      fireEvent.click(screen.getByRole('button', { name: 'Ask' }));
      await screen.findByRole('heading', { name: 'Ask' });
      console.log('.');

      fireEvent.click(screen.getByRole('button', { name: 'Questions' }));
      await screen.findByRole('heading', { name: 'Questions' });
      console.log('.');

      fireEvent.click(screen.getByRole('button', { name: 'Friends' }));
      await screen.findByRole('heading', { name: 'Friends' });
      console.log('.');
    }
  );
});

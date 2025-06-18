import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import LoginForm from './LoginForm.jsx';
import { BACKEND_URL } from './config.js';
import { Provider } from 'react-redux';
import { createAppStore } from './store.js';

function renderWithProvider(ui) {
  const testStore = createAppStore();
  return render(<Provider store={testStore}>{ui}</Provider>);
}

describe('LoginForm', () => {
  afterEach(() => {
    vi.restoreAllMocks();
    document.cookie
      .split(';')
      .forEach(
        (c) =>
          (document.cookie = c.replace(
            /=.*/,
            '=;expires=' + new Date(0).toUTCString() + ';path=/'
          ))
      );
  });
  it('renders email and password inputs', () => {
    renderWithProvider(<LoginForm />);
    expect(screen.getByTestId('email-input')).toBeInTheDocument();
    expect(screen.getByTestId('password-input')).toBeInTheDocument();
    expect(screen.getByTestId('remember-checkbox')).toBeInTheDocument();
  });

  it('toggles password visibility', () => {
    renderWithProvider(<LoginForm />);
    const input = screen.getByTestId('password-input');
    const toggle = screen.getByRole('button', { name: /show password/i });
    expect(input).toHaveAttribute('type', 'password');
    fireEvent.click(toggle);
    expect(input).toHaveAttribute('type', 'text');
    expect(toggle).toHaveAccessibleName('Hide password');
    fireEvent.click(toggle);
    expect(input).toHaveAttribute('type', 'password');
    expect(toggle).toHaveAccessibleName('Show password');
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
        text: () => Promise.resolve('1.0.0'),
      })
    );

    renderWithProvider(<LoginForm />);

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
    expect(screen.getByRole('button', { name: 'Search' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Ask' })).toBeInTheDocument();
    expect(
      screen.getByRole('button', { name: 'Questions' })
    ).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Friends' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Logs' })).toBeInTheDocument();
    expect(screen.getByText('Server version: 1.0.0')).toBeInTheDocument();
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
        text: () => Promise.resolve('1.0.0'),
      })
    );

    renderWithProvider(<LoginForm />);
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
    renderWithProvider(<LoginForm />);
    expect(screen.getByTestId('email-input')).toHaveValue('foo@example.com');
    expect(screen.getByTestId('password-input')).toHaveValue('bar');
  });

  it('shows signup option when login fails and creates account', async () => {
    global.fetch = vi
      .fn()
      .mockResolvedValueOnce({ ok: false })
      .mockResolvedValueOnce({ ok: true });

    renderWithProvider(<LoginForm />);
    fireEvent.change(screen.getByTestId('email-input'), {
      target: { value: 'new@example.com' },
    });
    fireEvent.change(screen.getByTestId('password-input'), {
      target: { value: 'secret' },
    });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await screen.findByText(/login failed/i);
    const signupButton = screen.getByRole('button', {
      name: /create account/i,
    });
    expect(signupButton).toBeInTheDocument();

    fireEvent.click(signupButton);
    await screen.findByText(/account created/i);

    expect(global.fetch).toHaveBeenCalledWith(
      `${BACKEND_URL}/user`,
      expect.objectContaining({ method: 'PUT' })
    );
  });

  it(
    'shows proper view when a button is clicked',
    { timeout: 10000 },
    async () => {
      const mockResponse = {
        user: { nickname: 'Nick', email: 'user@example.com', id: '1' },
        jwtToken: 'token',
      };
      const items = ['id1'];
      global.fetch = vi.fn((url) => {
        if (url.endsWith('/items/user')) {
          return Promise.resolve({
            ok: true,
            json: () => Promise.resolve(items),
          });
        }
        if (url.endsWith('/user')) {
          return Promise.resolve({
            ok: true,
            json: () => Promise.resolve(mockResponse),
          });
        }
        if (url.endsWith('/version')) {
          return Promise.resolve({
            ok: true,
            text: () => Promise.resolve('1.0.0'),
          });
        }
        return Promise.resolve({ ok: false });
      });

      renderWithProvider(<LoginForm />);
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
      expect(screen.getAllByRole('button', { name: 'Back' })).toHaveLength(2);
      fireEvent.click(screen.getAllByRole('button', { name: 'Back' })[0]);
      await screen.findByRole('button', { name: 'Compare' });
      fireEvent.click(screen.getByRole('button', { name: 'Compare' }));
      await screen.findByRole('heading', { name: 'Compare' });
      fireEvent.click(screen.getAllByRole('button', { name: 'Back' })[0]);
      await screen.findByRole('button', { name: 'Search' });
      fireEvent.click(screen.getByRole('button', { name: 'Search' }));
      await screen.findByRole('heading', { name: 'Search' });
      fireEvent.click(screen.getAllByRole('button', { name: 'Back' })[0]);
      await screen.findByRole('button', { name: 'Ask' });
      fireEvent.click(screen.getByRole('button', { name: 'Ask' }));
      await screen.findByRole('heading', { name: 'Ask' });
      fireEvent.click(screen.getAllByRole('button', { name: 'Back' })[0]);
      await screen.findByRole('button', { name: 'Questions' });
      fireEvent.click(screen.getByRole('button', { name: 'Questions' }));
      await screen.findByRole('heading', { name: 'Questions' });
      fireEvent.click(screen.getAllByRole('button', { name: 'Back' })[0]);
      await screen.findByRole('button', { name: 'Friends' });
      fireEvent.click(screen.getByRole('button', { name: 'Friends' }));
      await screen.findByRole('heading', { name: 'Friends' });
      fireEvent.click(screen.getAllByRole('button', { name: 'Back' })[0]);
      await screen.findByRole('button', { name: 'Logs' });
      fireEvent.click(screen.getByRole('button', { name: 'Logs' }));
      await screen.findByRole('heading', { name: 'Logs' });
      fireEvent.click(screen.getAllByRole('button', { name: 'Back' })[0]);
    }
  );

  it('logs out and clears cookies', async () => {
    const mockResponse = {
      user: { nickname: 'Nick', email: 'user@example.com', id: '1' },
      jwtToken: 'token',
    };
    global.fetch = vi.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockResponse),
        text: () => Promise.resolve('1.0.0'),
      })
    );

    document.cookie = 'email=foo%40example.com';
    document.cookie = 'password=bar';

    renderWithProvider(<LoginForm />);
    fireEvent.change(screen.getByTestId('email-input'), {
      target: { value: 'user@example.com' },
    });
    fireEvent.change(screen.getByTestId('password-input'), {
      target: { value: 'secret' },
    });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await screen.findByRole('button', { name: 'Logout' });
    fireEvent.click(screen.getByRole('button', { name: 'Logout' }));

    await screen.findByRole('button', { name: /login/i });
    expect(screen.getByTestId('email-input')).toHaveValue('');
    expect(document.cookie).not.toMatch(/email=/);
    expect(document.cookie).not.toMatch(/password=/);
  });
});

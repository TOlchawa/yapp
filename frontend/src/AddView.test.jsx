import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import AddView from './AddView.jsx';

describe('AddView', () => {
  beforeEach(() => {
    Object.defineProperty(global, 'isSecureContext', {
      value: true,
      configurable: true,
    });
    vi.spyOn(HTMLMediaElement.prototype, 'play').mockImplementation(() => Promise.resolve());
    global.navigator.permissions = {
      query: vi.fn(() => Promise.resolve({ state: 'granted' })),
    };
  });

  afterEach(() => {
    vi.restoreAllMocks();
    delete global.navigator.mediaDevices;
    delete global.navigator.permissions;
  });

  it('shows Add title', () => {
    render(<AddView />);
    expect(screen.getByRole('heading', { name: 'Add' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Back' })).toBeInTheDocument();
  });

  it('enables camera and shows take photo button', async () => {
    global.navigator.mediaDevices = {
      getUserMedia: vi.fn(() =>
        Promise.resolve({
          active: true,
          addEventListener: vi.fn(),
          getVideoTracks: vi.fn(() => [{ readyState: 'live', addEventListener: vi.fn() }]),
        })
      ),
    };

    render(<AddView />);

    const enableBtn = screen.getByRole('button', { name: /enable camera/i });
    fireEvent.click(enableBtn);

    await waitFor(() => {
      expect(navigator.mediaDevices.getUserMedia).toHaveBeenCalledWith({ video: true });
      expect(screen.getByRole('button', { name: /take photo/i })).toBeInTheDocument();
      expect(screen.getByTestId('camera-preview')).toBeInTheDocument();
    });

    const debugOutput = screen.getByTestId('debug-output');
    expect(debugOutput.value).toMatch(/permission state: granted/i);
  });

  it('shows error when camera not supported', async () => {
    delete global.navigator.mediaDevices;

    render(<AddView />);

    const enableBtn = screen.getByRole('button', { name: /enable camera/i });
    fireEvent.click(enableBtn);

    await waitFor(() => {
      expect(screen.getByText(/camera not supported/i)).toBeInTheDocument();
    });
  });
});

import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import AddView from './AddView.jsx';

describe('AddView', () => {
  it('shows Add title', () => {
    render(<AddView />);
    expect(screen.getByRole('heading', { name: 'Add' })).toBeInTheDocument();
  });

  it('enables camera and shows take photo button', async () => {
    global.navigator.mediaDevices = {
      getUserMedia: vi.fn(() => Promise.resolve('stream')),
    };

    render(<AddView />);

    const enableBtn = screen.getByRole('button', { name: /enable camera/i });
    fireEvent.click(enableBtn);

    await waitFor(() => {
      expect(navigator.mediaDevices.getUserMedia).toHaveBeenCalledWith({ video: true });
      expect(screen.getByRole('button', { name: /take photo/i })).toBeInTheDocument();
      expect(screen.getByTestId('camera-preview')).toBeInTheDocument();
    });
  });
});

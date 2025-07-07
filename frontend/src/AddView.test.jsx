import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { vi } from 'vitest';

const startMock = vi.fn();
const stopMock = vi.fn();

let mockBarcode = '';
vi.mock('./hooks/useBarcodeScanner.js', () => ({
  default: () => ({
    videoRef: { current: null },
    get barcode() {
      return mockBarcode;
    },
    scanning: false,
    start: startMock,
    stop: stopMock,
  }),
}));

import AddView from './AddView.jsx';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

describe('AddView', () => {
  beforeEach(() => {
    mockBarcode = '';
    Object.defineProperty(global, 'isSecureContext', {
      value: true,
      configurable: true,
    });
    vi.spyOn(HTMLMediaElement.prototype, 'play').mockImplementation(() =>
      Promise.resolve()
    );
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
    expect(screen.getAllByRole('button', { name: 'Back' })).toHaveLength(2);
  });

  it('enables camera and shows take photo button', async () => {
    global.navigator.mediaDevices = {
      getUserMedia: vi.fn(() =>
        Promise.resolve({
          active: true,
          addEventListener: vi.fn(),
          getVideoTracks: vi.fn(() => [
            { readyState: 'live', addEventListener: vi.fn() },
          ]),
        })
      ),
    };

    render(<AddView />);

    const enableBtn = screen.getByRole('button', { name: /enable camera/i });
    fireEvent.click(enableBtn);

    await waitFor(() => {
      expect(navigator.mediaDevices.getUserMedia).toHaveBeenCalledWith({
        video: { facingMode: 'user' },
      });
      expect(
        screen.getByRole('button', { name: /take photo/i })
      ).toBeInTheDocument();
      expect(screen.getByTestId('camera-preview')).toBeInTheDocument();
      expect(
        screen.getByRole('button', { name: /switch to back/i })
      ).toBeInTheDocument();
      expect(
        screen.getByRole('button', { name: /scan barcode/i })
      ).toBeInTheDocument();
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

  it('switches between cameras', async () => {
    const mockStream = {
      active: true,
      addEventListener: vi.fn(),
      getVideoTracks: vi.fn(() => [
        { readyState: 'live', addEventListener: vi.fn(), stop: vi.fn() },
      ]),
    };
    global.navigator.mediaDevices = {
      getUserMedia: vi.fn(() => Promise.resolve(mockStream)),
    };

    render(<AddView />);

    fireEvent.click(screen.getByRole('button', { name: /enable camera/i }));

    await waitFor(() => {
      expect(
        screen.getByRole('button', { name: /switch to back/i })
      ).toBeInTheDocument();
    });

    navigator.mediaDevices.getUserMedia.mockResolvedValueOnce(mockStream);
    const switchBtn = screen.getByRole('button', { name: /switch to back/i });
    fireEvent.click(switchBtn);

    await waitFor(() => {
      expect(navigator.mediaDevices.getUserMedia).toHaveBeenLastCalledWith({
        video: { facingMode: 'environment' },
      });
      expect(
        screen.getByRole('button', { name: /switch to front/i })
      ).toBeInTheDocument();
    });
  });

  it('starts barcode scanning', async () => {
    global.navigator.mediaDevices = {
      getUserMedia: vi.fn(() =>
        Promise.resolve({
          active: true,
          addEventListener: vi.fn(),
          getVideoTracks: vi.fn(() => [
            { readyState: 'live', addEventListener: vi.fn() },
          ]),
        })
      ),
    };

    render(<AddView />);

    fireEvent.click(screen.getByRole('button', { name: /enable camera/i }));

    await waitFor(() => {
      expect(
        screen.getByRole('button', { name: /scan barcode/i })
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole('button', { name: /scan barcode/i }));
    expect(startMock).toHaveBeenCalled();
  });

  it('captures photo without sending request', async () => {
    const mockStream = {
      active: true,
      addEventListener: vi.fn(),
      getVideoTracks: vi.fn(() => [
        { readyState: 'live', addEventListener: vi.fn() },
      ]),
    };
    global.navigator.mediaDevices = {
      getUserMedia: vi.fn(() => Promise.resolve(mockStream)),
    };
    global.fetch = vi.fn(() => Promise.resolve({ ok: true }));

    vi.spyOn(HTMLCanvasElement.prototype, 'getContext').mockReturnValue({
      drawImage: vi.fn(),
    });
    vi.spyOn(HTMLCanvasElement.prototype, 'toDataURL').mockReturnValue(
      'data:image/jpeg;base64,testimg'
    );

    render(<AddView />);

    fireEvent.click(screen.getByRole('button', { name: /enable camera/i }));
    await screen.findByRole('button', { name: /take photo/i });

    // Set dimensions so drawImage works
    const video = screen.getByTestId('camera-preview');
    Object.defineProperty(video, 'videoWidth', { value: 10 });
    Object.defineProperty(video, 'videoHeight', { value: 10 });

    fireEvent.click(screen.getByRole('button', { name: /take photo/i }));

    await waitFor(() => {
      expect(global.fetch).not.toHaveBeenCalled();
    });
  });

  it('sends POST request with barcode when adding item', async () => {
    mockBarcode = '123456';
    global.fetch = vi.fn(() =>
      Promise.resolve({ ok: true, text: () => Promise.resolve('id123') })
    );

    const mockStream = {
      active: true,
      addEventListener: vi.fn(),
      getVideoTracks: vi.fn(() => [
        { readyState: 'live', addEventListener: vi.fn() },
      ]),
    };
    global.navigator.mediaDevices = {
      getUserMedia: vi.fn(() => Promise.resolve(mockStream)),
    };

    render(<AddView />);

    fireEvent.click(screen.getByRole('button', { name: /enable camera/i }));
    await screen.findByRole('button', { name: /add item/i });

    const addBtn = screen.getByRole('button', { name: /add item/i });
    fireEvent.click(addBtn);

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledTimes(1);
      expect(global.fetch).toHaveBeenCalledWith(
        `${BACKEND_URL}/item`,
        expect.objectContaining({
          method: 'POST',
          headers: expect.objectContaining({
            Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
          }),
        })
      );
    });

    const formData = global.fetch.mock.calls[0][1].body;
    expect(formData.get('name')).toBe('item123');
    expect(formData.get('barCode')).toBe('123456');
    expect(formData.has('pictureBase64')).toBe(false);
  });

  it('sends POST and PUT when adding item after taking photo', async () => {
    global.fetch = vi.fn(() =>
      Promise.resolve({ ok: true, text: () => Promise.resolve('id123') })
    );

    const mockStream = {
      active: true,
      addEventListener: vi.fn(),
      getVideoTracks: vi.fn(() => [
        { readyState: 'live', addEventListener: vi.fn() },
      ]),
    };
    global.navigator.mediaDevices = {
      getUserMedia: vi.fn(() => Promise.resolve(mockStream)),
    };

    vi.spyOn(HTMLCanvasElement.prototype, 'getContext').mockReturnValue({
      drawImage: vi.fn(),
    });
    vi.spyOn(HTMLCanvasElement.prototype, 'toDataURL').mockReturnValue(
      'data:image/jpeg;base64,testimg'
    );

    render(<AddView />);

    fireEvent.click(screen.getByRole('button', { name: /enable camera/i }));
    await screen.findByRole('button', { name: /take photo/i });

    const video = screen.getByTestId('camera-preview');
    Object.defineProperty(video, 'videoWidth', { value: 10 });
    Object.defineProperty(video, 'videoHeight', { value: 10 });

    fireEvent.click(screen.getByRole('button', { name: /take photo/i }));

    await waitFor(() => {
      expect(global.fetch).not.toHaveBeenCalled();
    });

    const addBtn = screen.getByRole('button', { name: /add item/i });
    fireEvent.click(addBtn);

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledTimes(2);
      expect(global.fetch).toHaveBeenNthCalledWith(
        1,
        `${BACKEND_URL}/item`,
        expect.objectContaining({
          method: 'POST',
          headers: expect.objectContaining({
            Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
          }),
        })
      );
      expect(global.fetch).toHaveBeenNthCalledWith(
        2,
        `${BACKEND_URL}/item/id123`,
        expect.objectContaining({
          method: 'PUT',
          headers: expect.objectContaining({
            Authorization: `Basic ${btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`)}`,
          }),
        })
      );
    });

    const postData = global.fetch.mock.calls[0][1].body;
    expect(postData.get('name')).toBe('item123');
    expect(postData.has('pictureBase64')).toBe(false);

    const putData = global.fetch.mock.calls[1][1].body;
    expect(putData.has('id')).toBe(false);
    expect(putData.get('pictureBase64')).toBe('testimg');
  });
});

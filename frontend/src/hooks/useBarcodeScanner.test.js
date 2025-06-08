import { renderHook, act } from '@testing-library/react';
import { vi } from 'vitest';
import useBarcodeScanner from './useBarcodeScanner.js';
import { BrowserMultiFormatReader } from '@zxing/browser';

vi.mock('@zxing/browser', () => {
  return {
    BrowserMultiFormatReader: vi.fn().mockImplementation(() => ({
      decodeFromVideoDevice: vi.fn((deviceId, video, cb) => {
        cb({ getText: () => '123456' }, null);
        return Promise.resolve();
      }),
      reset: vi.fn(),
    })),
  };
});

describe('useBarcodeScanner', () => {
  it('reads barcode from camera', async () => {
    const { result } = renderHook(() => useBarcodeScanner());
    await act(async () => {
      await result.current.start();
    });
    expect(result.current.barcode).toBe('123456');
    expect(result.current.scanning).toBe(true);
  });

  it('stops scanning', () => {
    const { result } = renderHook(() => useBarcodeScanner());
    act(() => {
      result.current.stop();
    });
    expect(result.current.scanning).toBe(false);
  });
});

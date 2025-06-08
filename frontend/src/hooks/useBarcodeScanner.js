import { useRef, useState } from 'react';
import { BrowserMultiFormatReader } from '@zxing/browser';

export default function useBarcodeScanner() {
  const videoRef = useRef(null);
  const [barcode, setBarcode] = useState(null);
  const [scanning, setScanning] = useState(false);
  const readerRef = useRef(null);

  async function start() {
    if (scanning) return;
    setScanning(true);
    const reader = new BrowserMultiFormatReader();
    readerRef.current = reader;
    try {
      await reader.decodeFromVideoDevice(null, videoRef.current, (result, err) => {
        if (result) {
          setBarcode(result.getText());
        }
      });
    } catch (e) {
      console.error(e);
    }
  }

  function stop() {
    if (readerRef.current) {
      readerRef.current.reset();
      readerRef.current = null;
    }
    setScanning(false);
  }

  return { videoRef, barcode, scanning, start, stop };
}

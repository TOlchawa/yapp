import React, { useRef, useState, useEffect } from 'react';
import useBarcodeScanner from './hooks/useBarcodeScanner.js';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

export default function AddView({ onBack = () => {} }) {
  const {
    videoRef,
    barcode,
    scanning,
    start: startScanning,
    stop: stopScanning,
  } = useBarcodeScanner();
  const [stream, setStream] = useState(null);
  const [photo, setPhoto] = useState(null);
  const [itemId, setItemId] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [isFrontCamera, setIsFrontCamera] = useState(true);
  // Start with a message so we know the debug box works
  const [debugMessages, setDebugMessages] = useState(['Debug box ready']);
  const canvasRef = useRef(null);

  function addDebug(msg) {
    setDebugMessages((prev) => [...prev, msg]);
  }

  // Confirm component is ready
  useEffect(() => {
    addDebug('AddView mounted');
  }, []);

  useEffect(() => {
    if (videoRef.current && stream) {
      // Attach the stream to the video element
      // JSDOM will just store this property
      const video = videoRef.current;
      video.srcObject = stream;
      video.muted = true;
      const track = stream.getVideoTracks()[0];
      if (track) {
        addDebug(`Track readyState: ${track.readyState}`);
        track.addEventListener('ended', () => addDebug('Track ended'));
      }
      video
        .play()
        .then(() => addDebug('Video playing'))
        .catch((err) => addDebug(`Failed to play video: ${err.message}`));
    }
    if (stream) {
      addDebug(`Stream active (effect): ${stream.active}`);
    }
  }, [stream]);

  async function startCamera(mode) {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      setErrorMessage('Camera not supported');
      addDebug('navigator.mediaDevices not available');
      return;
    }
    if (!window.isSecureContext) {
      setErrorMessage('Camera requires HTTPS or localhost');
      addDebug('Page not secure');
      return;
    }
    try {
      addDebug('Requesting camera stream');
      const userStream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: mode },
      });
      addDebug(`Stream active after getUserMedia: ${userStream.active}`);
      if (userStream.addEventListener) {
        userStream.addEventListener('inactive', () =>
          addDebug('Stream inactive')
        );
      }
      setStream(userStream);
      addDebug('Stream received');
    } catch (err) {
      setErrorMessage('Failed to access camera');
      addDebug(`getUserMedia error: ${err.message}`);
    }
  }

  async function handleEnableCamera() {
    setErrorMessage('');
    addDebug('Enable camera clicked');
    if (navigator.permissions && navigator.permissions.query) {
      try {
        const result = await navigator.permissions.query({ name: 'camera' });
        addDebug(`Permission state: ${result.state}`);
      } catch (err) {
        addDebug(`Permission query failed: ${err.message}`);
      }
    }
    await startCamera('user');
    setIsFrontCamera(true);
  }

  async function handleSwitchCamera() {
    const mode = isFrontCamera ? 'environment' : 'user';
    stopScanning();
    if (stream && stream.getTracks) {
      stream.getTracks().forEach((t) => t.stop());
    }
    await startCamera(mode);
    setIsFrontCamera(!isFrontCamera);
  }

  async function handleTakePhoto() {
    const video = videoRef.current;
    const canvas = canvasRef.current;
    if (!video || !canvas) {
      return;
    }
    addDebug('Taking photo');
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    const ctx = canvas.getContext('2d');
    ctx.drawImage(video, 0, 0);
    const dataUrl = canvas.toDataURL('image/jpeg');
    setPhoto(dataUrl);

    addDebug('Photo captured');
  }

  async function handleAddItem() {
    const formData = new FormData();
    formData.append('name', 'item123');
    if (barcode) {
      formData.append('barCode', barcode);
    }
    try {
      const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
      const res = await fetch(`${BACKEND_URL}/item`, {
        method: 'POST',
        headers: { Authorization: `Basic ${token}` },
        body: formData,
      });
      const idText = await res.text();
      setItemId(idText);
      addDebug('Item created on server');

      if (photo) {
        const updateData = new FormData();
        updateData.append('pictureBase64', photo.split(',')[1]);
        await fetch(`${BACKEND_URL}/item/${idText}`, {
          method: 'PUT',
          headers: { Authorization: `Basic ${token}` },
          body: updateData,
        });
        addDebug('Photo uploaded');
      }
    } catch (err) {
      addDebug(`Failed to create item: ${err.message}`);
    }
  }

  function handleScanBarcode() {
    if (scanning) {
      stopScanning();
    } else {
      startScanning();
    }
  }

  return (
    <div>
      <div className="view-header">
        <h1>Add</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>

      {errorMessage && <p className="error">{errorMessage}</p>}

      <div className="camera-window">
        {stream ? (
          <video
            data-testid="camera-preview"
            ref={videoRef}
            muted
            autoPlay
            playsInline
          />
        ) : (
          <div className="camera-placeholder">Camera disabled</div>
        )}
        {stream && (
          <>
            <button type="button" onClick={handleTakePhoto}>
              Take Photo
            </button>
            <button type="button" onClick={handleAddItem}>
              Add Item
            </button>
            <button type="button" onClick={handleSwitchCamera}>
              {isFrontCamera ? 'Switch to back' : 'Switch to front'}
            </button>
            <button type="button" onClick={handleScanBarcode}>
              {scanning ? 'Stop scanning' : 'Scan barcode'}
            </button>
          </>
        )}
        {!stream && (
          <button type="button" onClick={handleEnableCamera}>
            Enable Camera
          </button>
        )}
      </div>
      <canvas ref={canvasRef} style={{ display: 'none' }} />
      {photo && <img src={photo} alt="Captured" className="captured-photo" />}
      {scanning && (
        <input
          type="text"
          value={barcode || ''}
          readOnly
          data-testid="barcode-input"
        />
      )}
      <div className="debug-box">
        <textarea
          readOnly
          value={debugMessages.join('\n')}
          style={{ width: '100%', height: '100px' }}
          data-testid="debug-output"
        />
      </div>
      <footer className="view-footer">
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
    </div>
  );
}

import React, { useRef, useState, useEffect } from 'react';

export default function AddView({ onBack = () => {} }) {
  const [stream, setStream] = useState(null);
  const [photo, setPhoto] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [debugMessages, setDebugMessages] = useState([]);
  const videoRef = useRef(null);
  const canvasRef = useRef(null);

  function addDebug(msg) {
    setDebugMessages((prev) => [...prev, msg]);
  }

  useEffect(() => {
    if (videoRef.current && stream) {
      // Attach the stream to the video element
      // JSDOM will just store this property
      const video = videoRef.current;
      video.srcObject = stream;
      video.muted = true;
      video
        .play()
        .then(() => addDebug('Video playing'))
        .catch((err) => addDebug(`Failed to play video: ${err.message}`));
    }
  }, [stream]);

  async function handleEnableCamera() {
    setErrorMessage('');
    addDebug('Enable camera clicked');
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
      const userStream = await navigator.mediaDevices.getUserMedia({ video: true });
      setStream(userStream);
      addDebug('Stream received');
    } catch (err) {
      setErrorMessage('Failed to access camera');
      addDebug(`getUserMedia error: ${err.message}`);
    }
  }

  function handleTakePhoto() {
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
    setPhoto(canvas.toDataURL('image/png'));
  }

  return (
    <div>
      <div className="view-header">
        <h1>Add</h1>
        <button className="back-button" onClick={onBack}>
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
          <button onClick={handleTakePhoto}>Take Photo</button>
        )}
        {!stream && (
          <button onClick={handleEnableCamera}>Enable Camera</button>
        )}
      </div>
      <canvas ref={canvasRef} style={{ display: 'none' }} />
      {photo && <img src={photo} alt="Captured" />}
      <div className="debug-box">
        <textarea
          readOnly
          value={debugMessages.join('\n')}
          style={{ width: '100%', height: '100px' }}
          data-testid="debug-output"
        />
      </div>
    </div>
  );
}

import React, { useRef, useState, useEffect } from 'react';

export default function AddView({ onBack = () => {} }) {
  const [stream, setStream] = useState(null);
  const [photo, setPhoto] = useState(null);
  const videoRef = useRef(null);
  const canvasRef = useRef(null);

  useEffect(() => {
    if (videoRef.current && stream) {
      // Attach the stream to the video element
      // JSDOM will just store this property
      videoRef.current.srcObject = stream;
    }
  }, [stream]);

  async function handleEnableCamera() {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      return;
    }
    try {
      const userStream = await navigator.mediaDevices.getUserMedia({ video: true });
      setStream(userStream);
    } catch (err) {
      // Ignore errors for now
    }
  }

  function handleTakePhoto() {
    const video = videoRef.current;
    const canvas = canvasRef.current;
    if (!video || !canvas) {
      return;
    }
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    const ctx = canvas.getContext('2d');
    ctx.drawImage(video, 0, 0);
    setPhoto(canvas.toDataURL('image/png'));
  }

  return (
    <div style={{ position: 'relative' }}>
      <button className="back-button" onClick={onBack}>
        Back
      </button>
      <h1>Add</h1>
      {!stream && <button onClick={handleEnableCamera}>Enable Camera</button>}
      <div className="camera-window">
        {stream ? (
          <video
            data-testid="camera-preview"
            ref={videoRef}
            autoPlay
            playsInline
          />
        ) : (
          <div className="camera-placeholder">Camera disabled</div>
        )}
        {stream && (
          <button onClick={handleTakePhoto}>Take Photo</button>
        )}
      </div>
      <canvas ref={canvasRef} style={{ display: 'none' }} />
      {photo && <img src={photo} alt="Captured" />}
    </div>
  );
}

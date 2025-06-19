import React, { useState, useRef } from 'react';
import { useSelector } from 'react-redux';
import { FaMicrophone } from 'react-icons/fa';

import { backendFetch } from './backend.js';

export default function Ask({ onBack = () => {} }) {
  const [question, setQuestion] = useState('');
  const [message, setMessage] = useState('');
  const [questionId, setQuestionId] = useState(null);
  const [smoothResult, setSmoothResult] = useState('');
  const [showSmooth, setShowSmooth] = useState(false);
  const [smoothDisabled, setSmoothDisabled] = useState(false);
  const [showRecord, setShowRecord] = useState(false);
  const [recordLength, setRecordLength] = useState(null);
  const [showLength, setShowLength] = useState(false);
  const mediaRecorderRef = useRef(null);
  const startTimeRef = useRef(null);
  const chunksRef = useRef([]);
  const userInfo = useSelector((state) => state.user.userInfo);

  async function handleAsk() {
    setMessage('');
    try {
      const params = new URLSearchParams({
        fromUserId: userInfo.id,
        question,
      });
      const resp = await backendFetch('/question', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params,
      });
      if (!resp.ok) {
        throw new Error('failed');
      }
      const id = await resp.json();
      setQuestionId(id);
      setMessage('Question added');
    } catch (err) {
      setMessage('Failed to add question');
    }
  }

  async function handleEdit() {
    setMessage('');
    try {
      const params = new URLSearchParams({ id: questionId, question });
      const resp = await backendFetch('/question', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params,
      });
      if (!resp.ok) {
        throw new Error('failed');
      }
      setMessage('Question updated');
    } catch (err) {
      setMessage('Failed to update question');
    }
  }

  async function handleSmooth() {
    if (smoothDisabled) {
      return;
    }
    setSmoothDisabled(true);
    setTimeout(() => setSmoothDisabled(false), 5000);
    try {
      const resp = await backendFetch('/ai/smooth', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        body: question,
      });
      if (!resp.ok) {
        throw new Error('failed');
      }
      const text = await resp.text();
      setSmoothResult(text);
      setShowSmooth(true);
    } catch (err) {
      // ignore errors
    }
  }

  async function handleRecord() {
    setShowRecord(true);
    setRecordLength(null);
    setShowLength(false);
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      return;
    }
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const rec = new MediaRecorder(stream);
      mediaRecorderRef.current = rec;
      startTimeRef.current = Date.now();
      chunksRef.current = [];
      rec.addEventListener('dataavailable', (e) => {
        if (e.data && e.data.size > 0) {
          chunksRef.current.push(e.data);
        }
      });
      rec.start();
    } catch (err) {
      // ignore errors
    }
  }

  function handleStopRecord() {
    setShowRecord(false);
    if (mediaRecorderRef.current) {
      const rec = mediaRecorderRef.current;
      rec.stop();
      if (rec.stream) {
        rec.stream.getTracks().forEach((t) => t.stop());
      }
      const len = Math.round((Date.now() - startTimeRef.current) / 1000);
      setRecordLength(len);
      mediaRecorderRef.current = null;
      startTimeRef.current = null;

      const blob = new Blob(chunksRef.current, { type: 'audio/webm' });
      chunksRef.current = [];
      const fd = new FormData();
      fd.append('file', blob, 'record.webm');
      backendFetch('/ai/transcribe', { method: 'POST', body: fd })
        .then((r) => (r.ok ? r.text() : Promise.resolve('')))
        .then((text) => {
          if (text) {
            setQuestion((prev) => (prev ? `${prev} ${text}` : text));
          }
        })
        .catch(() => {});
    }
    setShowLength(true);
  }

  return (
    <div className="ask-form" data-testid="ask-form">
      <div className="view-header">
        <h1>Ask</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <textarea
        className="ask-textarea"
        data-testid="ask-textarea"
        rows={5}
        value={question}
        onChange={(e) => setQuestion(e.target.value)}
      />
      <button type="button" onClick={questionId ? handleEdit : handleAsk}>
        {questionId ? 'Update' : 'Ask'}
      </button>
      <button
        type="button"
        onClick={handleSmooth}
        disabled={smoothDisabled}
      >
        Smooth
      </button>
      <button
        type="button"
        className="mic-button"
        aria-label="Record"
        onClick={handleRecord}
      >
        <FaMicrophone />
      </button>
      {message && <p>{message}</p>}
      <footer className="view-footer">
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
      {showSmooth && (
        <div className="popup-overlay" data-testid="smooth-popup">
          <div className="popup-window">
            <textarea readOnly rows={5} value={smoothResult} />
            <div className="popup-buttons">
              <button
                type="button"
                onClick={() => {
                  setQuestion(smoothResult);
                  setShowSmooth(false);
                }}
              >
                Apply
              </button>
              <button type="button" onClick={() => setShowSmooth(false)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
      {showRecord && (
        <div className="popup-overlay" data-testid="record-popup">
          <div className="popup-window">
            <p>Recording started...</p>
            <div className="popup-buttons">
              <button type="button" onClick={handleStopRecord}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}
      {showLength && (
        <div className="popup-overlay" data-testid="length-popup">
          <div className="popup-window">
            <p>{`Recording length: ${recordLength}s`}</p>
            <div className="popup-buttons">
              <button type="button" onClick={() => setShowLength(false)}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

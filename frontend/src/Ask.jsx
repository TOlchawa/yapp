import React, { useState } from 'react';
import { useSelector } from 'react-redux';

import { backendFetch } from './backend.js';

export default function Ask({ onBack = () => {} }) {
  const [question, setQuestion] = useState('');
  const [message, setMessage] = useState('');
  const [questionId, setQuestionId] = useState(null);
  const [smoothResult, setSmoothResult] = useState('');
  const [showSmooth, setShowSmooth] = useState(false);
  const [smoothDisabled, setSmoothDisabled] = useState(false);
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
    </div>
  );
}

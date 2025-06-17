import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { BACKEND_URL } from './config.js';

export default function Ask({ onBack = () => {} }) {
  const [question, setQuestion] = useState('');
  const [message, setMessage] = useState('');
  const [questionId, setQuestionId] = useState(null);
  const userInfo = useSelector((state) => state.user.userInfo);

  async function handleAsk() {
    setMessage('');
    try {
      const params = new URLSearchParams({
        fromUserId: userInfo.id,
        question,
      });
      const resp = await fetch(`${BACKEND_URL}/question`, {
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
      const resp = await fetch(`${BACKEND_URL}/question`, {
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
      {message && <p>{message}</p>}
      <footer className="view-footer">
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
    </div>
  );
}

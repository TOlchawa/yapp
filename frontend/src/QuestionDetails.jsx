import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';
import { backendFetch } from './backend.js';

export default function QuestionDetails({ id, onBack = () => {} }) {
  const [question, setQuestion] = useState(null);
  const [answers, setAnswers] = useState([]);
  const userInfo = useSelector((state) => state.user.userInfo);
  const [showPopup, setShowPopup] = useState(false);
  const [answerText, setAnswerText] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [showAnswerPopup, setShowAnswerPopup] = useState(false);
  const [selectedAnswer, setSelectedAnswer] = useState(null);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      try {
        const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
        const resp = await fetch(`${BACKEND_URL}/question/detail?id=${id}`, {
          headers: { Authorization: `Basic ${token}` },
        });
        if (!cancelled && resp.ok) {
          const data = await resp.json();
          setQuestion(data);
        }
      } catch (err) {
        // ignore
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [id]);

  async function handleAddAnswer() {
    if (!userInfo) {
      return;
    }
    setErrorMessage('');
    try {
      const params = new URLSearchParams({
        questionId: id,
        fromUserId: userInfo.id,
        text: answerText,
      });
      const resp = await backendFetch('/answer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params,
      });
      if (!resp.ok) {
        throw new Error('failed');
      }
      const newId = await resp.json();
      setAnswers((prev) => [...prev, { id: newId, text: answerText }]);
      setAnswerText('');
      setShowPopup(false);
    } catch (err) {
      setErrorMessage('Failed to add answer');
    }
  }

  useEffect(() => {
    let cancelled = false;
    async function load() {
      try {
        const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
        const resp = await fetch(`${BACKEND_URL}/answer?questionId=${id}`, {
          headers: { Authorization: `Basic ${token}` },
        });
        if (!cancelled && resp.ok) {
          const data = await resp.json();
          setAnswers(data);
        }
      } catch (err) {
        // ignore
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [id]);

  return (
    <div>
      <div className="view-header">
        <h1>Question details</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      {question && <p>{question.question}</p>}
      <ul>
        {answers.map((a, idx) => (
          <li key={a.id}>
            <button
              type="button"
              onClick={() => {
                setSelectedAnswer(a);
                setShowAnswerPopup(true);
              }}
            >
              {`Answer ${idx + 1}`}
            </button>
          </li>
        ))}
      </ul>
      <footer className="view-footer">
        <button type="button" onClick={() => setShowPopup(true)}>
          Answer question
        </button>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
      {showPopup && (
        <div className="popup-overlay" data-testid="answer-popup">
          <div className="popup-window">
            <textarea
              rows={5}
              value={answerText}
              onChange={(e) => setAnswerText(e.target.value)}
            />
            {errorMessage && <p>{errorMessage}</p>}
            <div className="popup-buttons">
              <button type="button" onClick={handleAddAnswer}>
                Submit
              </button>
              <button type="button" onClick={() => setShowPopup(false)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
      {showAnswerPopup && selectedAnswer && (
        <div className="popup-overlay" data-testid="show-answer-popup">
          <div className="popup-window">
            <p>{selectedAnswer.text}</p>
            <div className="popup-buttons">
              <button type="button" onClick={() => setShowAnswerPopup(false)}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

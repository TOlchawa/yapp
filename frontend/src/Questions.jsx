import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';
import QuestionDetails from './QuestionDetails.jsx';
import { getDisplaySummary } from './summaryUtils.js';

export default function Questions({ onBack = () => {} }) {
  const userInfo = useSelector((state) => state.user.userInfo);
  const [questions, setQuestions] = useState([]);
  const [selectedId, setSelectedId] = useState(null);

  useEffect(() => {
    if (!userInfo) {
      return;
    }
    let cancelled = false;
    async function load() {
      try {
        const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
        const headers = { Authorization: `Basic ${token}` };
        const resp = await fetch(`${BACKEND_URL}/question/ids/all`, {
          headers,
        });
        if (!cancelled && resp.ok) {
          const ids = await resp.json();
          setQuestions(ids.map((id) => ({ id, description: id })));
        }
      } catch (err) {
        // ignore errors
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [userInfo]);

  if (selectedId) {
    return (
      <QuestionDetails id={selectedId} onBack={() => setSelectedId(null)} />
    );
  }

  return (
    <div className="questions-view">
      <div className="view-header">
        <h1>Questions</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <ul className="questions-list">
        {questions.map((q) => (
          <li key={q.id} className="question-item">
            <button
              type="button"
              className="item-button"
              onClick={() => setSelectedId(q.id)}
            >
              {getDisplaySummary(q)}
            </button>
          </li>
        ))}
      </ul>
      <footer className="view-footer">
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
    </div>
  );
}

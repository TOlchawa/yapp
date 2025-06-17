import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';
import QuestionDetails from './QuestionDetails.jsx';

function truncate(str, max = 128) {
  if (str.length <= max) {
    return str;
  }
  return `${str.slice(0, max - 3)}...`;
}

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
        const resp = await fetch(
          `${BACKEND_URL}/question?userId=${userInfo.id}`,
          { headers: { Authorization: `Basic ${token}` } }
        );
        if (!cancelled && resp.ok) {
          const data = await resp.json();
          setQuestions(data);
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
    <div>
      <div className="view-header">
        <h1>Questions</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <ul>
        {questions.map((q) => (
          <li key={q.id} className="question-item">
            <button
              type="button"
              className="item-button"
              onClick={() => setSelectedId(q.id)}
            >
              {truncate(q.description)}
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

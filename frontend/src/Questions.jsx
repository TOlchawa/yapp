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
        const headers = { Authorization: `Basic ${token}` };
        const resp = await fetch(
          `${BACKEND_URL}/question/ids?userId=${userInfo.id}`,
          { headers }
        );
        if (!cancelled && resp.ok) {
          const ids = await resp.json();
          setQuestions(ids.map((id) => ({ id, description: id })));
          for (const id of ids) {
            if (cancelled) {
              break;
            }
            const qResp = await fetch(
              `${BACKEND_URL}/question/detail?id=${id}`,
              { headers }
            );
            if (!cancelled && qResp.ok) {
              const data = await qResp.json();
              setQuestions((prev) =>
                prev.map((q) =>
                  q.id === id ? { id, description: data.question } : q
                )
              );
            }
          }
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

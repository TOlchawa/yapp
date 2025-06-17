import React, { useState, useEffect } from 'react';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

export default function QuestionDetails({ id, onBack = () => {} }) {
  const [question, setQuestion] = useState(null);
  const [answers, setAnswers] = useState([]);

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
        {answers.map((a) => (
          <li key={a.id}>{a.text}</li>
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

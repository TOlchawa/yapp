import React from 'react';

const sampleQuestions = [
  'How can we optimize the database queries for better performance?',
  'What is the expected release date for version 2.0 of our product?',
  'Which new frameworks should we evaluate for the frontend redesign?',
];

function truncate(str, max) {
  if (str.length <= max) {
    return str;
  }
  return `${str.slice(0, max - 3)}...`;
}

export default function Questions({ onBack = () => {} }) {
  const maxChars = Math.floor(window.innerWidth / 10);
  return (
    <div>
      <div className="view-header">
        <h1>Questions</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <ul>
        {sampleQuestions.map((q, i) => (
          <li key={i} className="question-item">
            {truncate(q, maxChars)}
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

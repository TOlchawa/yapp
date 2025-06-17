import React, { useState } from 'react';

export default function Ask({ onBack = () => {} }) {
  const [question, setQuestion] = useState('');

  function handleAsk() {
    // Add API call or other logic here later
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
      <button type="button" onClick={handleAsk}>
        Ask
      </button>
    </div>
  );
}

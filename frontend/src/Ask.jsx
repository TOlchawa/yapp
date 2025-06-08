import React, { useState } from 'react';

export default function Ask({ onBack = () => {} }) {
  const [question, setQuestion] = useState('');

  function handleAsk() {
    // Add API call or other logic here later
  }
  return (
    <div style={{ position: 'relative' }}>
      <button className="back-button" onClick={onBack}>
        Back
      </button>
      <h1>Ask</h1>
      <textarea
        data-testid="ask-textarea"
        rows={5}
        value={question}
        onChange={(e) => setQuestion(e.target.value)}
      />
      <button onClick={handleAsk}>Ask</button>
    </div>
  );
}

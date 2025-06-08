import React, { useState } from 'react';

export default function Ask() {
  const [question, setQuestion] = useState('');

  function handleAsk() {
    // Add API call or other logic here later
  }

  return (
    <div>
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

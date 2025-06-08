import React from 'react';

export default function Questions({ onBack = () => {} }) {
  return (
    <div style={{ position: 'relative' }}>
      <button className="back-button" onClick={onBack}>
        Back
      </button>
      <h1>Questions</h1>
    </div>
  );
}

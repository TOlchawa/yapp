import React from 'react';

export default function Ask({ onBack = () => {} }) {
  return (
    <div style={{ position: 'relative' }}>
      <button className="back-button" onClick={onBack}>
        Back
      </button>
      <h1>Ask</h1>
    </div>
  );
}

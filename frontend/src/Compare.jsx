import React from 'react';

export default function Compare({ onBack = () => {} }) {
  return (
    <div style={{ position: 'relative' }}>
      <button className="back-button" onClick={onBack}>
        Back
      </button>
      <h1>Compare</h1>
    </div>
  );
}

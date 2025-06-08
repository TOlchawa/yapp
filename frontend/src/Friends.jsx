import React from 'react';

export default function Friends({ onBack = () => {} }) {
  return (
    <div style={{ position: 'relative' }}>
      <button className="back-button" onClick={onBack}>
        Back
      </button>
      <h1>Friends</h1>
    </div>
  );
}

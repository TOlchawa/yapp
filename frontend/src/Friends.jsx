import React from 'react';

export default function Friends({ onBack = () => {} }) {
  return (
    <div>
      <div className="view-header">
        <h1>Friends</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
    </div>
  );
}

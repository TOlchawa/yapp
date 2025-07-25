import React from 'react';

export default function Compare({ onBack = () => {} }) {
  return (
    <div>
      <div className="view-header">
        <h1>Compare</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <footer className="view-footer">
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
    </div>
  );
}

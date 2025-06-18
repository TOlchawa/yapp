import React, { useEffect, useState } from 'react';
import { backendFetch } from './backend.js';

export default function Logs({ onBack = () => {} }) {
  const [text, setText] = useState('');

  useEffect(() => {
    let cancelled = false;
    async function load() {
      try {
        const resp = await backendFetch('/logs');
        if (!cancelled && resp.ok) {
          const data = await resp.text();
          setText(data);
        }
      } catch (err) {
        // ignore errors
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="logs-view">
      <div className="view-header">
        <h1>Logs</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <textarea className="logs-textarea" readOnly value={text} />
      <footer className="view-footer">
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
    </div>
  );
}

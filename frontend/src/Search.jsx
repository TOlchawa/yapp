import React, { useState } from 'react';
import { BACKEND_URL } from './config.js';

export default function Search({ onBack = () => {} }) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);

  async function handleSearch() {
    const tags = query
      .split(',')
      .map((t) => t.trim())
      .filter((t) => t);
    try {
      const response = await fetch(`${BACKEND_URL}/items/tags`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ tags, matchAll: false }),
      });
      if (response.ok) {
        const data = await response.json();
        setResults(data);
      }
    } catch (err) {
      // Ignore errors for now
    }
  }

  return (
    <div>
      <div className="view-header">
        <h1>Search</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <input
        data-testid="search-input"
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />
      <button type="button" onClick={handleSearch}>
        Search
      </button>
      <ul>
        {results.map((item) => (
          <li key={item.id}>{item.name}</li>
        ))}
      </ul>
    </div>
  );
}

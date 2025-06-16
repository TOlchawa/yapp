import React, { useState, useEffect } from 'react';
import { BACKEND_URL } from './config.js';

export default function Search({ onBack = () => {} }) {
  const [itemIds, setItemIds] = useState([]);

  useEffect(() => {
    async function fetchItems() {
      try {
        const response = await fetch(`${BACKEND_URL}/items/user`, {
          method: 'POST',
        });
        if (response.ok) {
          const data = await response.json();
          setItemIds(data);
        }
      } catch (err) {
        // Ignore errors for now
      }
    }
    fetchItems();
  }, []);

  return (
    <div>
      <div className="view-header">
        <h1>Search</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <ul>
        {itemIds.map((id) => (
          <li key={id}>{id}</li>
        ))}
      </ul>
    </div>
  );
}

import React, { useState, useEffect } from 'react';
import { FaBarcode } from 'react-icons/fa';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';
import ItemDetails from './ItemDetails.jsx';

export default function Search({ onBack = () => {} }) {
  const [itemIds, setItemIds] = useState([]);
  const [details, setDetails] = useState({});
  const [selectedId, setSelectedId] = useState(null);

  useEffect(() => {
    async function fetchItems() {
      try {
        const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
        const response = await fetch(`${BACKEND_URL}/items/user`, {
          method: 'POST',
          headers: { Authorization: `Basic ${token}` },
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

  useEffect(() => {
    if (!selectedId || details[selectedId]) {
      return;
    }
    let cancelled = false;
    async function fetchDetail() {
      try {
        const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
        const resp = await fetch(`${BACKEND_URL}/item?id=${selectedId}`, {
          headers: { Authorization: `Basic ${token}` },
        });
        if (!cancelled && resp.ok) {
          const data = await resp.json();
          setDetails((prev) => ({ ...prev, [selectedId]: data }));
        }
      } catch (err) {
        // Ignore errors
      }
    }
    fetchDetail();
    return () => {
      cancelled = true;
    };
  }, [selectedId]);

  useEffect(() => {
    if (itemIds.length === 0) {
      return;
    }
    let cancelled = false;
    async function fetchDetailsSequential() {
      for (const id of itemIds) {
        if (cancelled || details[id]) {
          continue;
        }
        try {
          const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
          const resp = await fetch(`${BACKEND_URL}/item?id=${id}`, {
            headers: { Authorization: `Basic ${token}` },
          });
          if (!cancelled && resp.ok) {
            const data = await resp.json();
            setDetails((prev) => ({ ...prev, [id]: data }));
          }
        } catch (err) {
          // Ignore errors
        }
      }
    }
    fetchDetailsSequential();
    return () => {
      cancelled = true;
    };
  }, [itemIds]);

  if (selectedId) {
    return (
      <ItemDetails
        item={details[selectedId]}
        onBack={() => setSelectedId(null)}
      />
    );
  }

  return (
    <div>
      <div className="view-header">
        <h1>Search</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <ul>
        {itemIds.map((id) => {
          const info = details[id];
          const name = info && info.name ? info.name : id;
          const hasBarcode =
            info && info.description && info.description.barcode;
          return (
            <li key={id} onClick={() => setSelectedId(id)}>
              {name}
              {hasBarcode && <FaBarcode aria-label="barcode" />}
            </li>
          );
        })}
      </ul>
    </div>
  );
}

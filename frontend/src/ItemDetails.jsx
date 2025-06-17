import React, { useState, useEffect } from 'react';
import { FaBarcode } from 'react-icons/fa';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

export default function ItemDetails({ id, info, onBack = () => {}, onUpdate = () => {} }) {
  const [data, setData] = useState(info || null);

  useEffect(() => {
    if (data) return;
    async function load() {
      try {
        const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
        const resp = await fetch(`${BACKEND_URL}/item?id=${id}`, {
          headers: { Authorization: `Basic ${token}` },
        });
        if (resp.ok) {
          const result = await resp.json();
          setData(result);
          onUpdate(result);
        }
      } catch (err) {
        // ignore errors
      }
    }
    load();
  }, [id]);

  const name = data && data.name ? data.name : id;
  const barcode = data && data.description && data.description.barcode;

  return (
    <div>
      <div className="view-header">
        <h1>Item details</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <div>
        <p>{name}</p>
        {barcode && (
          <p>
            <FaBarcode aria-label="barcode" /> {barcode}
          </p>
        )}
      </div>
      <footer className="view-footer">
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
    </div>
  );
}

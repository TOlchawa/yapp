import React, { useState, useEffect } from 'react';
import { FaBarcode } from 'react-icons/fa';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

function ItemImage({ picture }) {
  const [src, setSrc] = useState(() => {
    if (!picture || !picture.picture) return null;
    const pic = picture.picture;
    return pic.startsWith('data:') ? pic : `data:image/jpeg;base64,${pic}`;
  });

  useEffect(() => {
    if (!picture || src || !picture.id) return;
    let cancelled = false;
    async function load() {
      try {
        const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
        const resp = await fetch(`${BACKEND_URL}/data?id=${picture.id}`, {
          headers: { Authorization: `Basic ${token}` },
        });
        if (!cancelled && resp.ok) {
          const array = await resp.arrayBuffer();
          const base64 = btoa(String.fromCharCode(...new Uint8Array(array)));
          setSrc(`data:image/jpeg;base64,${base64}`);
        }
      } catch (err) {
        // ignore errors
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [picture, src]);

  if (!src) return null;
  return <img src={src} alt="Item" className="item-picture" />;
}

export default function ItemDetails({
  id,
  info,
  onBack = () => {},
  onUpdate = () => {},
  onModify = () => {},
  onDelete = () => {},
}) {
  const [data, setData] = useState(info || null);

  useEffect(() => {
    if (data) return;
    let cancelled = false;
    async function load() {
      try {
        const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
        const resp = await fetch(`${BACKEND_URL}/item/${id}`, {
          headers: { Authorization: `Basic ${token}` },
        });
        if (!cancelled && resp.ok) {
          const result = await resp.json();
          setData(result);
          onUpdate(result);
        }
      } catch (err) {
        // ignore errors
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [id]);

  const name = data && data.name ? data.name : id;
  const barcode = data && data.description && data.description.barcode;
  const note = data && data.description && data.description.note;
  const pictures =
    (data && data.description && data.description.pictures) || [];

  return (
    <div>
      <div className="view-header">
        <h1>Item details</h1>
        <div className="header-buttons">
          <button type="button" onClick={onModify}>
            Modify
          </button>
          <button type="button" className="back-button" onClick={onBack}>
            Back
          </button>
        </div>
      </div>
      <h2>{name}</h2>
      {barcode && (
        <p>
          <FaBarcode aria-label="barcode" /> {barcode}
        </p>
      )}
      {note && <p>{note}</p>}
      {pictures.map((pic, idx) => (
        <ItemImage key={pic.id || idx} picture={pic} />
      ))}
      <footer className="view-footer">
        <button type="button" onClick={onDelete}>
          Delete
        </button>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
    </div>
  );
}

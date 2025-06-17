import React, { useState, useEffect } from 'react';
import { FaBarcode } from 'react-icons/fa';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

export default function ItemDetails({ id, info, onBack = () => {}, onUpdate = () => {} }) {
  const [data, setData] = useState(info || null);

  useEffect(() => {
    if (data) return;
    let cancelled = false;
    async function load() {
      try {
        const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
        const resp = await fetch(`${BACKEND_URL}/item?id=${id}`, {
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
  const picture =
    data &&
    data.description &&
    data.description.pictures &&
    data.description.pictures[0];
  const [imgSrc, setImgSrc] = useState(null);

  useEffect(() => {
    if (!picture || imgSrc) return;
    if (picture.picture) {
      const picData = picture.picture;
      if (picData.startsWith('data:')) {
        setImgSrc(picData);
      } else {
        setImgSrc(`data:image/jpeg;base64,${picData}`);
      }
      return;
    }
    if (!picture.id) return;
    let cancelled = false;
    async function loadPicture() {
      try {
        const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
        const resp = await fetch(`${BACKEND_URL}/data?id=${picture.id}`, {
          headers: { Authorization: `Basic ${token}` },
        });
        if (!cancelled && resp.ok) {
          const array = await resp.arrayBuffer();
          const base64 = btoa(
            String.fromCharCode(...new Uint8Array(array))
          );
          setImgSrc(`data:image/jpeg;base64,${base64}`);
        }
      } catch (err) {
        // ignore errors
      }
    }
    loadPicture();
    return () => {
      cancelled = true;
    };
  }, [picture, imgSrc]);

  return (
    <div>
      <div className="view-header">
        <h1>Item details</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <h2>{name}</h2>
      {barcode && (
        <p>
          <FaBarcode aria-label="barcode" /> {barcode}
        </p>
      )}
      {note && <p>{note}</p>}
      {imgSrc && <img src={imgSrc} alt="Item" />}
      <footer className="view-footer">
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
    </div>
  );
}

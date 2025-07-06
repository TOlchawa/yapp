import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { backendFetch } from './backend.js';
import ItemDetails from './ItemDetails.jsx';

export default function Data({ onBack = () => {} }) {
  const userInfo = useSelector((state) => state.user.userInfo);
  const [ids, setIds] = useState([]);
  const [details, setDetails] = useState({});
  const [selectedId, setSelectedId] = useState(null);

  useEffect(() => {
    if (!userInfo) return;
    let cancelled = false;
    async function loadIds() {
      try {
        const resp = await backendFetch('/items/user', { method: 'POST' });
        if (!cancelled && resp.ok) {
          const data = await resp.json();
          setIds(data);
        }
      } catch {
        // ignore errors
      }
    }
    loadIds();
    return () => {
      cancelled = true;
    };
  }, [userInfo]);

  useEffect(() => {
    if (!selectedId || details[selectedId]) return;
    let cancelled = false;
    async function load() {
      try {
        const resp = await backendFetch(`/item/${selectedId}`);
        if (!cancelled && resp.ok) {
          const data = await resp.json();
          setDetails((prev) => ({ ...prev, [selectedId]: data }));
        }
      } catch {
        // ignore errors
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [selectedId]);

  if (selectedId) {
    return (
      <ItemDetails
        id={selectedId}
        info={details[selectedId]}
        onBack={() => setSelectedId(null)}
        onUpdate={(data) =>
          setDetails((prev) => ({ ...prev, [selectedId]: data }))
        }
      />
    );
  }

  return (
    <div className="data-view">
      <div className="view-header">
        <h1>Data</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <ul>
        {ids.map((id) => (
          <li key={id}>
            <button
              type="button"
              className="item-button"
              onClick={() => setSelectedId(id)}
            >
              {details[id] && details[id].name ? details[id].name : id}
            </button>
          </li>
        ))}
      </ul>
      <footer className="view-footer">
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
    </div>
  );
}

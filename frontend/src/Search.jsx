import React, { useState, useEffect } from 'react';
import { FaBarcode } from 'react-icons/fa';
import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';
import { getItemById } from './backend.js';
import ItemDetails from './ItemDetails.jsx';

function ItemThumbnail({ id, info, update }) {
  const picture =
    info &&
    info.description &&
    info.description.pictures &&
    info.description.pictures[0];
  const [src, setSrc] = useState(() => {
    if (picture && picture.picture) {
      const pic = picture.picture;
      return pic.startsWith('data:') ? pic : `data:image/jpeg;base64,${pic}`;
    }
    return null;
  });

  useEffect(() => {
    if (!picture || src || !picture.id) {
      return;
    }
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
          const dataUri = `data:image/jpeg;base64,${base64}`;
          setSrc(dataUri);
          update((prev) => {
            const item = prev[id];
            if (!item) return prev;
            const pic = item.description?.pictures?.[0];
            if (pic && !pic.picture) {
              const newPic = { ...pic, picture: base64 };
              const newDesc = { ...item.description, pictures: [newPic] };
              return { ...prev, [id]: { ...item, description: newDesc } };
            }
            return prev;
          });
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

  if (!src) {
    return null;
  }
  return <img src={src} alt="Item" className="item-thumbnail" />;
}

export default function Search({ onBack = () => {} }) {
  const [itemIds, setItemIds] = useState([]);
  const [details, setDetails] = useState({});
  const [selectedId, setSelectedId] = useState(null);

  async function handleDelete(id) {
    try {
      const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
      const resp = await fetch(`${BACKEND_URL}/item/${id}`, {
        method: 'DELETE',
        headers: { Authorization: `Basic ${token}` },
      });
      if (resp.ok) {
        setItemIds((prev) => prev.filter((itemId) => itemId !== id));
        setDetails((prev) => {
          const { [id]: _omit, ...rest } = prev;
          return rest;
        });
        setSelectedId(null);
      }
    } catch (err) {
      // Ignore errors
    }
  }

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
        const resp = await getItemById(selectedId);
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
          const resp = await getItemById(id);
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
        id={selectedId}
        info={details[selectedId]}
        onBack={() => setSelectedId(null)}
        onUpdate={(data) =>
          setDetails((prev) => ({ ...prev, [selectedId]: data }))
        }
        onDelete={() => handleDelete(selectedId)}
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
          const name = info ? info.name || 'noname' : id;
          const hasBarcode =
            info && info.description && info.description.barcode;
          return (
            <li key={id}>
              <button
                type="button"
                className="item-button"
                onClick={() => setSelectedId(id)}
              >
                <ItemThumbnail id={id} info={info} update={setDetails} />
                <span className="item-name">{name}</span>
                {hasBarcode && (
                  <FaBarcode className="barcode-icon" aria-label="barcode" />
                )}
              </button>
            </li>
          );
        })}
      </ul>
      <footer className="view-footer">
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
    </div>
  );
}

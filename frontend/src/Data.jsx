import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { backendFetch } from './backend.js';
import ItemDetails from './ItemDetails.jsx';
import QuestionDetails from './QuestionDetails.jsx';
import FriendDetails from './FriendDetails.jsx';

function detectFormat(data) {
  if (data instanceof Uint8Array) {
    const startsWith = (arr) => arr.every((b, i) => data[i] === b);
    if (startsWith([0xff, 0xd8, 0xff])) return 'JPEG';
    if (startsWith([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]))
      return 'PNG';
    if (
      startsWith([0x47, 0x49, 0x46, 0x38, 0x37, 0x61]) ||
      startsWith([0x47, 0x49, 0x46, 0x38, 0x39, 0x61])
    )
      return 'GIF';
    if (
      startsWith([0x52, 0x49, 0x46, 0x46]) &&
      data[8] === 0x57 &&
      data[9] === 0x45 &&
      data[10] === 0x42 &&
      data[11] === 0x50
    )
      return 'WEBP';
    return 'Binary';
  }
  const text = data;
  if (text.startsWith('\xFF\xD8\xFF')) {
    return 'JPEG';
  }
  if (text.startsWith('\x89PNG\r\n\x1A\n')) {
    return 'PNG';
  }
  if (text.startsWith('GIF87a') || text.startsWith('GIF89a')) {
    return 'GIF';
  }
  if (text.startsWith('RIFF') && text.slice(8, 12) === 'WEBP') {
    return 'WEBP';
  }
  try {
    JSON.parse(text);
    return 'JSON';
  } catch {
    /* ignore */
  }
  try {
    const parser = new DOMParser();
    const xml = parser.parseFromString(text, 'application/xml');
    if (!xml.querySelector('parsererror')) {
      return 'XML';
    }
  } catch {
    /* ignore */
  }
  if (/^[\s-]*[\w"']+:/.test(text)) {
    return 'YAML';
  }
  if (/<html/i.test(text)) {
    return 'HTML';
  }
  return 'Plain';
}

export default function Data({ onBack = () => {} }) {
  const userInfo = useSelector((state) => state.user.userInfo);
  const [ids, setIds] = useState([]);
  const [details, setDetails] = useState({});
  const [selectedId, setSelectedId] = useState(null);
  const [collection, setCollection] = useState('items');
  const [redisData, setRedisData] = useState(null);
  const [redisFormat, setRedisFormat] = useState(null);

  useEffect(() => {
    setIds([]);
    setDetails({});
    setSelectedId(null);
    setRedisData(null);
    setRedisFormat(null);
  }, [collection]);

  useEffect(() => {
    if (!userInfo) return;
    let cancelled = false;
    async function loadIds() {
      try {
        let resp;
        if (collection === 'items') {
          resp = await backendFetch('/items/user', { method: 'POST' });
        } else if (collection === 'questions') {
          resp = await backendFetch('/question/ids/all');
        } else if (collection === 'friends') {
          resp = await backendFetch(`/friend?userId=${userInfo.id}`);
        } else if (collection === 'redis') {
          resp = await backendFetch('/data/ids');
        }
        if (resp && !cancelled && resp.ok) {
          const data = await resp.json();
          if (collection === 'friends') {
            setIds(data.map((r) => r.id));
            const map = {};
            for (const r of data) {
              map[r.id] = r;
            }
            setDetails(map);
          } else if (collection === 'redis') {
            console.debug('Loaded Redis IDs', data);
            setIds(data);
            setRedisData(null);
          } else {
            setIds(data);
          }
        }
      } catch {
        // ignore errors
      }
    }
    loadIds();
    return () => {
      cancelled = true;
    };
  }, [userInfo, collection]);

  useEffect(() => {
    if (!selectedId) return;
    if (collection === 'friends') return;
    if (collection !== 'redis' && details[selectedId]) return;
    let cancelled = false;
    async function load() {
      try {
        let resp;
        if (collection === 'items') {
          resp = await backendFetch(`/item/${selectedId}`);
        } else if (collection === 'questions') {
          resp = await backendFetch(`/question/detail?id=${selectedId}`);
        } else if (collection === 'redis') {
          resp = await backendFetch(`/data?id=${selectedId}`);
        }
        if (resp && !cancelled && resp.ok) {
          if (collection === 'redis') {
            const type =
              resp.headers && resp.headers.get
                ? resp.headers.get('X-Data-Type')
                : null;
            if (type === 'picture') {
              const array = await resp.arrayBuffer();
              const bytes = new Uint8Array(array);
              const base64 = btoa(String.fromCharCode(...bytes));
              console.debug('Loaded Redis binary for', selectedId);
              setRedisData(base64);
              setRedisFormat(detectFormat(bytes));
            } else {
              const text = await resp.text();
              console.debug('Loaded Redis data for', selectedId);
              setRedisData(text);
              setRedisFormat(detectFormat(text));
            }
          } else {
            const data = await resp.json();
            setDetails((prev) => ({ ...prev, [selectedId]: data }));
          }
        }
      } catch {
        // ignore errors
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [selectedId, collection]);

  if (selectedId) {
    if (collection === 'items') {
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
    if (collection === 'questions') {
      return (
        <QuestionDetails id={selectedId} onBack={() => setSelectedId(null)} />
      );
    }
    if (collection === 'friends') {
      return (
        <FriendDetails
          relation={details[selectedId]}
          onBack={() => setSelectedId(null)}
        />
      );
    }
    if (collection === 'redis') {
      return (
        <div className="redis-view">
          <div className="redis-format-label">{redisFormat || 'Unknown'}</div>
          <textarea
            className="redis-textarea"
            readOnly
            value={redisData || ''}
          />
          <footer className="view-footer">
            <button
              type="button"
              className="back-button"
              onClick={() => setSelectedId(null)}
            >
              Back
            </button>
          </footer>
        </div>
      );
    }
  }

  return (
    <div className="data-view">
      <div className="view-header">
        <h1>Data</h1>
        <select
          value={collection}
          onChange={(e) => setCollection(e.target.value)}
          aria-label="collection selector"
        >
          <option value="items">Items</option>
          <option value="questions">Questions</option>
          <option value="friends">Friends</option>
          <option value="redis">Redis</option>
        </select>
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
              {collection === 'friends'
                ? `${details[id].friendId} - ${details[id].type}`
                : details[id] && details[id].name
                  ? details[id].name
                  : id}
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

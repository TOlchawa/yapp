import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { backendFetch } from './backend.js';
import ItemDetails from './ItemDetails.jsx';
import QuestionDetails from './QuestionDetails.jsx';
import FriendDetails from './FriendDetails.jsx';

export default function Data({ onBack = () => {} }) {
  const userInfo = useSelector((state) => state.user.userInfo);
  const [ids, setIds] = useState([]);
  const [details, setDetails] = useState({});
  const [selectedId, setSelectedId] = useState(null);
  const [collection, setCollection] = useState('items');
  const [redisData, setRedisData] = useState(null);

  useEffect(() => {
    setIds([]);
    setDetails({});
    setSelectedId(null);
    setRedisData(null);
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
            const array = await resp.arrayBuffer();
            const base64 = btoa(String.fromCharCode(...new Uint8Array(array)));
            const img = `data:image/jpeg;base64,${base64}`;
            console.debug('Loaded Redis data for', selectedId);
            setRedisData(img);
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

  useEffect(() => {
    if (redisData) {
      console.debug('Displaying Redis image');
    }
  }, [redisData]);

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
        <div>
          {redisData && <img src={redisData} alt="redis item" />}
          <footer className="view-footer">
            <button type="button" className="back-button" onClick={() => setSelectedId(null)}>
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

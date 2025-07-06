import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { getFriends } from './backend.js';

export default function Friends({ onBack = () => {} }) {
  const userInfo = useSelector((state) => state.user.userInfo);
  const [friends, setFriends] = useState([]);

  useEffect(() => {
    if (!userInfo) return;
    let cancelled = false;
    async function load() {
      try {
        const resp = await getFriends(userInfo.id);
        if (!cancelled && resp.ok) {
          const data = await resp.json();
          setFriends(data);
        }
      } catch {
        // ignore errors
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [userInfo]);

  return (
    <div>
      <div className="view-header">
        <h1>Friends</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <ul>
        {friends.map((f) => (
          <li key={f.id}>{`${f.friendId} - ${f.type}`}</li>
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

import React from 'react';

export default function FriendDetails({ relation, onBack = () => {} }) {
  if (!relation) return null;
  return (
    <div className="friend-details-view">
      <div className="view-header">
        <h1>Friend relation</h1>
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </div>
      <p>{`Friend ID: ${relation.friendId}`}</p>
      <p>{`Type: ${relation.type}`}</p>
      <footer className="view-footer">
        <button type="button" className="back-button" onClick={onBack}>
          Back
        </button>
      </footer>
    </div>
  );
}

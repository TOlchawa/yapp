import React, { useState } from 'react';

import AddView from './AddView.jsx';
import Compare from './Compare.jsx';
import Ask from './Ask.jsx';
import Questions from './Questions.jsx';
import Friends from './Friends.jsx';

import { BACKEND_URL } from './config.js';

export default function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [userInfo, setUserInfo] = useState(null);
  const [currentView, setCurrentView] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setMessage('');
    setUserInfo(null);
    try {
      const response = await fetch(`${BACKEND_URL}/user`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });
      if (!response.ok) {
        throw new Error('login failed');
      }
      const data = await response.json();
      setMessage(`Welcome ${data.user.nickname}`);
      setUserInfo(data.user);
    } catch (err) {
      setMessage('Login error');
    }
  }

  return (
    <form className="login-form" onSubmit={handleSubmit}>
      <label>
        Email
        <input
          data-testid="email-input"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
      </label>
      <label>
        Password
        <input
          data-testid="password-input"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </label>
      <button type="submit">Login</button>
      {message && <p>{message}</p>}
      {userInfo && (
        <div>
          <h2>User Info</h2>
          <ul>
            <li>Nickname: {userInfo.nickname}</li>
            <li>Email: {userInfo.email}</li>
            <li>ID: {userInfo.id}</li>
          </ul>
          <div>
            <button onClick={() => setCurrentView('add')}>Add</button>
          </div>
          <div>
            <button onClick={() => setCurrentView('compare')}>Compare</button>
          </div>
          <div>
            <button onClick={() => setCurrentView('ask')}>Ask</button>
          </div>
          <div>
            <button onClick={() => setCurrentView('questions')}>Questions</button>
          </div>
          <div>
            <button onClick={() => setCurrentView('friends')}>Friends</button>
          </div>
          {currentView === 'add' && <AddView />}
          {currentView === 'compare' && <Compare />}
          {currentView === 'ask' && <Ask />}
          {currentView === 'questions' && <Questions />}
          {currentView === 'friends' && <Friends />}
        </div>
      )}
    </form>
  );
}

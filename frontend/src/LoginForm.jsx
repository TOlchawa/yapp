import React, { useState } from 'react';

import { BACKEND_URL } from './config.js';

export default function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [userInfo, setUserInfo] = useState(null);

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
            <button>Add</button>
          </div>
          <div>
            <button>Check</button>
          </div>
          <div>
            <button>Inspiration</button>
          </div>
        </div>
      )}
    </form>
  );
}

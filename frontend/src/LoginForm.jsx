import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { setUserInfo, setCurrentView } from './store.js';

import AddView from './AddView.jsx';
import Compare from './Compare.jsx';
import Ask from './Ask.jsx';
import Questions from './Questions.jsx';
import Friends from './Friends.jsx';

import { BACKEND_URL } from './config.js';

export default function LoginForm({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [remember, setRemember] = useState(false);
  const [message, setMessage] = useState('');
  const userInfo = useSelector((state) => state.user.userInfo);
  const currentView = useSelector((state) => state.user.currentView);
  const dispatch = useDispatch();

  useEffect(() => {
    const matchEmail = document.cookie.match(/(?:^|;)\s*email=([^;]+)/);
    const matchPassword = document.cookie.match(/(?:^|;)\s*password=([^;]+)/);
    if (matchEmail) {
      setEmail(decodeURIComponent(matchEmail[1]));
      setRemember(true);
    }
    if (matchPassword) {
      setPassword(decodeURIComponent(matchPassword[1]));
    }
  }, []);

  async function handleSubmit(e) {
    e.preventDefault();
    setMessage('');
    dispatch(setUserInfo(null));
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
      dispatch(setUserInfo(data.user));
      if (remember) {
        document.cookie = `email=${encodeURIComponent(email)}; path=/`;
        document.cookie = `password=${encodeURIComponent(password)}; path=/`;
      } else {
        document.cookie = 'email=; Max-Age=0; path=/';
        document.cookie = 'password=; Max-Age=0; path=/';
      }
      if (onLogin) {
        onLogin(data.user);
      }
    } catch (err) {
      setMessage('Login error');
    }
  }

  return (
    <form className="login-form" onSubmit={handleSubmit}>
      {!userInfo && (
        <>
          <h1>Login</h1>
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
          <label>
            <input
              data-testid="remember-checkbox"
              type="checkbox"
              checked={remember}
              onChange={(e) => setRemember(e.target.checked)}
            />
            Remember me
          </label>
          <button type="submit">Login</button>
          {message && !userInfo && <p>{message}</p>}
        </>
      )}
      {userInfo && (
        <div>
          {currentView === null && (
            <>
              <h1>Welcome</h1>
              <p data-testid="user-nickname">{userInfo.nickname}</p>
              <h2>User Info</h2>
              <ul>
                <li>Nickname: {userInfo.nickname}</li>
                <li>Email: {userInfo.email}</li>
                <li>ID: {userInfo.id}</li>
              </ul>
              <div>
                <button onClick={() => dispatch(setCurrentView('add'))}>Add</button>
              </div>
              <div>
                <button onClick={() => dispatch(setCurrentView('compare'))}>Compare</button>
              </div>
              <div>
                <button onClick={() => dispatch(setCurrentView('ask'))}>Ask</button>
              </div>
              <div>
                <button onClick={() => dispatch(setCurrentView('questions'))}>Questions</button>
              </div>
              <div>
                <button onClick={() => dispatch(setCurrentView('friends'))}>Friends</button>
              </div>
            </>
          )}
          {currentView === 'add' && (
            <AddView onBack={() => dispatch(setCurrentView(null))} />
          )}
          {currentView === 'compare' && (
            <Compare onBack={() => dispatch(setCurrentView(null))} />
          )}
          {currentView === 'ask' && (
            <Ask onBack={() => dispatch(setCurrentView(null))} />
          )}
          {currentView === 'questions' && (
            <Questions onBack={() => dispatch(setCurrentView(null))} />
          )}
          {currentView === 'friends' && (
            <Friends onBack={() => dispatch(setCurrentView(null))} />
          )}
        </div>
      )}
    </form>
  );
}

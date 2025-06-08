import React, { useState } from 'react';
import LoginForm from './LoginForm.jsx';

export default function App() {
  const [userInfo, setUserInfo] = useState(null);

  return (
    <div>
      <h1>{userInfo ? `Welcome ${userInfo.nickname}` : 'Login'}</h1>
      <LoginForm onLogin={setUserInfo} />
    </div>
  );
}

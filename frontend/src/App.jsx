import React, { useState } from 'react';
import LoginForm from './LoginForm.jsx';

export default function App() {
  const [userInfo, setUserInfo] = useState(null);

  return (
    <div>
      {userInfo ? (
        <>
          <h1>Welcome</h1>
          <p data-testid="user-nickname">{userInfo.nickname}</p>
        </>
      ) : (
        <h1>Login</h1>
      )}
      <LoginForm onLogin={setUserInfo} />
    </div>
  );
}

import React from 'react';
import { Provider } from 'react-redux';
import LoginForm from './LoginForm.jsx';
import store from './store.js';
import useDeviceClass from './hooks/useDeviceClass.js';

export default function App() {
  useDeviceClass();
  return (
    <Provider store={store}>
      <div>
        <LoginForm />
      </div>
    </Provider>
  );
}

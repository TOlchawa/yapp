import React from 'react';
import { Provider } from 'react-redux';
import LoginForm from './LoginForm.jsx';
import store from './store.js';

export default function App() {
  return (
    <Provider store={store}>
      <div>
        <LoginForm />
      </div>
    </Provider>
  );
}

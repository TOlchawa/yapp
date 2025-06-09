import { configureStore, createSlice } from '@reduxjs/toolkit';

const userSlice = createSlice({
  name: 'user',
  initialState: { userInfo: null, currentView: null },
  reducers: {
    setUserInfo: (state, action) => {
      state.userInfo = action.payload;
    },
    setCurrentView: (state, action) => {
      state.currentView = action.payload;
    },
  },
});

export const { setUserInfo, setCurrentView } = userSlice.actions;

export function createAppStore(preloadedState) {
  return configureStore({
    reducer: {
      user: userSlice.reducer,
    },
    preloadedState,
  });
}

const store = createAppStore();

export default store;

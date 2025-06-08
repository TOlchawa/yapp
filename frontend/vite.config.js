import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
          port: 19992,
          allowedHosts: ['memoritta.com'],
          host: true,
  },
  root: '.',
  test: {
    globals: true,
    environment: 'jsdom',
  },
});

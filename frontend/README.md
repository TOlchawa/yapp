# React Hello World

This module contains a simple React application built with Vite. It displays a **Hello, World!** message.

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   ```
2. Start the development server:
   ```bash
   npm run dev
   ```

Visit `http://localhost:5173` in your browser to see the running application.

### Configuration
Change the backend API address in `src/config.js` if your server runs on a different host or port.

### Barcode Scanning
This app ships with a custom React hook `useBarcodeScanner`. It uses the `@zxing/browser` library to read barcodes from the camera. The hook is not used yet but you can import it and render a `video` element with the provided `ref` when you want to scan codes.

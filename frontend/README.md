# Memoritta

This module contains the React frontend for Memoritta. It was built with Vite and displays a simple message.

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   ```
2. Start the development server:
   ```bash
   npm run dev
   ```

### Test Coverage
Run unit tests with coverage:
```bash
npm run test:coverage
```
The HTML report appears in the `coverage` folder.

Visit `http://localhost:5173` in your browser to see the running application.

### Camera Access
The camera works only in a secure context (HTTPS or `localhost`).
Make sure the browser has permission to use the camera.
If the video preview does not start, check the console for errors and
verify that no other application is using the device.

### Debug Output
The *Add* view now includes a debug text box. Messages about camera access and
errors appear here. This helps debugging on mobile devices without developer
tools.

### Configuration
Change the backend API address in `src/config.js` if your server runs on a different host or port.

### Login Cookies
The login form has a **Remember me** option. When checked, the app stores your
email and password in browser cookies so the inputs are pre-filled next time.
When both cookies exist, the form submits automatically on load.

### Barcode Scanning
This app ships with a custom React hook `useBarcodeScanner`. It uses the `@zxing/browser` library to read barcodes from the camera. The hook is not used yet but you can import it and render a `video` element with the provided `ref` when you want to scan codes.

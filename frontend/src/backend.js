import { BACKEND_URL, AUTH_EMAIL, AUTH_PASSWORD } from './config.js';

export function createAuthHeader() {
  const token = btoa(`${AUTH_EMAIL}:${AUTH_PASSWORD}`);
  return { Authorization: `Basic ${token}` };
}

export async function backendFetch(path, options = {}) {
  const headers = { ...createAuthHeader(), ...(options.headers || {}) };
  const opts = { ...options, headers };
  const response = await fetch(`${BACKEND_URL}${path}`, opts);
  return response;
}

// Fetch a single item using the path-based endpoint.
export function getItemById(id) {
  return backendFetch(`/item/${encodeURIComponent(id)}`);
}

// Fetch friend relations for the given user ID.
export function getFriends(userId) {
  return backendFetch(`/friend?userId=${encodeURIComponent(userId)}`);
}

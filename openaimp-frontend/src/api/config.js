
// src/api/config.js

/**
 * Load system configuration
 * @returns {Promise<{data: Array<{key: string, value: string}>}>}
 */
export const loadConfig = async () => {
  try {
    const response = await fetch('/api/config');
    if (!response.ok) {
        // If backend is not ready, return empty data to prevent crash if not 404
        if (response.status === 404) {
             console.warn('API endpoint /api/config not found. Returning empty config.');
             return { data: [] };
        }
        throw new Error(`Error loading config: ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    // Return empty structure to allow UI to render
    return { data: [] };
  }
};

/**
 * Update system configuration
 * @param {Array<{key: string, value: string}>} configData 
 * @returns {Promise<any>}
 */
export const updateConfig = async (configData) => {
  const response = await fetch('/api/config', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(configData),
  });
  
  if (!response.ok) {
    throw new Error(`Error updating config: ${response.statusText}`);
  }
  return await response.json();
};

/**
 * Reset the JWT secret key
 * @returns {Promise<any>}
 */
export const resetSecretKey = async () => {
  const response = await fetch('/api/config/reset-secret', {
    method: 'POST',
  });

  if (!response.ok) {
    throw new Error(`Error resetting secret key: ${response.statusText}`);
  }
  return await response.json();
};

/**
 * Create a new API token
 * @param {string} expiryDate - YYYY-MM-DD
 * @returns {Promise<{data: {token: string}}>}
 */
export const createApiToken = async (expiryDate) => {
  const response = await fetch('/api/tokens', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ expiryDate }),
  });

  if (!response.ok) {
    throw new Error(`Error creating token: ${response.statusText}`);
  }
  return await response.json();
};

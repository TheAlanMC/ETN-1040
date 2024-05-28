import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'bo.edu.umsa.app',
  appName: 'Laboratorio Multimedia',
  webDir: 'dist/frontend/browser',
  plugins: {
    "CapacitorHttp": {
      "enabled": true
    },
    "PushNotifications": {
      "presentationOptions": ["badge", "sound", "alert"]
    },
  },
  android: {
    "allowMixedContent": true
  },
};

export default config;

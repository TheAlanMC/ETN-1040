import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'bo.edu.umsa.app',
  appName: 'Laboratorio Multimedia',
  webDir: 'dist/frontend/browser',
  plugins: {
    "PushNotifications": {
      "presentationOptions": ["badge", "sound", "alert"],
    },
  },
};

export default config;

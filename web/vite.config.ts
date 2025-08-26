// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  // Remove or comment out this line to use Vite's default 'dist' output directory
  // build: {
  //   outDir: 'build'
  // }
});
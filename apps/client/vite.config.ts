import react from '@vitejs/plugin-react';
import dns from 'dns';
import { fileURLToPath, URL } from 'url';
import { defineConfig, loadEnv } from 'vite';

dns.setDefaultResultOrder('verbatim');

export default defineConfig(({ mode }) => {
    const rootProjectDir = fileURLToPath(new URL('..', import.meta.url));
    const variablePrefixes = [''];

    process.env = { ...loadEnv(mode, rootProjectDir, variablePrefixes) };

    return {
        plugins: [
            react(),
        ],
        resolve: {
            alias: {
                '@': fileURLToPath(new URL('src', import.meta.url)),
            },
        },
        server: {
            proxy: {
                '^/(api)': {
                    target: 'http://localhost:4000',
                    changeOrigin: true,
                },
            },
        },
        build: {
            rollupOptions: {
                output: {
                    manualChunks: {
                        react: [
                            'react',
                            'react-dom',
                            'react-router',
                            'react-router-dom',
                        ],
                        material: [
                            '@emotion/cache',
                            '@emotion/react',
                            '@emotion/styled',
                            '@mui/icons-material',
                            '@mui/material',
                        ],
                    },
                },
            },
        },
        test: {
            browser: {
                enabled: true,
                provider: 'playwright',
                instances: [
                    { browser: 'chromium' },
                ],
            },
        },
    }
});

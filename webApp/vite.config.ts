import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import {resolve} from 'path';
import { visualizer } from 'rollup-plugin-visualizer';

export default defineConfig(({ mode }) => ({
    root: '.',
    plugins: [
        react(),
        tailwindcss(),
        // Bundle analyzer - only in analyze mode
        mode === 'analyze' && visualizer({
            filename: 'dist/stats.html',
            open: true,
            gzipSize: true,
            brotliSize: true
        })
    ].filter(Boolean),
    build: {
        outDir: 'dist',
        emptyOutDir: true,
        // Bundle optimization
        target: 'es2020',
        minify: 'terser',
        sourcemap: true,
        rollupOptions: {
            output: {
                // Code splitting for better caching
                manualChunks: {
                    'react-vendor': ['react', 'react-dom', 'react-router-dom'],
                    'ui-vendor': ['@radix-ui/react-avatar', '@radix-ui/react-checkbox', '@radix-ui/react-dialog', '@radix-ui/react-dropdown-menu'],
                },
                chunkFileNames: 'assets/[name]-[hash].js',
                entryFileNames: 'assets/[name]-[hash].js',
                assetFileNames: 'assets/[name]-[hash].[ext]'
            }
        },
        // Compression and optimization
        terserOptions: {
            compress: {
                drop_console: true, // Remove console.logs in production
                drop_debugger: true
            }
        },
        // Bundle analysis
        reportCompressedSize: true,
        chunkSizeWarningLimit: 1000
    },
    optimizeDeps: {
        include: ['react', 'react-dom'],
        esbuildOptions: {
            target: 'es2020'
        }
    },
    server: {
        port: 5173,
        proxy: {
            '/api': {
                target: process.env.VITE_API_URL || 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            },
        },
        // Development optimizations
        hmr: {
            overlay: true
        }
    },
    // CSS optimization
    css: {
        modules: {
            localsConvention: 'camelCase'
        },
        devSourcemap: true
    },
    resolve: {
        alias: {
            '@': resolve(__dirname, 'src'),
            '@components': resolve(__dirname, 'src/components'),
            '@services': resolve(__dirname, 'src/services'),
            '@types': resolve(__dirname, 'src/types'),
            '@hooks': resolve(__dirname, 'src/hooks')
        }
    },
    // Environment-specific optimizations
    define: {
        __DEV__: mode === 'development',
        __PROD__: mode === 'production'
    }
}));
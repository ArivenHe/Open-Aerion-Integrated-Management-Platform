import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
    plugins: [
        vue(),
        vueDevTools()
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
    server: {
        proxy: {
            '/auth': {
                target: 'http://localhost:6067',
                changeOrigin: true,
            },
            '/captcha': {
                target: 'http://localhost:6067',
                changeOrigin: true,
            },
            '/platform': {
                target: 'http://localhost:6067',
                changeOrigin: true,
            },
            '/rbac': {
                target: 'http://localhost:6067',
                changeOrigin: true,
            },
            '/flight-records': {
                target: 'http://localhost:6067',
                changeOrigin: true,
            },
            '/control-records': {
                target: 'http://localhost:6067',
                changeOrigin: true,
            }
        }
    }
})

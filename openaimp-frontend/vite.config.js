import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import ui from '@nuxt/ui/vite'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        vueDevTools(),
        ui(),
        tailwindcss()
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
            '/api': {
                target: 'http://localhost:6067',
                changeOrigin: true,
            }
        }
    }
})

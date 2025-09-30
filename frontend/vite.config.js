import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import vueSetupExtend from 'vite-plugin-vue-setup-extend-plus'
import OptimizationPersist from 'vite-plugin-optimize-persist'
import PkgConfig from 'vite-plugin-package-config'
import vueDevTools from 'vite-plugin-vue-devtools'

import { defineConfig  } from 'vite'
import { fileURLToPath, URL } from 'node:url'

const alias = {
  '/@': fileURLToPath(new URL('./src', import.meta.url)),
  '@': fileURLToPath(new URL('./src', import.meta.url)),
}

const viteConfig = defineConfig(mode => {
  return {
    plugins: [
      vue(),
      vueSetupExtend(),
      PkgConfig(),
      OptimizationPersist(),
      AutoImport({
        imports: ['vue', 'vue-router','pinia'],
      }),
      vueDevTools()
    ],
    root: process.cwd(),
    resolve: { alias },
    optimizeDeps: {
      exclude: ['@babel/runtime/helpers/extends'],
    },
    base:  './' ,
    server: {
      host: '0.0.0.0',
      proxy: {
        '/api': {
          target: 'http://10.108.152.169:8110',
          // target: 'http://10.9.101.164:8110',
          ws: true,
          changeOrigin: true,
          rewrite: path => path.replace(/^\/api/, ''),
        },
      },
    },
    build: {
      outDir: 'dist',
      sourcemap: true,
      chunkSizeWarningLimit: 1500,
      rollupOptions: {
        output: {
          entryFileNames: `assets/[name].${new Date().getTime()}.js`,
          chunkFileNames: `assets/[name].${new Date().getTime()}.js`,
          assetFileNames: `assets/[name].${new Date().getTime()}.[ext]`,
          compact: true,
          manualChunks: {
            vue: ['vue', 'vue-router', 'pinia'],
            echarts: ['echarts'],
          },
        },
      },
      terserOptions: {
        compress: {
          drop_console: true,
          drop_debugger: true,
        },
        ie8: true,
        output: {
          comments: true,
        },
      },
    },
    css: {
      postcss: {
        plugins: [
          {
            postcssPlugin: 'internal:charset-removal',
            AtRule: {
              charset: atRule => {
                if (atRule.name === 'charset') {
                  atRule.remove()
                }
              },
            },
          },
        ],
      },
      preprocessorOptions: {
        scss: { api: 'modern-compiler' },
      }
    },
  }
})

export default viteConfig

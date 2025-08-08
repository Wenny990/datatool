import { createRouter, createWebHistory, createWebHashHistory } from 'vue-router'
import routes from "./route.js"
import {start , close} from "@/nprogress/nprogress"

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    ...routes,
    {
      path: '/404',
      name: '404',
      component: () => import('@/views/404/index.vue'),
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/404',
    }]
})

//登录前
router.beforeEach((to, from, next) => {
  start();
  next()
})

//登录后
router.afterEach(transition => {
  close();
});

export default router

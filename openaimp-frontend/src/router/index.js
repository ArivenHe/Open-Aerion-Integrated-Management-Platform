import {createRouter, createWebHistory} from 'vue-router'
import DashboardLayout from "@/layouts/DashboardLayout.vue";
import AdminLayout from "@/layouts/AdminLayout.vue";
import DashboardView from "@/views/DashboardView.vue";
import AdminDashboardView from "@/views/AdminDashboardView.vue";
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import ForgotPasswordView from "@/views/ForgotPasswordView.vue";
import OnlineMapView from "@/views/OnlineMapView.vue";
import UsersView from "@/views/UsersView.vue";
import RolesView from "@/views/RolesView.vue";
import SettingsView from "@/views/SettingsView.vue";
import { hasAdminAccess, isAuthenticated } from '@/utils/session'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [

        {
            path: '/login',
            name: 'login',
            component: LoginView
        },
        {
            path: '/register',
            name: 'register',
            component: RegisterView
        },
        {
            path: '/forgot-password',
            name: 'forgot-password',
            component: ForgotPasswordView
        },
        {
            path: '/map',
            name: 'map',
            component: OnlineMapView
        },
        {
            path: '/',
            component: DashboardLayout,
            children: [
                {
                    path: '',
                    name: 'dashboard',
                    component: DashboardView
                },
                {
                    path: 'users',
                    name: 'users',
                    component: UsersView
                }
            ]
        },
        {
            path: '/admin',
            component: AdminLayout,
            children: [
                {
                    path: '',
                    name: 'admin',
                    component: AdminDashboardView,
                    meta: {
                        requiresAdmin: true
                    }
                },
                {
                    path: 'roles',
                    name: 'admin-roles',
                    component: RolesView,
                    meta: {
                        requiresAdmin: true
                    }
                },
                {
                    path: 'settings',
                    name: 'admin-settings',
                    component: SettingsView,
                    meta: {
                        requiresAdmin: true
                    }
                }
            ]
        }
    ]
})

router.beforeEach((to) => {
    if (to.path === '/login' || to.path === '/register' || to.path === '/forgot-password' || to.path === '/map') {
        if ((to.path === '/login' || to.path === '/register') && isAuthenticated()) {
            return { path: '/' }
        }
        return true
    }

    if (!isAuthenticated()) {
        return { path: '/login', query: { redirect: to.fullPath } }
    }

    if (to.matched.some((record) => record.meta?.requiresAdmin) && !hasAdminAccess()) {
        return { path: '/' }
    }

    return true
})

export default router

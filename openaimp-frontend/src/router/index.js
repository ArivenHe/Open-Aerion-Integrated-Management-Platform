import {createRouter, createWebHistory} from 'vue-router'
import DashboardLayout from "@/layouts/DashboardLayout.vue";
import DashboardView from "@/views/DashboardView.vue";
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import ForgotPasswordView from "@/views/ForgotPasswordView.vue";
import OnlineMapView from "@/views/OnlineMapView.vue";
import UsersView from "@/views/UsersView.vue";
import RolesView from "@/views/RolesView.vue";
import SettingsView from "@/views/SettingsView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        // Independent Pages
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
        // Dashboard Pages
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
                },
                {
                    path: 'roles',
                    name: 'roles',
                    component: RolesView
                },
                {
                    path: 'settings',
                    name: 'settings',
                    component: SettingsView
                }
            ]
        }
    ]
})

export default router

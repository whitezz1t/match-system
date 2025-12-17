import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
    { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
    {
        path: '/menu',
        name: 'Menu',
        component: () => import('../views/Menu.vue'),
        redirect: '/menu/scoreboard', // 默认打开计分板
        // ✨ 关键点：配置子路由
        children: [
            {
                path: 'scoreboard', // 访问地址是 /menu/scoreboard
                name: 'Scoreboard',
                component: () => import('../views/Scoreboard.vue'),
                meta: { title: '实时计分板' }
            },
            {
                path: 'players', // 访问地址是 /menu/players
                name: 'PlayerList',
                component: () => import('../views/PlayerList.vue'),
                meta: { title: '选手管理' }
            },
            {
                path: 'matches', // 访问地址是 /menu/matches
                name: 'MatchList',
                component: () => import('../views/MatchList.vue'),
                meta: { title: '赛程管理' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 门卫：路由守卫 (逻辑不变)
router.beforeEach((to, from, next) => {
    const publicPages = ['/login', '/register'];
    const authRequired = !publicPages.includes(to.path);
    const loggedIn = sessionStorage.getItem('match_user');

    if (authRequired && !loggedIn) {
        return next('/login');
    }
    next();
})

export default router
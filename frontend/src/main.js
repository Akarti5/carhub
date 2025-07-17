import { createApp } from "vue"
import { createRouter, createWebHistory } from "vue-router"
import App from "./App.vue"
import "./style.css"

// Import components
import Login from "./components/Login.vue"
import Dashboard from "./components/Dashboard.vue"
import Cars from "./components/Cars.vue"
import Sales from "./components/Sales.vue"
import Clients from "./components/Clients.vue"
import Reports from "./components/Reports.vue"
import Settings from "./components/Settings.vue"
import Layout from "./components/Layout.vue"

// Router configuration
const routes = [
  { path: "/login", component: Login },
  {
    path: "/",
    component: Layout,
    children: [
      { path: "", redirect: "/dashboard" },
      { path: "/dashboard", component: Dashboard },
      { path: "/cars", component: Cars },
      { path: "/sales", component: Sales },
      { path: "/clients", component: Clients },
      { path: "/reports", component: Reports },
      { path: "/settings", component: Settings },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Route guard
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem("token")

  if (to.path === "/login") {
    if (token) {
      next("/dashboard")
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next("/login")
    }
  }
})

const app = createApp(App)
app.use(router)
app.mount("#app")

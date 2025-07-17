import api from "./api"

export const authService = {
  async login(credentials) {
    return await api.post("/auth/login", credentials)
  },

  async logout() {
    return await api.post("/auth/logout")
  },

  async getProfile() {
    return await api.get("/auth/profile")
  },
}

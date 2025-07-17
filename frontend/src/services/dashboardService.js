import api from "./api"

export const dashboardService = {
  async getStats() {
    return await api.get("/dashboard/stats")
  },

  async getCharts(year = 2024) {
    return await api.get(`/dashboard/charts?year=${year}`)
  },
}

import api from "./api"

export const saleService = {
  async getSales(params = {}) {
    const queryString = new URLSearchParams(params).toString()
    return await api.get(`/sales?${queryString}`)
  },

  async getSaleById(id) {
    return await api.get(`/sales/${id}`)
  },

  async createSale(saleData) {
    return await api.post("/sales", saleData)
  },

  async updateSale(id, saleData) {
    return await api.put(`/sales/${id}`, saleData)
  },

  async deleteSale(id) {
    return await api.delete(`/sales/${id}`)
  },

  async getRecentSales(limit = 10) {
    return await api.get(`/sales/recent?limit=${limit}`)
  },
}

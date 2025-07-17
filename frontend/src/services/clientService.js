import api from "./api"

export const clientService = {
  async getClients(params = {}) {
    const queryString = new URLSearchParams(params).toString()
    return await api.get(`/clients?${queryString}`)
  },

  async getClientById(id) {
    return await api.get(`/clients/${id}`)
  },

  async createClient(clientData) {
    return await api.post("/clients", clientData)
  },

  async updateClient(id, clientData) {
    return await api.put(`/clients/${id}`, clientData)
  },

  async deleteClient(id) {
    return await api.delete(`/clients/${id}`)
  },

  async searchClients(search, page = 0, size = 10) {
    return await api.get(`/clients/search?search=${search}&page=${page}&size=${size}`)
  },
}

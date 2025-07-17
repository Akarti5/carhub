import api from "./api"

export const carService = {
  async getCars(params = {}) {
    const queryString = new URLSearchParams(params).toString()
    return await api.get(`/cars?${queryString}`)
  },

  async getCarById(id) {
    return await api.get(`/cars/${id}`)
  },

  async createCar(carData) {
    return await api.post("/cars", carData)
  },

  async updateCar(id, carData) {
    return await api.put(`/cars/${id}`, carData)
  },

  async deleteCar(id) {
    return await api.delete(`/cars/${id}`)
  },

  async updateCarStatus(id, status) {
    return await api.put(`/cars/${id}/status?status=${status}`)
  },

  async uploadCarImages(id, files) {
    const formData = new FormData()
    files.forEach((file) => {
      formData.append("files", file)
    })

    return await api.post(`/cars/${id}/images`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
  },

  async getAvailableCars(page = 0, size = 10) {
    return await api.get(`/cars/available?page=${page}&size=${size}`)
  },

  async getSoldCars(page = 0, size = 10) {
    return await api.get(`/cars/sold?page=${page}&size=${size}`)
  },
}

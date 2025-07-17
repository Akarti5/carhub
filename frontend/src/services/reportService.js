import api from "./api"

export const reportService = {
  async downloadCarInventoryPdf(filters = {}) {
    const queryString = new URLSearchParams(filters).toString()
    const response = await api.get(`/reports/cars/inventory/pdf?${queryString}`, {
      responseType: "blob",
    })

    // Create download link
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement("a")
    link.href = url
    link.setAttribute("download", "car-inventory-report.pdf")
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)

    return response
  },

  async downloadSaleInvoicePdf(saleId) {
    const response = await api.get(`/reports/sales/invoice/${saleId}/pdf`, {
      responseType: "blob",
    })

    // Create download link
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement("a")
    link.href = url
    link.setAttribute("download", `invoice-${saleId}.pdf`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)

    return response
  },

  async downloadMonthlySalesReportPdf(year, month) {
    const response = await api.get(`/reports/sales/monthly/pdf?year=${year}&month=${month}`, {
      responseType: "blob",
    })

    // Create download link
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement("a")
    link.href = url
    link.setAttribute("download", `monthly-sales-report-${year}-${month}.pdf`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)

    return response
  },
}

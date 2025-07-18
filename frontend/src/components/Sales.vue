<template>
  <div class="p-6 fade-in">
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 gap-4">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white">Sales Management</h1>
      <button 
        @click="showAddSaleModal = true"
        class="btn-primary flex items-center"
      >
        <i class="fas fa-plus mr-2"></i>Add New Sale
      </button>
    </div>
    
    <!-- Filters -->
    <div class="card p-4 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <input 
          v-model="filters.startDate"
          type="date" 
          placeholder="Start Date"
          class="input-field"
          title="Start Date"
        >
        <input 
          v-model="filters.endDate"
          type="date" 
          placeholder="End Date"
          class="input-field"
          title="End Date"
        >
        <button 
          @click="loadSales"
          class="btn-primary flex items-center justify-center"
        >
          <i class="fas fa-search mr-2"></i>Search
        </button>
      </div>
    </div>
    
    <!-- Sales Table -->
    <div class="card overflow-hidden">
      <div class="table-container">
        <table class="table">
          <thead class="table-header">
            <tr>
              <th>Invoice #</th>
              <th>Car</th>
              <th>Client</th>
              <th>Sale Price</th>
              <th>Date</th>
              <th>Payment Method</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="sale in sales" :key="sale.id" class="table-row">
              <td class="table-cell font-medium">{{ sale.invoiceNumber }}</td>
              <td class="table-cell">{{ sale.car?.brand }} {{ sale.car?.model }}</td>
              <td class="table-cell">{{ sale.client?.firstName }} {{ sale.client?.lastName }}</td>
              <td class="table-cell">${{ formatCurrency(sale.salePrice) }}</td>
              <td class="table-cell">{{ formatDate(sale.saleDate) }}</td>
              <td class="table-cell">{{ sale.paymentMethod }}</td>
              <td class="table-cell">
                <span :class="getStatusClass(sale.status)" class="px-2 py-1 text-xs font-semibold rounded-full">
                  {{ sale.status }}
                </span>
              </td>
              <td class="table-cell">
                <div class="flex space-x-2">
                  <button 
                    @click="editSale(sale)"
                    class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300"
                    title="Edit"
                  >
                    <i class="fas fa-edit"></i>
                  </button>
                  <button 
                    @click="deleteSale(sale.id)"
                    class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                    title="Delete"
                  >
                    <i class="fas fa-trash"></i>
                  </button>
                  <button 
                    @click="downloadInvoice(sale.id)"
                    class="text-purple-600 hover:text-purple-900 dark:text-purple-400 dark:hover:text-purple-300"
                    title="Download Invoice"
                  >
                    <i class="fas fa-file-pdf"></i>
                  </button>
                  <button 
                    @click="sendInvoiceEmail(sale.id)"
                    class="text-green-600 hover:text-green-900 dark:text-green-400 dark:hover:text-green-300"
                    title="Send Invoice by Email"
                  >
                    <i class="fas fa-envelope"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- Pagination -->
      <div class="bg-white dark:bg-gray-800 px-4 py-3 flex items-center justify-between border-t border-gray-200 dark:border-gray-700 sm:px-6">
        <div class="flex-1 flex justify-between sm:hidden">
          <button 
            @click="previousPage"
            :disabled="currentPage === 0"
            class="btn-secondary disabled:opacity-50"
          >
            Previous
          </button>
          <button 
            @click="nextPage"
            :disabled="currentPage >= totalPages - 1"
            class="btn-secondary disabled:opacity-50"
          >
            Next
          </button>
        </div>
        <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <p class="text-sm text-gray-700 dark:text-gray-300">
              Showing {{ currentPage * pageSize + 1 }} to {{ Math.min((currentPage + 1) * pageSize, totalElements) }} of {{ totalElements }} results
            </p>
          </div>
          <div class="flex space-x-2">
            <button 
              @click="previousPage"
              :disabled="currentPage === 0"
              class="btn-secondary disabled:opacity-50"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            <button 
              @click="nextPage"
              :disabled="currentPage >= totalPages - 1"
              class="btn-secondary disabled:opacity-50"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Add/Edit Sale Modal -->
    <div v-if="showAddSaleModal || showEditSaleModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">
          {{ showAddSaleModal ? 'Add New Sale' : 'Edit Sale' }}
        </h3>
        
        <form @submit.prevent="saveSale" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="form-group">
              <label class="form-label">Car</label>
              <select 
                v-model="saleForm.carId"
                class="input-field"
                required
              >
                <option value="">Select a Car</option>
                <option v-for="car in availableCars" :key="car.id" :value="car.id">
                  {{ car.brand }} {{ car.model }} ({{ car.year }}) - ${{ formatCurrency(car.price) }}
                </option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Client</label>
              <select 
                v-model="saleForm.clientId"
                class="input-field"
                required
              >
                <option value="">Select a Client</option>
                <option v-for="client in clientsList" :key="client.id" :value="client.id">
                  {{ client.firstName }} {{ client.lastName }} ({{ client.email }})
                </option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Admin (Seller)</label>
              <select 
                v-model="saleForm.adminId"
                class="input-field"
                required
              >
                <option value="">Select an Admin</option>
                <option v-if="user" :value="user.id">{{ user.firstName }} {{ user.lastName }}</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Sale Price</label>
              <input 
                v-model="saleForm.salePrice"
                type="number" 
                required
                min="0"
                step="0.01"
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Payment Method</label>
              <select 
                v-model="saleForm.paymentMethod"
                class="input-field"
                required
              >
                <option value="CASH">Cash</option>
                <option value="CREDIT_CARD">Credit Card</option>
                <option value="BANK_TRANSFER">Bank Transfer</option>
                <option value="FINANCING">Financing</option>
                <option value="CHECK">Check</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Sale Date</label>
              <input 
                v-model="saleForm.saleDate"
                type="date" 
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Status</label>
              <select 
                v-model="saleForm.status"
                class="input-field"
                required
              >
                <option value="COMPLETED">Completed</option>
                <option value="PENDING">Pending</option>
                <option value="CANCELLED">Cancelled</option>
                <option value="REFUNDED">Refunded</option>
              </select>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">Notes</label>
            <textarea 
              v-model="saleForm.notes"
              rows="3"
              class="input-field"
            ></textarea>
          </div>
          
          <div class="flex justify-end space-x-3 pt-4">
            <button 
              type="button"
              @click="closeModal"
              class="btn-secondary"
            >
              Cancel
            </button>
            <button 
              type="submit"
              class="btn-primary"
            >
              {{ showAddSaleModal ? 'Add Sale' : 'Update Sale' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, inject } from 'vue'
import { saleService } from '../services/saleService'
import { carService } from '../services/carService'
import { clientService } from '../services/clientService'
import { reportService } from '../services/reportService'
import { useStore } from '../composables/useStore'

export default {
  name: 'Sales',
  setup() {
    const toast = inject('toast')
    const { user } = useStore()
    
    const sales = ref([])
    const availableCars = ref([])
    const clientsList = ref([])
    const currentPage = ref(0)
    const pageSize = ref(10)
    const totalPages = ref(0)
    const totalElements = ref(0)
    const filters = ref({
      startDate: '',
      endDate: ''
    })
    const showAddSaleModal = ref(false)
    const showEditSaleModal = ref(false)
    const saleForm = ref({
      carId: '',
      clientId: '',
      adminId: user.value?.id || '', // Pre-fill with current admin if available
      salePrice: 0,
      paymentMethod: 'CASH',
      notes: '',
      status: 'COMPLETED',
      saleDate: new Date().toISOString().split('T')[0] // Default to today
    })
    const editingSaleId = ref(null)

    const loadSales = async () => {
      try {
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          startDate: filters.value.startDate,
          endDate: filters.value.endDate
        }
        
        const response = await saleService.getSales(params)
        sales.value = response.data.content
        totalPages.value = response.data.totalPages
        totalElements.value = response.data.totalElements
      } catch (error) {
        toast.value.show('Failed to load sales', 'error')
      }
    }

    const loadRelatedData = async () => {
      try {
        const [carsResponse, clientsResponse] = await Promise.all([
          carService.getCars({ status: 'AVAILABLE', size: 1000 }), // Get all available cars
          clientService.getClients({ size: 1000 }) // Get all clients
        ])
        availableCars.value = carsResponse.data.content
        clientsList.value = clientsResponse.data.content
      } catch (error) {
        toast.value.show('Failed to load related data for sales form', 'error')
      }
    }

    const saveSale = async () => {
      try {
        const payload = { ...saleForm.value }
        // Convert date string to ISO format for backend
        if (payload.saleDate) {
          payload.saleDate = new Date(payload.saleDate).toISOString()
        }

        if (showAddSaleModal.value) {
          await saleService.createSale(payload)
          toast.value.show('Sale added successfully!', 'success')
        } else {
          await saleService.updateSale(editingSaleId.value, payload)
          toast.value.show('Sale updated successfully!', 'success')
        }
        
        closeModal()
        await loadSales()
      } catch (error) {
        toast.value.show('Failed to save sale', 'error')
      }
    }

    const editSale = (sale) => {
      saleForm.value = { 
        ...sale,
        carId: sale.car.id,
        clientId: sale.client.id,
        adminId: sale.admin.id,
        saleDate: sale.saleDate ? new Date(sale.saleDate).toISOString().split('T')[0] : ''
      }
      editingSaleId.value = sale.id
      showEditSaleModal.value = true
    }

    const deleteSale = async (saleId) => {
      if (confirm('Are you sure you want to delete this sale?')) {
        try {
          await saleService.deleteSale(saleId)
          toast.value.show('Sale deleted successfully!', 'success')
          await loadSales()
        } catch (error) {
          toast.value.show('Failed to delete sale', 'error')
        }
      }
    }

    const downloadInvoice = async (saleId) => {
      try {
        await reportService.downloadSaleInvoicePdf(saleId)
        toast.value.show('Invoice downloaded successfully!', 'success')
      } catch (error) {
        toast.value.show('Failed to download invoice', 'error')
      }
    }

    const sendInvoiceEmail = async (saleId) => {
      if (confirm('Are you sure you want to send this invoice by email?')) {
        try {
          await reportService.sendInvoiceEmail(saleId)
          toast.value.show('Invoice email sent successfully!', 'success')
        } catch (error) {
          toast.value.show(error.response?.data || 'Failed to send invoice email', 'error')
        }
      }
    }

    const closeModal = () => {
      showAddSaleModal.value = false
      showEditSaleModal.value = false
      editingSaleId.value = null
      saleForm.value = {
        carId: '',
        clientId: '',
        adminId: user.value?.id || '',
        salePrice: 0,
        paymentMethod: 'CASH',
        notes: '',
        status: 'COMPLETED',
        saleDate: new Date().toISOString().split('T')[0]
      }
    }

    const previousPage = () => {
      if (currentPage.value > 0) {
        currentPage.value--
        loadSales()
      }
    }

    const nextPage = () => {
      if (currentPage.value < totalPages.value - 1) {
        currentPage.value++
        loadSales()
      }
    }

    const getStatusClass = (status) => {
      const classes = {
        'COMPLETED': 'status-available', // Using green for completed
        'PENDING': 'status-reserved',    // Using yellow for pending
        'CANCELLED': 'status-sold',      // Using red for cancelled
        'REFUNDED': 'status-maintenance' // Using blue for refunded
      }
      return classes[status] || 'bg-gray-100 text-gray-800'
    }

    const formatCurrency = (amount) => {
      return new Intl.NumberFormat('en-US').format(amount || 0)
    }

    const formatDate = (date) => {
      if (!date) return 'N/A'
      return new Date(date).toLocaleDateString()
    }

    onMounted(() => {
      loadSales()
      loadRelatedData()
    })

    return {
      sales,
      availableCars,
      clientsList,
      currentPage,
      pageSize,
      totalPages,
      totalElements,
      filters,
      showAddSaleModal,
      showEditSaleModal,
      saleForm,
      editingSaleId,
      user,
      loadSales,
      saveSale,
      editSale,
      deleteSale,
      downloadInvoice,
      sendInvoiceEmail,
      closeModal,
      previousPage,
      nextPage,
      getStatusClass,
      formatCurrency,
      formatDate
    }
  }
}
</script>

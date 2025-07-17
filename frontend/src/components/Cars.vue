<template>
  <div class="p-6 fade-in">
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 gap-4">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white">Cars Management</h1>
      <button 
        @click="showAddCarModal = true"
        class="btn-primary flex items-center"
      >
        <i class="fas fa-plus mr-2"></i>Add New Car
      </button>
    </div>
    
    <!-- Filters -->
    <div class="card p-4 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <input 
          v-model="filters.brand"
          type="text" 
          placeholder="Brand"
          class="input-field"
        >
        <input 
          v-model="filters.model"
          type="text" 
          placeholder="Model"
          class="input-field"
        >
        <select 
          v-model="filters.status"
          class="input-field"
        >
          <option value="">All Status</option>
          <option value="AVAILABLE">Available</option>
          <option value="SOLD">Sold</option>
          <option value="RESERVED">Reserved</option>
        </select>
        <button 
          @click="loadCars"
          class="btn-primary flex items-center justify-center"
        >
          <i class="fas fa-search mr-2"></i>Search
        </button>
      </div>
    </div>
    
    <!-- Cars Table -->
    <div class="card overflow-hidden">
      <div class="table-container">
        <table class="table">
          <thead class="table-header">
            <tr>
              <th>Image</th>
              <th>Brand/Model</th>
              <th>Year</th>
              <th>Price</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="car in cars" :key="car.id" class="table-row">
              <td class="table-cell">
                <img 
                  :src="getCarImage(car)" 
                  :alt="car.brand + ' ' + car.model"
                  class="h-12 w-16 object-cover rounded"
                >
              </td>
              <td class="table-cell">
                <div class="font-medium text-gray-900 dark:text-white">{{ car.brand }} {{ car.model }}</div>
                <div class="text-sm text-gray-500 dark:text-gray-300">{{ car.color }}</div>
              </td>
              <td class="table-cell">{{ car.year }}</td>
              <td class="table-cell">${{ formatCurrency(car.price) }}</td>
              <td class="table-cell">
                <span :class="getStatusClass(car.status)" class="px-2 py-1 text-xs font-semibold rounded-full">
                  {{ car.status }}
                </span>
              </td>
              <td class="table-cell">
                <div class="flex space-x-2">
                  <button 
                    @click="editCar(car)"
                    class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300"
                    title="Edit"
                  >
                    <i class="fas fa-edit"></i>
                  </button>
                  <button 
                    @click="deleteCar(car.id)"
                    class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                    title="Delete"
                  >
                    <i class="fas fa-trash"></i>
                  </button>
                  <button 
                    v-if="car.status === 'AVAILABLE'"
                    @click="markAsSold(car.id)"
                    class="text-green-600 hover:text-green-900 dark:text-green-400 dark:hover:text-green-300"
                    title="Mark as Sold"
                  >
                    <i class="fas fa-check"></i>
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
    
    <!-- Add/Edit Car Modal -->
    <div v-if="showAddCarModal || showEditCarModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">
          {{ showAddCarModal ? 'Add New Car' : 'Edit Car' }}
        </h3>
        
        <form @submit.prevent="saveCar" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="form-group">
              <label class="form-label">Brand</label>
              <input 
                v-model="carForm.brand"
                type="text" 
                required
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Model</label>
              <input 
                v-model="carForm.model"
                type="text" 
                required
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Year</label>
              <input 
                v-model="carForm.year"
                type="number" 
                required
                min="1900"
                :max="new Date().getFullYear() + 1"
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Price</label>
              <input 
                v-model="carForm.price"
                type="number" 
                required
                min="0"
                step="0.01"
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Color</label>
              <input 
                v-model="carForm.color"
                type="text" 
                required
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Mileage</label>
              <input 
                v-model="carForm.mileage"
                type="number" 
                required
                min="0"
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Fuel Type</label>
              <select 
                v-model="carForm.fuelType"
                class="input-field"
              >
                <option value="GASOLINE">Gasoline</option>
                <option value="DIESEL">Diesel</option>
                <option value="ELECTRIC">Electric</option>
                <option value="HYBRID">Hybrid</option>
                <option value="LPG">LPG</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Transmission</label>
              <select 
                v-model="carForm.transmission"
                class="input-field"
              >
                <option value="MANUAL">Manual</option>
                <option value="AUTOMATIC">Automatic</option>
                <option value="CVT">CVT</option>
              </select>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">Description</label>
            <textarea 
              v-model="carForm.description"
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
              {{ showAddCarModal ? 'Add Car' : 'Update Car' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, inject } from 'vue'
import { carService } from '../services/carService'

export default {
  name: 'Cars',
  setup() {
    const toast = inject('toast')
    
    const cars = ref([])
    const currentPage = ref(0)
    const pageSize = ref(10)
    const totalPages = ref(0)
    const totalElements = ref(0)
    const filters = ref({
      brand: '',
      model: '',
      status: ''
    })
    const showAddCarModal = ref(false)
    const showEditCarModal = ref(false)
    const carForm = ref({
      brand: '',
      model: '',
      year: new Date().getFullYear(),
      price: 0,
      color: '',
      mileage: 0,
      fuelType: 'GASOLINE',
      transmission: 'MANUAL',
      description: ''
    })
    const editingCarId = ref(null)

    const loadCars = async () => {
      try {
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          ...filters.value
        }
        
        const response = await carService.getCars(params)
        cars.value = response.data.content
        totalPages.value = response.data.totalPages
        totalElements.value = response.data.totalElements
      } catch (error) {
        toast.value.show('Failed to load cars', 'error')
      }
    }

    const saveCar = async () => {
      try {
        if (showAddCarModal.value) {
          await carService.createCar(carForm.value)
          toast.value.show('Car added successfully!', 'success')
        } else {
          await carService.updateCar(editingCarId.value, carForm.value)
          toast.value.show('Car updated successfully!', 'success')
        }
        
        closeModal()
        await loadCars()
      } catch (error) {
        toast.value.show('Failed to save car', 'error')
      }
    }

    const editCar = (car) => {
      carForm.value = { ...car }
      editingCarId.value = car.id
      showEditCarModal.value = true
    }

    const deleteCar = async (carId) => {
      if (confirm('Are you sure you want to delete this car?')) {
        try {
          await carService.deleteCar(carId)
          toast.value.show('Car deleted successfully!', 'success')
          await loadCars()
        } catch (error) {
          toast.value.show('Failed to delete car', 'error')
        }
      }
    }

    const markAsSold = async (carId) => {
      try {
        await carService.updateCarStatus(carId, 'SOLD')
        toast.value.show('Car marked as sold!', 'success')
        await loadCars()
      } catch (error) {
        toast.value.show('Failed to update car status', 'error')
      }
    }

    const closeModal = () => {
      showAddCarModal.value = false
      showEditCarModal.value = false
      editingCarId.value = null
      carForm.value = {
        brand: '',
        model: '',
        year: new Date().getFullYear(),
        price: 0,
        color: '',
        mileage: 0,
        fuelType: 'GASOLINE',
        transmission: 'MANUAL',
        description: ''
      }
    }

    const previousPage = () => {
      if (currentPage.value > 0) {
        currentPage.value--
        loadCars()
      }
    }

    const nextPage = () => {
      if (currentPage.value < totalPages.value - 1) {
        currentPage.value++
        loadCars()
      }
    }

    const getCarImage = (car) => {
      if (car.images && car.images.length > 0) {
        return car.images[0].imageUrl
      }
      return '/placeholder.svg?height=48&width=64'
    }

    const getStatusClass = (status) => {
      const classes = {
        'AVAILABLE': 'status-available',
        'SOLD': 'status-sold',
        'RESERVED': 'status-reserved',
        'MAINTENANCE': 'status-maintenance'
      }
      return classes[status] || 'bg-gray-100 text-gray-800'
    }

    const formatCurrency = (amount) => {
      return new Intl.NumberFormat('en-US').format(amount || 0)
    }

    onMounted(() => {
      loadCars()
    })

    return {
      cars,
      currentPage,
      pageSize,
      totalPages,
      totalElements,
      filters,
      showAddCarModal,
      showEditCarModal,
      carForm,
      editingCarId,
      loadCars,
      saveCar,
      editCar,
      deleteCar,
      markAsSold,
      closeModal,
      previousPage,
      nextPage,
      getCarImage,
      getStatusClass,
      formatCurrency
    }
  }
}
</script>

<template>
  <div class="p-6 fade-in">
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 gap-4">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white">Clients Management</h1>
      <button 
        @click="showAddClientModal = true"
        class="btn-primary flex items-center"
      >
        <i class="fas fa-plus mr-2"></i>Add New Client
      </button>
    </div>
    
    <!-- Filters -->
    <div class="card p-4 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <input 
          v-model="filters.search"
          type="text" 
          placeholder="Search by Name or Email"
          class="input-field col-span-2"
        >
        <button 
          @click="loadClients"
          class="btn-primary flex items-center justify-center"
        >
          <i class="fas fa-search mr-2"></i>Search
        </button>
      </div>
    </div>
    
    <!-- Clients Table -->
    <div class="card overflow-hidden">
      <div class="table-container">
        <table class="table">
          <thead class="table-header">
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Address</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="client in clients" :key="client.id" class="table-row">
              <td class="table-cell">
                <div class="font-medium text-gray-900 dark:text-white">{{ client.firstName }} {{ client.lastName }}</div>
              </td>
              <td class="table-cell">{{ client.email }}</td>
              <td class="table-cell">{{ client.phone || 'N/A' }}</td>
              <td class="table-cell">{{ client.address || 'N/A' }}</td>
              <td class="table-cell">
                <div class="flex space-x-2">
                  <button 
                    @click="editClient(client)"
                    class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300"
                    title="Edit"
                  >
                    <i class="fas fa-edit"></i>
                  </button>
                  <button 
                    @click="deleteClient(client.id)"
                    class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                    title="Delete"
                  >
                    <i class="fas fa-trash"></i>
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
    
    <!-- Add/Edit Client Modal -->
    <div v-if="showAddClientModal || showEditClientModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-4">
          {{ showAddClientModal ? 'Add New Client' : 'Edit Client' }}
        </h3>
        
        <form @submit.prevent="saveClient" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="form-group">
              <label class="form-label">First Name</label>
              <input 
                v-model="clientForm.firstName"
                type="text" 
                required
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Last Name</label>
              <input 
                v-model="clientForm.lastName"
                type="text" 
                required
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Email</label>
              <input 
                v-model="clientForm.email"
                type="email" 
                required
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Phone</label>
              <input 
                v-model="clientForm.phone"
                type="text" 
                class="input-field"
              >
            </div>
            <div class="form-group col-span-full">
              <label class="form-label">Address</label>
              <input 
                v-model="clientForm.address"
                type="text" 
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">City</label>
              <input 
                v-model="clientForm.city"
                type="text" 
                class="input-field"
              >
            </div>
            <div class="form-group">
              <label class="form-label">Zip Code</label>
              <input 
                v-model="clientForm.zipCode"
                type="text" 
                class="input-field"
              >
            </div>
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
              {{ showAddClientModal ? 'Add Client' : 'Update Client' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, inject } from 'vue'
import { clientService } from '../services/clientService'

export default {
  name: 'Clients',
  setup() {
    const toast = inject('toast')
    
    const clients = ref([])
    const currentPage = ref(0)
    const pageSize = ref(10)
    const totalPages = ref(0)
    const totalElements = ref(0)
    const filters = ref({
      search: ''
    })
    const showAddClientModal = ref(false)
    const showEditClientModal = ref(false)
    const clientForm = ref({
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      address: '',
      city: '',
      zipCode: ''
    })
    const editingClientId = ref(null)

    const loadClients = async () => {
      try {
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          search: filters.value.search
        }
        
        const response = await clientService.getClients(params)
        clients.value = response.data.content
        totalPages.value = response.data.totalPages
        totalElements.value = response.data.totalElements
      } catch (error) {
        toast.value.show('Failed to load clients', 'error')
      }
    }

    const saveClient = async () => {
      try {
        if (showAddClientModal.value) {
          await clientService.createClient(clientForm.value)
          toast.value.show('Client added successfully!', 'success')
        } else {
          await clientService.updateClient(editingClientId.value, clientForm.value)
          toast.value.show('Client updated successfully!', 'success')
        }
        
        closeModal()
        await loadClients()
      } catch (error) {
        toast.value.show('Failed to save client', 'error')
      }
    }

    const editClient = (client) => {
      clientForm.value = { ...client }
      editingClientId.value = client.id
      showEditClientModal.value = true
    }

    const deleteClient = async (clientId) => {
      if (confirm('Are you sure you want to delete this client?')) {
        try {
          await clientService.deleteClient(clientId)
          toast.value.show('Client deleted successfully!', 'success')
          await loadClients()
        } catch (error) {
          toast.value.show('Failed to delete client', 'error')
        }
      }
    }

    const closeModal = () => {
      showAddClientModal.value = false
      showEditClientModal.value = false
      editingClientId.value = null
      clientForm.value = {
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: '',
        city: '',
        zipCode: ''
      }
    }

    const previousPage = () => {
      if (currentPage.value > 0) {
        currentPage.value--
        loadClients()
      }
    }

    const nextPage = () => {
      if (currentPage.value < totalPages.value - 1) {
        currentPage.value++
        loadClients()
      }
    }

    onMounted(() => {
      loadClients()
    })

    return {
      clients,
      currentPage,
      pageSize,
      totalPages,
      totalElements,
      filters,
      showAddClientModal,
      showEditClientModal,
      clientForm,
      editingClientId,
      loadClients,
      saveClient,
      editClient,
      deleteClient,
      closeModal,
      previousPage,
      nextPage
    }
  }
}
</script>

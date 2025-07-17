<template>
  <div class="p-6 fade-in">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2">Dashboard</h1>
      <p class="text-gray-600 dark:text-gray-300">Welcome back, {{ user?.firstName }}!</p>
    </div>
    
    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <div class="card p-6 bounce-in">
        <div class="flex items-center">
          <div class="p-3 rounded-full bg-blue-100 dark:bg-blue-900">
            <i class="fas fa-car text-blue-600 dark:text-blue-300 text-xl"></i>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600 dark:text-gray-300">Total Cars</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ stats.totalCars }}</p>
          </div>
        </div>
      </div>
      
      <div class="card p-6 bounce-in" style="animation-delay: 0.1s">
        <div class="flex items-center">
          <div class="p-3 rounded-full bg-green-100 dark:bg-green-900">
            <i class="fas fa-check-circle text-green-600 dark:text-green-300 text-xl"></i>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600 dark:text-gray-300">Available Cars</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ stats.availableCars }}</p>
          </div>
        </div>
      </div>
      
      <div class="card p-6 bounce-in" style="animation-delay: 0.2s">
        <div class="flex items-center">
          <div class="p-3 rounded-full bg-primary-100 dark:bg-primary-900">
            <i class="fas fa-dollar-sign text-primary-600 dark:text-primary-300 text-xl"></i>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600 dark:text-gray-300">Total Revenue</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">${{ formatCurrency(stats.totalRevenue) }}</p>
          </div>
        </div>
      </div>
      
      <div class="card p-6 bounce-in" style="animation-delay: 0.3s">
        <div class="flex items-center">
          <div class="p-3 rounded-full bg-purple-100 dark:bg-purple-900">
            <i class="fas fa-chart-line text-purple-600 dark:text-purple-300 text-xl"></i>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-600 dark:text-gray-300">Sold This Month</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ stats.carsSoldThisMonth }}</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Charts -->
    <div class="flex gap-6 mb-8">
  <div class="card p-11 w-1/2 h-[400px] overflow-hidden">
    <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Monthly Sales</h3>
    <canvas ref="monthlySalesChart" class="w-full h-full"></canvas>
  </div>

  <div class="card p-11 w-1/2 h-[400px] overflow-hidden">
    <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Sales by Brand</h3>
    <canvas ref="brandSalesChart" class="w-full h-full"></canvas>
  </div>
</div>

    
    <!-- Recent Activities -->
    <div class="card p-6">
      <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Recent Sales</h3>
      <div class="table-container">
        <table class="table">
          <thead class="table-header">
            <tr>
              <th>Car</th>
              <th>Client</th>
              <th>Price</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="sale in recentSales" :key="sale.id" class="table-row">
              <td class="table-cell">
                {{ sale.car?.brand }} {{ sale.car?.model }}
              </td>
              <td class="table-cell">
                {{ sale.client?.firstName }} {{ sale.client?.lastName }}
              </td>
              <td class="table-cell">
                ${{ formatCurrency(sale.salePrice) }}
              </td>
              <td class="table-cell text-gray-500 dark:text-gray-300">
                {{ formatDate(sale.saleDate) }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick, inject } from 'vue'
import { Chart, registerables } from 'chart.js'
import { useStore } from '../composables/useStore'
import { dashboardService } from '../services/dashboardService'

Chart.register(...registerables)

export default {
  name: 'Dashboard',
  setup() {
    const toast = inject('toast')
    const { user, isDarkMode } = useStore()
    
    const stats = ref({
      totalCars: 0,
      availableCars: 0,
      soldCars: 0,
      carsSoldThisMonth: 0,
      totalRevenue: 0,
      averageSalePrice: 0
    })
    const recentSales = ref([])
    const monthlySalesChart = ref(null)
    const brandSalesChart = ref(null)

    const loadDashboardData = async () => {
      try {
        const [statsResponse, chartsResponse] = await Promise.all([
          dashboardService.getStats(),
          dashboardService.getCharts()
        ])
        
        stats.value = statsResponse.data
        recentSales.value = chartsResponse.data.recentSales || []
        
        await nextTick()
        initCharts(chartsResponse.data)
      } catch (error) {
        toast.value.show('Failed to load dashboard data', 'error')
      }
    }

    const initCharts = (chartData) => {
      // Monthly Sales Chart
      if (monthlySalesChart.value) {
        new Chart(monthlySalesChart.value, {
          type: 'line',
          data: {
            labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
            datasets: [{
              label: 'Sales',
              data: [12, 19, 3, 5, 2, 3, 9, 15, 8, 12, 6, 4],
              borderColor: '#f97316',
              backgroundColor: 'rgba(249, 115, 22, 0.1)',
              tension: 0.4,
              fill: true
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                labels: {
                  color: isDarkMode.value ? '#fff' : '#000'
                }
              }
            },
            scales: {
              y: {
                ticks: {
                  color: isDarkMode.value ? '#fff' : '#000'
                },
                grid: {
                  color: isDarkMode.value ? '#374151' : '#e5e7eb'
                }
              },
              x: {
                ticks: {
                  color: isDarkMode.value ? '#fff' : '#000'
                },
                grid: {
                  color: isDarkMode.value ? '#374151' : '#e5e7eb'
                }
              }
            }
          }
        })
      }
      
      // Brand Sales Chart
      if (brandSalesChart.value) {
        new Chart(brandSalesChart.value, {
          type: 'doughnut',
          data: {
            labels: ['Toyota', 'Honda', 'Ford', 'BMW', 'Mercedes'],
            datasets: [{
              data: [30, 25, 20, 15, 10],
              backgroundColor: [
                '#f97316',
                '#fb923c',
                '#fdba74',
                '#fed7aa',
                '#ffedd5'
              ],
              borderWidth: 2,
              borderColor: isDarkMode.value ? '#1f2937' : '#ffffff'
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'bottom',
                labels: {
                  color: isDarkMode.value ? '#fff' : '#000',
                  padding: 20
                }
              }
            }
          }
        })
      }
    }

    const formatCurrency = (amount) => {
      return new Intl.NumberFormat('en-US').format(amount || 0)
    }

    const formatDate = (date) => {
      return new Date(date).toLocaleDateString()
    }

    onMounted(() => {
      loadDashboardData()
    })

    return {
      user,
      stats,
      recentSales,
      monthlySalesChart,
      brandSalesChart,
      formatCurrency,
      formatDate
    }
  }
}
</script>

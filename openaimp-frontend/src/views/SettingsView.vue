<script setup>
import { ref, onMounted } from 'vue'
import { loadConfig, updateConfig, resetSecretKey, createApiToken } from '@/api/config'

const configData = ref([])
const loading = ref(false)
const showCreateTokenModal = ref(false)
const tokenExpiryDate = ref('')
const messageModal = ref({ show: false, text: '' })

const fetchConfig = async () => {
  loading.value = true
  try {
    const res = await loadConfig()
    if (res && res.data) {
      configData.value = res.data
    }
  } catch (error) {
    console.error('Failed to load config:', error)
  } finally {
    loading.value = false
  }
}

const saveConfig = async () => {
  loading.value = true
  try {
    await updateConfig(configData.value)
    showMessage('Configuration saved successfully!')
  } catch (error) {
    console.error('Failed to save config:', error)
    showMessage('Failed to save configuration.')
  } finally {
    loading.value = false
  }
}

const handleAddConfig = () => {
  configData.value.push({ key: '', value: '' })
}

const handleRemoveConfig = (index) => {
  configData.value.splice(index, 1)
}

const handleResetSecretKey = async () => {
  if (!confirm('Are you sure you want to reset the JWT secret? This will invalidate all existing tokens.')) return
  try {
    await resetSecretKey()
    showMessage('JWT Secret reset successfully.')
  } catch (error) {
    console.error('Failed to reset secret key:', error)
    showMessage('Failed to reset secret key.')
  }
}

const handleCreateToken = async () => {
  try {
    const res = await createApiToken(tokenExpiryDate.value || '')
    showCreateTokenModal.value = false
    // Assuming response contains the token, let's show it
    if (res && res.data) {
        showMessage(`Token created: ${res.data.token}`)
    } else {
        showMessage('Token created but no token returned (check logs).')
    }
  } catch (error) {
    console.error('Failed to create token:', error)
    showMessage('Failed to create token.')
  }
}

const showMessage = (text) => {
  messageModal.value = { show: true, text }
}

onMounted(() => {
  fetchConfig()
})
</script>

<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold text-gray-900 dark:text-white mb-6">Config Editor</h1>
    
    <!-- Config Editor Card -->
    <div class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 shadow-sm mb-6">
      <div class="p-6 border-b border-gray-200 dark:border-gray-800">
        <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Configuration</h2>
      </div>
      <div class="p-6">
        <div v-if="loading" class="text-center text-gray-500 py-4">Loading...</div>
        <form v-else @submit.prevent="saveConfig" class="space-y-4">
          <div v-for="(item, index) in configData" :key="index" class="flex gap-4 items-center">
            <input 
              v-model="item.key" 
              type="text" 
              placeholder="Key" 
              class="flex-1 rounded-lg border-gray-300 dark:border-gray-700 dark:bg-gray-800 dark:text-white focus:ring-primary-500 focus:border-primary-500"
            >
            <input 
              v-model="item.value" 
              type="text" 
              placeholder="Value" 
              class="flex-1 rounded-lg border-gray-300 dark:border-gray-700 dark:bg-gray-800 dark:text-white focus:ring-primary-500 focus:border-primary-500"
            >
            <button 
              type="button" 
              @click="handleRemoveConfig(index)" 
              class="p-2 text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
              </svg>
            </button>
          </div>
          
          <div class="flex gap-4 pt-4">
            <button 
              type="button" 
              @click="handleAddConfig" 
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700 transition-colors"
            >
              Add New Config
            </button>
            <button 
              type="submit" 
              class="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-colors"
            >
              Save Changes
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- API Tokens Card -->
    <div class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 shadow-sm">
      <div class="p-6 border-b border-gray-200 dark:border-gray-800 text-center">
        <h2 class="text-lg font-semibold text-gray-900 dark:text-white">API Tokens</h2>
      </div>
      <div class="p-6 flex justify-center gap-4">
        <button 
          @click="showCreateTokenModal = true" 
          class="px-4 py-2 border border-green-600 text-green-600 rounded-lg hover:bg-green-50 dark:hover:bg-green-900/20 transition-colors"
        >
          Create New Token
        </button>
        <button 
          @click="handleResetSecretKey" 
          class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
        >
          Reset All Tokens
        </button>
      </div>
    </div>

    <!-- Create Token Modal -->
    <div v-if="showCreateTokenModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
      <div class="bg-white dark:bg-gray-900 rounded-xl shadow-lg w-full max-w-md mx-4 overflow-hidden">
        <div class="p-6 border-b border-gray-200 dark:border-gray-800 flex justify-between items-center">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Create New API Token</h3>
          <button @click="showCreateTokenModal = false" class="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          </button>
        </div>
        <div class="p-6">
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Expiry Date (Optional)</label>
          <input 
            v-model="tokenExpiryDate" 
            type="date" 
            class="w-full rounded-lg border-gray-300 dark:border-gray-700 dark:bg-gray-800 dark:text-white focus:ring-primary-500 focus:border-primary-500"
          >
          <p class="mt-2 text-sm text-gray-500">Leave blank for one year from now.</p>
        </div>
        <div class="p-6 bg-gray-50 dark:bg-gray-800/50 flex justify-end gap-3">
          <button 
            @click="showCreateTokenModal = false" 
            class="px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-600 dark:hover:bg-gray-700"
          >
            Cancel
          </button>
          <button 
            @click="handleCreateToken" 
            class="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
          >
            Create
          </button>
        </div>
      </div>
    </div>

    <!-- Message Modal -->
    <div v-if="messageModal.show" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
      <div class="bg-white dark:bg-gray-900 rounded-xl shadow-lg w-full max-w-sm mx-4 overflow-hidden">
        <div class="p-6 border-b border-gray-200 dark:border-gray-800">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Message</h3>
        </div>
        <div class="p-6">
          <p class="text-gray-700 dark:text-gray-300 break-all">{{ messageModal.text }}</p>
        </div>
        <div class="p-6 bg-gray-50 dark:bg-gray-800/50 flex justify-end">
          <button 
            @click="messageModal.show = false" 
            class="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
          >
            OK
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
import { defineStore } from 'pinia'
import {
  getAllSchemes as apiGetAllSchemes,
  createScheme as apiCreateScheme,
  updateScheme as apiUpdateScheme,
  deleteScheme as apiDeleteScheme,
  getSchemeItems as apiGetSchemeItems,
  createSchemeItem as apiCreateSchemeItem,
  updateSchemeItem as apiUpdateSchemeItem,
  deleteSchemeItem as apiDeleteSchemeItem
} from '../services/configApi'

export const useSchemeStore = defineStore('scheme', {
  state: () => ({
    schemeList: [],
    currentSchemeItems: [],
    loading: false
  }),
  
  actions: {
    async loadAllSchemes() {
      try {
        this.loading = true
        const result = await apiGetAllSchemes()
        if (result.status === 'success') {
          this.schemeList = result.data || []
        }
      } catch (error) {
        console.error('Failed to load schemes:', error)
      } finally {
        this.loading = false
      }
    },

    async createScheme(scheme) {
        try {
            this.loading = true
            const result = await apiCreateScheme(scheme)
            if (result.status === 'success') {
                await this.loadAllSchemes()
                return result.data.id
            }
            return null
        } catch (error) {
            console.error('Failed to create scheme:', error)
            return null
        } finally {
            this.loading = false
        }
    },

    async updateScheme(id, scheme) {
        try {
            this.loading = true
            const result = await apiUpdateScheme(id, scheme)
            if (result.status === 'success') {
                await this.loadAllSchemes()
                return true
            }
            return false
        } catch (error) {
            console.error('Failed to update scheme:', error)
            return false
        } finally {
            this.loading = false
        }
    },

    async deleteScheme(id) {
        try {
            this.loading = true
            const result = await apiDeleteScheme(id)
            if (result.status === 'success') {
                await this.loadAllSchemes()
                return true
            }
            return false
        } catch (error) {
            console.error('Failed to delete scheme:', error)
            return false
        } finally {
            this.loading = false
        }
    },
    
    async loadSchemeItems(schemeId) {
      try {
        this.loading = true
        const result = await apiGetSchemeItems(schemeId)
        if (result.status === 'success') {
          this.currentSchemeItems = result.data || []
          return this.currentSchemeItems
        }
        return []
      } catch (error) {
        console.error(`Failed to load items for scheme ${schemeId}:`, error)
        return []
      } finally {
        this.loading = false
      }
    },

    async fetchSchemeItems(schemeId) {
      try {
        const result = await apiGetSchemeItems(schemeId)
        if (result.status === 'success') {
          return result.data || []
        }
        return []
      } catch (error) {
        console.error(`Failed to fetch items for scheme ${schemeId}:`, error)
        return []
      }
    },

    async createSchemeItem(item) {
        try {
            this.loading = true
            const result = await apiCreateSchemeItem(item)
            if (result.status === 'success') {
                await this.loadSchemeItems(item.scheme_id)
                return true
            }
            return false
        } catch (error) {
            console.error('Failed to create scheme item:', error)
            return false
        } finally {
            this.loading = false
        }
    },

    async updateSchemeItem(id, item) {
        try {
            this.loading = true
            const result = await apiUpdateSchemeItem(id, item)
            if (result.status === 'success') {
                 // Note: we can't easily reload items here without schemeId, 
                 // caller should reload or we store currentSchemeId in state
                return true
            }
            return false
        } catch (error) {
            console.error('Failed to update scheme item:', error)
            return false
        } finally {
            this.loading = false
        }
    },

    async deleteSchemeItem(id, schemeId) {
        try {
            this.loading = true
            const result = await apiDeleteSchemeItem(id)
            if (result.status === 'success') {
                await this.loadSchemeItems(schemeId)
                return true
            }
            return false
        } catch (error) {
            console.error('Failed to delete scheme item:', error)
            return false
        } finally {
            this.loading = false
        }
    }
  }
})
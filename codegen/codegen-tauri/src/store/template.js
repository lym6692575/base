import { defineStore } from 'pinia'
import {
  getAllTemplates as apiGetAllTemplates,
  getTemplateById as apiGetTemplateById,
  updateTemplate as apiUpdateTemplate,
  getAllSchemes as apiGetAllSchemes,
  createTemplate as apiCreateTemplate,
  deleteTemplate as apiDeleteTemplate
} from '../services/configApi'

export const useTemplateStore = defineStore('template', {
  state: () => ({
    templateList: [],
    schemeList: [],
    currentTemplate: null,
    loading: false
  }),
  
  actions: {
    async loadAllTemplates() {
      try {
        this.loading = true
        const result = await apiGetAllTemplates()
        if (result.status === 'success') {
          this.templateList = result.data || []
        }
      } catch (error) {
        console.error('Failed to load templates:', error)
      } finally {
        this.loading = false
      }
    },
    
    async loadTemplateById(id) {
      try {
        this.loading = true
        const result = await apiGetTemplateById(id)
        if (result.status === 'success') {
          this.currentTemplate = result.data
          return result.data
        }
        return null
      } catch (error) {
        console.error(`Failed to load template ${id}:`, error)
        return null
      } finally {
        this.loading = false
      }
    },
    
    async updateTemplate(id, content) {
      try {
        this.loading = true
        const result = await apiUpdateTemplate(id, content)
        if (result.status === 'success') {
          // Update local list if needed
          await this.loadAllTemplates()
          return true
        }
        return false
      } catch (error) {
        console.error(`Failed to update template ${id}:`, error)
        return false
      } finally {
        this.loading = false
      }
    },

    async createTemplate(template) {
      try {
        this.loading = true
        const result = await apiCreateTemplate(template)
        if (result.status === 'success') {
          await this.loadAllTemplates()
          return result.data.id
        }
        return null
      } catch (error) {
        console.error('Failed to create template:', error)
        return null
      } finally {
        this.loading = false
      }
    },

    async deleteTemplate(id) {
      try {
        this.loading = true
        const result = await apiDeleteTemplate(id)
        if (result.status === 'success') {
          await this.loadAllTemplates()
          if (this.currentTemplate && this.currentTemplate.id === id) {
            this.currentTemplate = null
          }
          return true
        }
        return false
      } catch (error) {
        console.error(`Failed to delete template ${id}:`, error)
        return false
      } finally {
        this.loading = false
      }
    },

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
    }
  }
})
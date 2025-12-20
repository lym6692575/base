// 配置管理的API服务层
// 用于与Python后端API交互，实现配置的增删改查功能

const API_BASE_URL = 'http://localhost:8000'; // Python后端API的基础URL

// 获取所有配置
export const getAllConfigs = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/configs`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('获取配置列表失败:', error);
    throw error;
  }
};

// 获取特定配置
export const getConfigById = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/configs/${id}`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error(`获取配置ID ${id} 失败:`, error);
    throw error;
  }
};

// 创建新配置
export const createConfig = async (config) => {
  try {
    const response = await fetch(`${API_BASE_URL}/configs`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ config }),
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('创建配置失败:', error);
    throw error;
  }
};

// 更新配置
export const updateConfig = async (id, config) => {
  try {
    const response = await fetch(`${API_BASE_URL}/configs/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ config }),
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error(`更新配置ID ${id} 失败:`, error);
    throw error;
  }
};

// 删除配置
export const deleteConfig = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/configs/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error(`删除配置ID ${id} 失败:`, error);
    throw error;
  }
};

// 生成代码
export const generateCode = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/generate/${id}`, {
      method: 'POST',
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error(`生成代码失败:`, error);
    throw error;
  }
};

// --- 模板相关 API ---

// 获取所有模板
export const getAllTemplates = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/templates`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('获取模板列表失败:', error);
    throw error;
  }
};

// 获取特定模板
export const getTemplateById = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/templates/${id}`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error(`获取模板ID ${id} 失败:`, error);
    throw error;
  }
};

// 更新模板
export const updateTemplate = async (id, content) => {
  try {
    const response = await fetch(`${API_BASE_URL}/templates/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ content }),
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error(`更新模板ID ${id} 失败:`, error);
    throw error;
  }
};

// 创建模板
export const createTemplate = async (template) => {
  try {
    const response = await fetch(`${API_BASE_URL}/templates`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(template),
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('创建模板失败:', error);
    throw error;
  }
};

// 删除模板
export const deleteTemplate = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/templates/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error(`删除模板ID ${id} 失败:`, error);
    throw error;
  }
};

// --- 方案相关 API ---

// 获取所有方案
export const getAllSchemes = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/schemes`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('获取方案列表失败:', error);
    throw error;
  }
};

// 创建方案
export const createScheme = async (scheme) => {
  try {
    const response = await fetch(`${API_BASE_URL}/schemes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(scheme)
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return await response.json();
  } catch (error) {
    console.error('创建方案失败:', error);
    throw error;
  }
};

// 更新方案
export const updateScheme = async (id, scheme) => {
  try {
    const response = await fetch(`${API_BASE_URL}/schemes/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(scheme)
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return await response.json();
  } catch (error) {
    console.error(`更新方案 ${id} 失败:`, error);
    throw error;
  }
};

// 删除方案
export const deleteScheme = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/schemes/${id}`, { method: 'DELETE' });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return await response.json();
  } catch (error) {
    console.error(`删除方案 ${id} 失败:`, error);
    throw error;
  }
};

// 获取方案的步骤
export const getSchemeItems = async (schemeId) => {
  try {
    const response = await fetch(`${API_BASE_URL}/schemes/${schemeId}/items`);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return await response.json();
  } catch (error) {
    console.error(`获取方案 ${schemeId} 步骤失败:`, error);
    throw error;
  }
};

// 创建步骤
export const createSchemeItem = async (item) => {
  try {
    const response = await fetch(`${API_BASE_URL}/scheme_items`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(item)
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return await response.json();
  } catch (error) {
    console.error('创建步骤失败:', error);
    throw error;
  }
};

// 更新步骤
export const updateSchemeItem = async (id, item) => {
  try {
    const response = await fetch(`${API_BASE_URL}/scheme_items/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(item)
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return await response.json();
  } catch (error) {
    console.error(`更新步骤 ${id} 失败:`, error);
    throw error;
  }
};

// 删除步骤
export const deleteSchemeItem = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/scheme_items/${id}`, { method: 'DELETE' });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return await response.json();
  } catch (error) {
    console.error(`删除步骤 ${id} 失败:`, error);
    throw error;
  }
};

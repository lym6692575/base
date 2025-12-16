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

module.exports = {
  /**
   * 是否系统布局配置
   */
  showSettings: false,
  /**
   * 是否显示搜索框（搜索菜单）
   */
  showSearchText: true,
  /**
   * 菜单是否联动
   */
  menuLinkage: true,
  /**
   * 是否显示 tagsView
   */
  tagsView: true,

  /**
   * 是否固定头部
   */
  fixedHeader: false,

  /**
   * 是否显示logo
   */
  sidebarLogo: true,
  /**
   * 布局方式,默认纵向布局
   */
  layout: "vertical",
  /**
   * @type {string | array} 'production' | ['production', 'development']
   * @description Need show err logs component.
   * The default is only used in the production env
   * If you want to also use it in dev, you can pass ['production', 'development']
   */
  errorLog: "production"
};

import store from "@/store";

export function getMenuName(tabId) {
  let result = "";
  let menu = store.getters.visitedViews.find(c => c.tabId == tabId);
  if (menu) {
    result = menu.name;
  } else {
    result = store.getters.allMenus.find(c => c.id == tabId).name;
  }
  return result;
}

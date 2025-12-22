<template>
  <div>
    <svg-icon icon-class="star" @click="click" />

    <el-dialog title="收藏菜单" :visible.sync="dialogFormVisible">
      <div class="el-dialog__body">
        <fl-tree
          :data="userMenuTree"
          :props="defaultProps"
          :treeProperties="configurations"
          @nodeCheck="flTreeCheckChange"
        ></fl-tree>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Collection",
  data() {
    return {
      dialogFormVisible: false,
      formLabelWidth: "120px",
      defaultProps: {
        children: "children",
        label: "name",
        icon: "icon"
      },
      configurations: {
        //树节点主键
        treeNodeKey: "id",
        //是否显示或隐藏搜索框
        //showFilter: true,
        // 是否开启复选框
        showCheckbox: true,
        // 是否需要复选
        multipleSelect: true,
        // 是否显示图标
        showIcon: true,
        //是否可以拖拽节点
        draggableNode: false,
        //展开节点的方式，all 所有,first 第一个,one 一级,two 二级,array 自定义节点数组
        expendType: "all",
        //是否使用手风琴模式，对于同一级的节点，每次只能展开一个
        accordionNode: false,
        //是否懒加载
        lazyNode: false,
        //是否显示按钮
        showBtn: false,
        showFilter: false,
        checkStrictly: false,
        checkNodes: []
      }
    };
  },
  mounted() {
    //测试数据
    // let testData = [
    //   { id: '1000', name: '测试1', children: [] },
    //   { id: '1001', name: '测试2',
    //     children: [
    //       { id: '1032', name: '测试11', children: null }
    //     ]
    //   }
    // ]

    //初始化菜单树默认选中节点
    if (this.userCollectionTree != null && this.userCollectionTree.length > 0) {
      setDefaultCheckedNodes(
        this.userCollectionTree,
        this.configurations.checkNodes
      );
    }
  },
  beforeDestroy() {},
  computed: {
    userMenus() {
      return this.$store.state.permission.allMenus;
    },
    userMenuTree() {
      return this.$store.state.permission.allMenusTree;
    },
    userCollectionTree() {
      return this.$store.state.collection.menus;
    }
  },
  methods: {
    click() {
      this.dialogFormVisible = true;
    },

    //选中或取消菜单树节点时，同步更新store中收藏菜单的数据
    flTreeCheckChange(data, allCheckedNodeKeys) {
      let selectedMenus = this.findMenuByIds(allCheckedNodeKeys);
      let selectedMenuTree = buildMenuTree(selectedMenus, 0);
      this.$store.dispatch("collection/setMenus", selectedMenuTree);
    },

    //根据菜单树返回的菜单id查找菜单
    findMenuByIds(ids) {
      let menus = [];
      if (ids != null && ids.length > 0) {
        for (let i = 0; i < ids.length; i++) {
          for (let k = 0; k < this.userMenus.length; k++) {
            let m = this.userMenus[k];
            if (m.id == ids[i]) {
              menus.push(m);
              break;
            }
          }
        }
      }
      return menus;
    }
  }
};

function buildMenuTree(menus, parentId) {
  let nodes = [];
  for (let i = 0; i < menus.length; i++) {
    let menu = menus[i];
    if (menu.parentMenuId == parentId) {
      let newMenuObj = copyMenu(menu);
      newMenuObj.children = buildMenuTree(menus, menu.id);
      nodes.push(newMenuObj);
    }
  }
  return nodes;
}

function copyMenu(menu) {
  return {
    ancestors: menu.ancestors,
    app: menu.app,
    appId: menu.appId,
    args: menu.args,
    code: menu.code,
    createTime: menu.createTime,
    creator: menu.creator,
    hierarchy: menu.hierarchy,
    icon: menu.icon,
    id: menu.id,
    isControl: menu.isControl,
    mainPage: menu.mainPage,
    mainPageId: menu.mainPageId,
    meta: menu.meta,
    modifier: menu.modifier,
    modifyTime: menu.modifyTime,
    name: menu.name,
    orderNo: menu.orderNo,
    parentMenu: menu.parentMenu,
    parentMenuId: menu.parentMenuId,
    path: menu.path,
    relatedPages: menu.relatedPages,
    remark: menu.remark,
    status: menu.status,
    subMenus: null,
    children: null
  };
}

function setDefaultCheckedNodes(roots, checkedArray) {
  for (let i = 0; i < roots.length; i++) {
    let children = roots[i].children;
    if (children != null && children.length > 0) {
      setDefaultCheckedNodes(children, checkedArray);
    } else {
      checkedArray.push({ id: roots[i].id });
    }
  }
}
</script>

<style scoped>
.el-dialog__body {
  height: 50vh;
  overflow: auto;
  padding: 0;
}
</style>

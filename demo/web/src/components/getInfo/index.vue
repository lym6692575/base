<script>
import { Message, MessageBox, Notification } from "element-ui";
import { getMgmtOrgTreesByExample as getOrgListData } from "@/api/fineMgmt/mgmtOrg"; //获取组织信息
import store from "../../store";
export default {
  //获取登录用户信息
  getLoginUserInfo(callBack) {
    let userInfo = store.state.user.info;
    typeof callBack == "function" && callBack(userInfo);
  },
  //  获取登录用户组织信息
  getLoginUserOrgInfo(callBack) {
    let userInfo = store.state.user.info;
    let org = {};
    org.orgId = userInfo.orgId;
    org.orgName = userInfo.org;
    typeof callBack == "function" && callBack(org);
  },
  //  获取登录用户权限下组织信息
  getOrgList(callBack) {
    getOrgListData(store.getters.info.id, "").then(response => {
      typeof callBack == "function" && callBack(response);
    });
  },
  setBukrs(bukrs) {
    store.dispatch("setBukrs", bukrs);
  },
  getBukrs() {
    return store.state.bukrs.bukrs;
  }
};
</script>

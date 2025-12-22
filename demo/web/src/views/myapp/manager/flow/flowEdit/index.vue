<template>
  <el-dialog
    title="流程图管理"
    :visible.sync="dialogVisible"
    width="80%"
    @close="reset"
  >
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="环节配置" name="first">
        <flow-step-box ref="setpBoxRef" />
      </el-tab-pane>
      <el-tab-pane label="环节细节管理" name="second">
        <flow-setp-detail ref="detailRef" />
      </el-tab-pane>
    </el-tabs>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="handleEditTDywlc">保存</el-button>
    </span>
  </el-dialog>
</template>

<script>
import flowStepBox from "./flowStepBox.vue";
import flowSetpDetail from "./flowSetpDetail.vue";
import { mapGetters, mapActions } from "vuex";
import { saveTDywlc } from "@/api/app/manager/flow";

export default {
  props:{
    activeType:{
      type:String
    }
  },
  data() {
    return {
      activeName: "first",
      dialogVisible: false,
      index: null,
    };
  },
  components: {
    flowStepBox,
    flowSetpDetail,
  },
  methods: {
    ...mapActions("flow", [
      "changeNewButtondisabled",
      "resetNewButtondisabled",
      "resetflow",
    ]),

    // 生成唯一的 key 值
    generateDialogKey() {
      this.dialogKey = Math.random().toString(36).substr(2, 10);
    },

    // 恢复初始状态，清空表单信息
    reset() {
      this.resetflow();
      this.activeName = "first";
      this.$refs.detailRef.activeTab = 1;
      this.$refs.detailRef.childEditingStatuses = [];
      this.$refs.setpBoxRef.tabList=[]
      this.$refs.setpBoxRef.$refs.general.tableData=[]
      console.log("close")
    },

    // 打开逻辑
    openDialog(index) {
      this.index = index;
      this.dialogVisible = true; // 打开对话框
    },

    // 处理保存编辑逻辑
    async handleEditTDywlc() {
      // 校验附件列表
      const childEditingStatuses = this.$refs.detailRef.childEditingStatuses;
      let index = childEditingStatuses.findIndex((status) => status === true);
      if (index != -1) {
        const hjmc = this.flowFormParams.tdywlcHjList[index - 1].hjmc;
        let message =
          "环节细节中环节《" +
          hjmc +
          "》存在未编辑完成的的附件列表，请确认文件信息后继续保存！";
        this.$message({
          message,
          type: "warning",
        });
        return;
      }

      // 校验执行部门和执行岗位
      const tdywlcHjList = this.flowFormParams.tdywlcHjList || [];
      let emptyZxbmHjmcList = []; // 保存执行部门为空的环节名称
      let emptyZxgwHjmcList = []; // 保存执行岗位为空的环节名称
      for (let i = 0; i < tdywlcHjList.length; i++) {
        const content = tdywlcHjList[i];
        const hjmc = this.flowFormParams.tdywlcHjList[i].hjmc;
        if (content.zxbm === []) {
          emptyZxbmHjmcList.push(`《${hjmc}》`);
        }
        if (!content.zxgw && !content.zxgwmc) {
          emptyZxgwHjmcList.push(`《${hjmc}》`);
        }
      }

      // 将环节名称转为字符串，并在环节之间添加、符号
      let emptyZxbmHjmcStr = emptyZxbmHjmcList.join("、");
      let emptyZxgwHjmcStr = emptyZxgwHjmcList.join("、");
      // 如果存在执行部门为空的环节或执行岗位为空的环节，弹出确认框
      if (emptyZxbmHjmcList.length > 0 || emptyZxgwHjmcList.length > 0) {
        let message = "";
        if (emptyZxbmHjmcList.length > 0) {
          message += `环节细节中环节${emptyZxbmHjmcStr}的执行部门为空，`;
        }
        if (emptyZxgwHjmcList.length > 0) {
          message += `环节细节中环节${emptyZxgwHjmcStr}的执行岗位为空，`;
        }
        message += "是否继续保存？";
        try {
          await this.$confirm(message, "提示", {
            confirmButtonText: "是",
            cancelButtonText: "否",
            type: "warning",
          });
        } catch {
          // 用户点击了"否"，关闭对话框并结束
          return;
        }
      }

      // 其他校验
      this.$refs.setpBoxRef.validate(async (err) => {
        if (!err) {
          console.log("Validation passed");

          const formDataWithFiles = new FormData(); // 表单
          const form = this.flowFormParams;
          const disabled = this.newButtondisabled;
          const addedGeneralFiles = new Set(); // 使用Set来存储已添加的generalFiles
          const generalFileList = []; // 通用附件表单数据

          // 处理文件数据
          form.tdywlcHjList.forEach((tdywlcHjList) => {
            if (tdywlcHjList && tdywlcHjList.tdywlcHjfjList)
              tdywlcHjList.tdywlcHjfjList.forEach((item) => {
                if (item.fileChanged && !item.isGeneral) {
                  formDataWithFiles.append("files", item.fileInfo);
                }
                // 特殊处理通用附件
                if (item.isGeneral) {
                  // 检查item.fileInfo是否已经被添加
                  if (!addedGeneralFiles.has(item.fileInfo)) {
                    generalFileList.push(item);
                    formDataWithFiles.append("generalFiles", item.fileInfo);
                    addedGeneralFiles.add(item.fileInfo); // 将item.fileInfo添加到Set中
                  }
                }
              });
          });
          if(this.activeType==="save"){
            this.flowFormParams.cjr = this.userInfo.realname
          }
          // 表单数据
          let data = JSON.parse(JSON.stringify(this.flowFormParams));

          const addedItems = new Set(); // 用于跟踪已经添加的项
          const indicesToRemove = []; // 保存删除的索引

          if (data.tdywlcHjList) {
            data.tdywlcHjList.forEach((ywlcHj, i) => {
              if (ywlcHj.tdywlcHjfjList) {
                ywlcHj.tdywlcHjfjList.forEach((fj, j) => {
                  console.log("fj", fj);

                  if (fj.isGeneral) {
                    const fjString = JSON.stringify(fj);

                    // 检查是否已存在
                    if (!addedItems.has(fjString)) {
                      addedItems.add(fjString);
                    }

                    // 保存要删除的索引
                    indicesToRemove.push({ i, j });
                  }
                });
              }
            });
          }

          // 从大到小排序索引，以便安全地删除项
          indicesToRemove.sort((a, b) => b.j - a.j || b.i - a.i);

          // 删除项
          indicesToRemove.forEach(({ i, j }) => {
            data.tdywlcHjList[i].tdywlcHjfjList.splice(j, 1);
          });

          // console.log("GeneralFilesData", generalFileList);
          // console.log("data", data);

          formDataWithFiles.append("data", JSON.stringify(data));
          formDataWithFiles.append(
            "generalFileList",
            JSON.stringify(generalFileList)
          );

          // 保存逻辑
          const saveFunction = async () => {
            const res = await saveTDywlc(formDataWithFiles);
            if (res.result) {
              this.$message({
                message: res.msg,
                type: "success",
              });
              // 刷新数据
              this.$emit("data-saved");
              // 组件初始化
              this.reset();
              this.dialogVisible = false;
            }
          };

          // 校验
          if (disabled == true) {
            this.$confirm("您还没有确认新增的环节，是否确认保存？", "提示", {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning",
            })
              .then(() => {
                saveFunction();
              })
              .catch(() => {});
          } else {
            saveFunction();
          }
        } else {
          console.log("Validation failed");
          this.$message({
            message: "环节配置必填信息缺失，请检查环节配置！",
            type: "warning",
          });
        }
      });
    },
    handleClick() {},
  },
  computed: {
    ...mapGetters([
      "userInfo",
      "flowFormParams",
      "newButtondisabled",
    ]),
  },
  created() {
    this.generateDialogKey(); // 生成初始的 key 值
  },
};
</script>

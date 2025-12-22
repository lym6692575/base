<template>
  <div>
    <lee-table
      ref="leeTable"
      :fetch-data="getTableDataAdapter"
      :query-params="value"
      v-bind="$attrs"
      v-on="$listeners"
    >
      <el-table-column property="ywlb.lbmc" label="类型">
        <template v-slot="spoce">
          {{
            spoce.row.hgwj ? '合规文件' : '流程图'
          }}
        </template>
      </el-table-column>
      <el-table-column property="ywlb.lbmc" label="业务类别">
        <template v-slot="spoce">
          {{
            (spoce.row.hgwj && Array.isArray(spoce.row.hgwj.ywlb) && spoce.row.hgwj.ywlb.map(item => item.lbmc).join(', '))
            ||
            (spoce.row.ywlc && spoce.row.ywlc.tdywlb && spoce.row.ywlc.tdywlb.lbmc)
          }}
        </template>
      </el-table-column>
      <el-table-column property="ywlb.lbmc" label="文件 / 流程名称">
        <template v-slot="spoce">
          {{ (spoce.row.hgwj && spoce.row.hgwj.wjmc) || (spoce.row.ywlc && spoce.row.ywlc.lcmc) }}
        </template>
      </el-table-column>
      <el-table-column
        property="fwsj"
        label="发文时间 / 创建时间"
      >
        <template v-slot="spoce">
          {{ (spoce.row.hgwj && spoce.row.hgwj.fwsj) || (spoce.row.ywlc && spoce.row.ywlc.cjsj.substring(0, 10)) }}
        </template>
      </el-table-column>
      <el-table-column
        property="cjr"
        label="发布人"
      >
        <template v-slot="spoce">
          {{ (spoce.row.hgwj && spoce.row.hgwj.cjr) || (spoce.row.ywlc && spoce.row.ywlc.cjr) }}
        </template>
      </el-table-column>
      <!--      <el-table-column-->
      <!--          property="cjr"-->
      <!--          label="操作"-->
      <!--          width="120"-->
      <!--      >-->
      <!--        <template v-slot="scope">-->
      <!--          <el-popconfirm-->
      <!--              title="确定删除该视频链接？"-->
      <!--              @confirm="handleDelete(scope.$index, scope.row)"-->
      <!--          >-->
      <!--            <el-button slot="reference" size="mini" type="danger">-->
      <!--              删除-->
      <!--            </el-button>-->
      <!--          </el-popconfirm>-->
      <!--        </template>-->
      <!--      </el-table-column>-->
    </lee-table>
  </div>
</template>

<script>
import {getGwlcgx} from "@/api/app/manager/workPositionJoiner";
import {DeepCopyUtils, dateUtils, searchUtils} from "@/utils/LeeUtils";
import {mapGetters} from "vuex";
import leeTable from '@/components/elementUI/lee-table/index.vue'
import {getAdminList} from '@/api/app/manager/admin'

export default {
  inject: ["eventBus"],
  components: {leeTable},
  data() {
    return {
      rawData: [],
      currentRow: null,
      page: 1,
      rows: 10,
      isLoading: false,
      dialogVisible: false,
    };
  },
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  computed: {
    ...mapGetters(["znbm"]),
  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getGwlcgx(params)
        console.log(response)
        return {
          data: response.data.content, // 适应到你的数据结构
          total: response.data.totalElements // 适应到你的数据结构
        }
      } catch (error) {
        this.$message({
          message: error,
          type: 'warning'
        })
        throw error
      }
    },

    // 查
    handelSearch() {
      this.$refs.leeTable.getData()
    },

    // index方法
    indexMethods(index) {
      return (this.page - 1) * this.rows + index + 1;
    },

    // 获取数据
    handleGetTableData(params) {
      this.isLoading = true;

      let _params = DeepCopyUtils.deepCopy(params);
      console.log(_params);
      if (_params.gwglid.length > 0) {
        _params.gwglid = _params.gwglid[_params.gwglid.length - 1];
      } else {
        _params.gwglid = _params.gwglid[0];
      }

      getGwlcgx(_params).then((res) => {
        this.rawData = res.data;
        this.isLoading = false;
      });
    },

    // 分页切换
    handleCurrentChange(val) {
      let params = this.value;
      if (params) {
        params.page = val;
        this.page = val;
      }
      this.handleGetTableData(params);
    },

    // 寻找职能部门名称
    findZnbmmc(id) {
      if (this.znbm) {
        const found = this.znbm.data.find((item) => {
          return item.id == id;
        });
        if (found) {
          return found.bmmc;
        } else {
          return "无";
        }
      }
    },

    // 寻找职能部门名称
    findHierarchy(id, topLevel = true) {
      let hierarchy = [];
      const data = DeepCopyUtils.deepCopy(this.znbm.data);
      for (let i = 0; i < data.length; i++) {
        if (data[i].id === id) {
          hierarchy.push(data[i].bmmc);
          if (data[i].sjid !== null) {
            // Combine the arrays
            hierarchy = this.findHierarchy(data[i].sjid, false).concat(
              hierarchy
            );
          }
          break;
        }
      }

      return topLevel ? hierarchy.join(" / ") : hierarchy;
    },
  },

  mounted() {
  },
};
</script>

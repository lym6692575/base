<template>
  <div>
    {{ test }}
    <gw
      ref="refGw"
      v-model="test"
      :showAllOptions="false"
      filterable
      allow-create>
    </gw>
    <el-button @click="testtt">t</el-button>
  </div>
</template>

<script>
import leeQuarterPicker from "@/components/elementUI/lee-quarter-picker/index.vue";
//引入VueOfficeDocx组件
import VueOfficePdf from '@vue-office/pdf'
//引入相关样式
import '@vue-office/docx/lib/index.css'
import gw from "@/components/select/gw.vue";
import leeSelect from "@/components/elementUI/lee-select.vue";

export default {
  components: {
    leeSelect,
    gw,
    VueOfficePdf,
    leeQuarterPicker
  },
  data() {
    return {
      docx: '',
      test: '',
    }
  },
  mounted() {
    let src = 'http://localhost:10555/dev-api/manager/file/view/8b6ae2be8c0a072e018c0a0a9ccc0000'
    // let src = 'http://10.114.12.59:11330/manager/file/view/'
    fetch(src,
      {
        method: 'get'
      }).then(res => {
      console.log('源', res)
      //读取文件的arrayBuffer
      res.arrayBuffer().then(res => {
        console.log(res)
        this.docx = res
      })
    })
  },
  methods: {
    rendered() {
      console.log('渲染完成')
    },
    testtt(){
      // this.$refs.editGrade.createdLabel
      console.log(this.$refs.refGw.createdLabel)
    },
    handleUpdate(val){
      console.log(val)
    }
  }
}
</script>

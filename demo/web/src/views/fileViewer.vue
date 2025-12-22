<template>
  <div>
    <vue-office-pdf
      :src="docx"
      style="height: 100vh;"
      @rendered="rendered"
    />
  </div>
</template>

<script>
//引入VueOfficeDocx组件
import VueOfficePdf from '@vue-office/pdf'
//引入相关样式
import '@vue-office/docx/lib/index.css'
import {evn} from "@/utils/ProjectUtils";

export default {
  components: {
    VueOfficePdf
  },
  data() {
    return {
      docx: ''
    }
  },
  mounted() {
    const query = this.$route.query
    let path = query.fjdz + query.savedname
    path = path.replace(/\//g, '\\');
    let encodedPath = encodeURIComponent(path);
    let url = evn.getBasePathConfig().biz.toString()
    // let src = 'http://localhost:10555/dev-api/manager/file/view'
    let src = `${url}manager/file/view?path=${encodedPath}`
    console.log(src)
    // let src = `http://localhost:11330/manager/file/view?path=${encodedPath}`
    // let src = 'http://localhost:11330/dev-api/manager/file/view'
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
    }
  }
}
</script>

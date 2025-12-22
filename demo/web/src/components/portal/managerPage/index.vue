<template>
  <div>
    <svg-icon icon-class="user" @click="goto"/>
  </div>
</template>

<script>
export default {
  name: 'managerPage',
  data() {
    return {
      url: 'managerHome'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
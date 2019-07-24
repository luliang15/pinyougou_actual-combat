var app = new Vue({

    el: "#app",
    data: {
        list:[],
    },
    methods: {
        findAllOrderBySellerId: function () {
            axios.post('order/findOrderBySellerId.shtml').then(function (response) {

                  app.list = response.data;


                }
            )
        }

    },
    created: function () {
          this.findAllOrderBySellerId();
    }


});

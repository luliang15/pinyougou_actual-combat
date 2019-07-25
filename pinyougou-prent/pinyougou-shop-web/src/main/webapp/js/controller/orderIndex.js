var app = new Vue({

    el: "#app",
    data: {
        list:[],
        status:['','等待买家付款','等待发货']
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

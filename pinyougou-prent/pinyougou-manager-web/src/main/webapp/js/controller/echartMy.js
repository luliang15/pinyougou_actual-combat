var app = new Vue({
    el: "#app",
    data: {

        entity:{},
        orderList:[],


    },
    methods: {
        //查询所有订单
        findAll:function () {
            axios.get("/order/findAll.shtml").then((resp)=>{
                app.orderList = resp.data
            });
        },

        upload:function () {
            location.href="../../download/orderTable.xlsx"
        }

    },
    //钩子函数 初始化了事件和
    created: function () {
      this.findAll();

    }

})

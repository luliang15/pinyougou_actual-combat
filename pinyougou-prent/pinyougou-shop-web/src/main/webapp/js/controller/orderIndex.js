var app = new Vue({

    el: "#app",
    data: {
        list:[],
        status:['','未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价'],
        pages: 10,
        pageNo: 1,
    },
    methods: {
        findAllOrderBySellerId: function (pageNo) {

            axios.post('order/findOrderBySellerId.shtml?pageNo='+pageNo).then(function (response) {
                  app.list = response.data.order;
                               //Map{"pageInfo":pageInfo,"order":order}

                console.log(response.data.pageInfo.pageNum)


                app.pageNo = response.data.pageInfo.pageNum;


                //总页数
                app.pages = response.data.pageInfo.pages;

                }
            )
        },
        updateStatus:function (status,orderId) {
            axios.get('order/update.shtml?status='+status+'&orderId='+orderId).then(function (response) {
                if (response.data) {
                    alert('发货成功')
                    app.findAllOrderBySellerId(1);
                }
            })

        }

    },
    created: function () {
          this.findAllOrderBySellerId(1);
    }


});

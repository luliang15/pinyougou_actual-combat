var app = new Vue({

    el: "#app",
    data: {
        list:[],
        status:['','等待买家付款','等待发货'],
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
        }

    },
    created: function () {
          this.findAllOrderBySellerId(1);
    }


});

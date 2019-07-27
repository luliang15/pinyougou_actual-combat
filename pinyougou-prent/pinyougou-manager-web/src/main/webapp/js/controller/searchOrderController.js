
var app = new Vue({
    el: "#app",
    data: {
        //定义变量的地方  []表示数字，{}表示集合
        pages:15,   //总页数
        pageNo:1,   //当前页初始值为第一页
        list1:[],     //获取到服务器响应数据，数组形式
        entity:[] ,  //品牌对象,绑定页面、
        ids:[] ,  //为品牌绑定删除的ids数组参数
        searchEntity:{},   //搜索条件对象
        createTime1:[],
        paymentTime1:''

    },
    methods: {
        searchList:function (curPage) {
            axios.post('/order/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据,将服务器响应回来的数据赋给前端的list中
                app.list1=response.data.orderLists;
                //当前页
                app.pageNo= response.data.pageInfo.pageNum;
                //总页数
                app.pages=response.data.pageInfo.pages;
                for (var i=0;i<app.list1.length;i++){
                    app.list1[i].order.createTime=app.formatDate(app.list1[i].order.createTime);
                    app.list1[i].order.paymentTime=app.formatDate(app.list1[i].order.paymentTime);
                }
                app.searchEntity={};
            });
        },
        newPage:function () {
            window.location.href="http://localhost:9101/admin/searchorder.html"
        },
        formatDate: function (value) {
            var date = new Date(value);
            var y = date.getFullYear();
            var MM = date.getMonth() + 1;
            MM = MM < 10 ? ('0' + MM) : MM;
            var d = date.getDate();
            d = d < 10 ? ('0' + d) : d;
            var h = date.getHours();
            h = h < 10 ? ('0' + h) : h;
            var m = date.getMinutes();
            m = m < 10 ? ('0' + m) : m;
            var s = date.getSeconds();
            s = s < 10 ? ('0' + s) : s;
            return y + '-' + MM + '-' + d + ' ' + h + ':' + m + ':' + s;
        }
    },
    //钩子函数
    created: function () {
        this.searchList(1);
        //this.findAllOrder();
    }
});


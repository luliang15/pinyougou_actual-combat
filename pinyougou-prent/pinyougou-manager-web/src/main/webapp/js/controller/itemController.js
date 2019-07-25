var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        itemlist:[],
        list:[],
        ids:[],
        entity:{},
        ids:[],
        searchEntity:{}

    },
    methods: {

        findAll:function () {
            axios.get("/item/findAll.shtml").then((resp)=>{
                app.itemlist = resp.data;

            })
            this.findPage();


        },

        findPage:function () {
            var that = this;
            axios.get('/item/findPage.shtml',{params:{
                    pageNo:this.pageNo
                }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },
        searchList:function (curPage) {
            axios.post('/item/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },

        upload:function (page) {
            if (page=="1"){

            location.href="../../download/item.xlsx"
            } else if(page=="2"){
                location.href="../../download/itemAll.xlsx"
            }
        }





    },
    //钩子函数 初始化了事件和
    created: function () {
        this.findAll();



    }

})

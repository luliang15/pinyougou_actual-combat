var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        specList:[],   //此变量的格式：[{id:1,'text':"网络",options:[{},{}]}]
        list: [],
        ids: [],
        searchEntity: {},
        specName:'',

    },
    methods: {


        save:function () {
           axios.get("/sepcifcation/add.shtml?name="+this.specName).then((resp)=>{
               app.findSpec();
           })
        },

        findSpec:function () {
            axios.get("/sepcifcation/sepcfindAll.shtml").then((resp)=>{

                app.list=resp.data
            })
        },



        searchList: function (curPage) {
            axios.post('/sepcifcation/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        findPage: function () {
            var that = this;
            axios.get('/sepcifcation/findPage.shtml', {
                params: {
                    pageNo: this.pageNo
                }
            }).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data.list;
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            }).catch(function (error) {

            })
        },



    },

    //钩子函数 初始化了事件和
    created: function () {
        this.searchList(1)
    }

})


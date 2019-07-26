var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        specList: [],
        list: [],
        ids: [],
        searchEntity: {},
        brand: {"brandStatus": '0'},

    },
    methods: {
        save: function () {

            axios.post("/brand/add.shtml", this.brand).then((resp) => {
                alert(resp.data.message)

                app. searchList(1);
            })
        },

        findSpec: function () {
            axios.get("/brand/sepcfindAll.shtml").then((resp) => {

                app.list = resp.data
            })
        },


        searchList: function (curPage) {
            axios.post('/brand/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
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
            axios.get('/brand/findPage.shtml', {
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


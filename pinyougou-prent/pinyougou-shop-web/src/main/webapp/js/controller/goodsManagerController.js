var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        //定义一个变量，通过这个变量来确定此状态是否审核
        status: ['未审核', '已审核', '审核不通过', '驳回审核'],
        isMarketable:['下架','上架'],
        //定义一个变量来接受根据分类id查询到的数据
        itemCatList: [],
        ids: [],
        //根据商品名称与商品状态进行模糊查询的变量条件，将这个变量的条件作为发送请求携带的参数，根据这些参数查询
        searchEntity: {}
    },
    //分页查询显示
    methods: {
        searchList: function (curPage) {
            axios.post('/goods/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/goods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/goods/findPage.shtml', {
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
        //该方法只要不在生命周期的
        add: function () {
            axios.post('/goods/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/goods/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save: function () {
            if (this.entity.id != null) {
                this.update();
            } else {
                this.add();
            }
        },
        findOne: function (id) {
            axios.get('/goods/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/goods/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //定义一个函数，根据分类的id获取到分类的名称并展示在页面
        findAllItemCategory: function () {
            //向后台发送请求获取要查询的数据,查询分类，请求发送到itemCat的Controller中查询
            axios.get('/itemCat/findAll.shtml').then(
                function (response) {

                    alert("查询分类级别1")
                    //data中定义一个变量来接受查询到的数据
                    //将查询到的数据进行遍历
                    for (var i = 0; response.data.length; i++) {

                        app.itemCatList[response.data[i].id] = response.data[i].name;
                    }
                    //重新手动刷新页面
                    app.$mount("#app");
                }
            )
        },


        },
        //修改上下架状态 修改1为上架 0为下架
        changeIsMarketable:function (status) {


            if (status == 1) {
                var url = "/goods/marketable.shtml"
            }else {
                var url = "/goods/unmarketable.shtml"
            }
            axios.post(url,this.ids).then(function (response) {

                console.log(response.data)

                if (response.data.success) {
                    alert(response.data.message)
                }else {
                    alert(response.data.message)
                }

                app.$mount("#app");
            })
        }


    },
    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);
        //钩子函数初始化
        this.findAllItemCategory();


    }

})

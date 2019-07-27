var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{status:'0'}
    },
    methods: {


        searchList:function (curPage) {


            axios.post('/seller/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {


                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/seller/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
        //商家管理的分页查询审核状态
         findPage:function () {
            var that = this;
            axios.get('/seller/findPage.shtml',{params:{
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
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/seller/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/seller/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        //根据id查询回显
        findOne:function (id) {
            axios.get('/seller/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/seller/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //修改与审核商家信息的的函数
        updateStatus:function (sellerId,status) {
            //1.向服务器发送异步请求，并携带商家ID与审核状态参数
            axios.get('/seller/updateStatus.shtml',{
               //这是get请求的携带参数的一种方法
                params:{
                    sellerId:sellerId,
                    status:status
                }
            }).then(function (response) {
                //2.获取服务器响应的结果，进行判断是否修改成功
                if(response.data.success){

                    //修改成功,刷新页面
                    app.searchList(1);
                }else {
                    //修改失败
                    alert(response.data.message);
                }

            })
        }


    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);

    }

})



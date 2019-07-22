var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{},
        //根据广告分类的id查询到的广告列表的数据，绑定广告列表数据
        contentList:[],
        //关键字变量
        keywords:'',
        //定义一个广告列表展示的要传递的id参数 ,1表示首页轮播，5、6表示广告楼层的展示
        categoryId:['1','5','6'],

    },
    methods: {
        searchList:function (curPage) {
            axios.post('/content/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/content/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/content/findPage.shtml',{params:{
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
            axios.post('/content/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/content/update.shtml',this.entity).then(function (response) {
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
        findOne:function (id) {
            axios.get('/content/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/content/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //根据广告分类的id查询广告列表
        findByCategoryId:function () {

           // alert("id"+categoryId)
            //向服务器发送请求
            axios.post('/content/findByCategoryId.shtml',this.categoryId).then(function (response) {

                alert("根据广告分类id查询显示的广告列表")

                //将查询到的数据绑定一个列表的变量
                console.log(response.data)
                app.contentList = response.data;

            }).catch(function (error) {
                alert(error)
            });
        },

        //定义一个点击此函数从首页跳转到根据此关键字搜索到的搜索页面
        doSearch:function () {
            //因为是页面的路径跳转携带参数，所以需要先将携带的中文参数进行转码
            var keywordsx = encodeURIComponent(this.keywords);

            window.location.href="http://localhost:9104/search.html?keywords="+keywordsx;
        }


    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);

        this.findByCategoryId();

    }

})

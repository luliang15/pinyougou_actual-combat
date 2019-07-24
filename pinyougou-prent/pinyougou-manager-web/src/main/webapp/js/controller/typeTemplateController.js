var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{customAttributeItems:[]}, //初始化，entity是下拉框要添加的数据的总集合，一开始给初始化为空

        //页面加载的时候，发送请求，获取所有的品牌列表的数据，组合成如下的格式，赋值给该变量
        brandOptions:[],
        //页面加载的时候，发送请求，获取所有的规格列表的数据，组合成{'id':1,text:'颜色'}的格式，赋值给该变量
        specOptions:[],
        ids:[],
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/typeTemplate/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/typeTemplate/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/typeTemplate/findPage.shtml',{params:{
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
            axios.post('/typeTemplate/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/typeTemplate/update.shtml',this.entity).then(function (response) {
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
        //修改时的回显
        findOne:function (id) {

            axios.get('/typeTemplate/findOne/'+id+'.shtml').then(function (response) {

                app.entity=response.data;
                //将json字符串转成json对像(js对象)
                app.entity.brandIds=JSON.parse(app.entity.brandIds);
                app.entity.customAttributeItems=JSON.parse(app.entity.customAttributeItems);
                app.entity.specIds=JSON.parse(app.entity.specIds);

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        //删除的函数方法
        dele:function () {
            axios.post('/typeTemplate/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //定义一个查询所有品牌对象的函数
        findAllBrandOptions:function () {
            alert(66666);

            axios.get('/brand/findAll.shtml').then(function (response) {
          //下拉框想要获取到的对象格式[{'id':1,text:'联想'},{'id':2,text:'华为'}]
           // 服务器响应过来的的格式
            // response.data ====>[{id:1,name:"联想",firstChar:"L"}]  品牌列表
                var brandList = response.data;

                for(var i=0;i<brandList.length;i++){
                    var obj = brandList[i];   //循环遍历获取到的品牌列表元素{id:1,name:"联想",firstChar:"L"}
                    app.brandOptions.push({"id":obj.id,"text":obj.name});
                }

            }).catch(function (error) {
                //响应报错信息的地方
            })
        },

        //查询所有规格列表的数据回显
        findAllSpecOptions:function () {

            axios.get('/specification/findAll.shtml').then(function (response) {
                //下拉框想要获取到的对象格式[{'id':1,text:'颜色'},{'id':2,text:'桂萼'}]
                // 服务器响应过来的的格式
                // response.data ====>[{id:1,name:"联想",firstChar:"L"}]  品牌列表
                var specList = response.data;

                for(var i=0;i<specList.length;i++){
                    var obj = specList[i];   //循环遍历获取到的品牌列表元素{id:1,name:"颜色",firstChar:"L"}
                    app.specOptions.push({"id":obj.id,"text":obj.specName});
                }

            }).catch(function (error) {
                //响应报错信息的地方
            })
        },
        // 当点击新增按钮的时候调用，向已有的数组中添加一个{} (json对象)
        addTableRow:function () {
            //向数组中添加对象
            this.entity.customAttributeItems.push({});
        },
        //删点击删除按钮的时候，向已有的数组中删除对应的那个对象
        removeTableRow:function (index) {
            //
            this.entity.customAttributeItems.splice(index,1);
        },

        //将json对象转成字符串，以逗号拼接后返回
        jsonToString:function (strList,key) {

           // alert(strList)

           //定义一个空字符串
           var str = "";

           //1.将json字符串 转成 json对象

            var jsonObjArray = JSON.parse(strList);

            for(var i=0;i<jsonObjArray.length;i++){

                var obj = jsonObjArray[i];

                str+=obj[key]+",";
                // str+=obj.key+",";
            }

            if(str.length>0){
                str = str.substring(0,str.length-1);
            }

            return str;
        },


    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);
        //初始化的时候调用查询品牌列表的方法
        this.findAllBrandOptions();
        //初始化的时候调用查询规格列表的方法
        this.findAllSpecOptions();
    }

})

var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],//分类对象
        templateObj:[],//，欧版对象
        entity:{name:'',parentId:0,typeId:0}, //初始化，entity是下拉框要添加的数据的总集合，一开始给初始化为空
        newEntity:{name:'',parentId:0,typeId:0,status:'0'}, //用来存储到分类表中

        //页面加载的时候，发送请求，获取所有的品牌列表的数据，组合成如下的格式，赋值给该变量
        brandOptions:[],
        //页面加载的时候，发送请求，获取所有的规格列表的数据，组合成{'id':1,text:'颜色'}的格式，赋值给该变量
        specOptions:[],
        ids:[],
        searchEntity:{},
        templateName:[],
        itemCat1List:[],//分类回显
        templateNameList:[],//模板名称下拉回显
    },
    methods: {


        searchList:function (curPage) {
            axios.post('/itemCat/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                app.itemCat1List=[]
                var list = app.list;
                for (var i = 0; i <list.length ; i++) {

                    app.itemCat1List.push(list[i].name)

                }

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });



        },
     //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/itemCat/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;




            }).catch(function (error) {

            })

        },
         findPage:function () {
            var that = this;
            axios.get('/itemCat/findPage.shtml',{params:{
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

        //页面加载查询模板名称
        findTemplate:function () {

            axios.get("/typeTemplate/findAll.shtml").then((resp)=>{
                app.templateObj = resp.data
                var template = resp.data
                for (var i = 0; i <template.length ; i++) {
                    //得到每一个对象
                   var JsonStr = template[i];
                   var id = JsonStr.id;
                   var name = JsonStr.name;
                   //下拉回显
                    app.templateNameList .push(name)
                    //显示页面回显
                   app.templateName[id] = name;


                }


            })


            },



        //添加审核商品
        add:function () {
            axios.post('/itemCat/add.shtml',this.newEntity).then((response)=>{
                    if(response.data.success){
                        alert(response.data.message)
                        this.searchList(1);
                    }

            }).catch(function (error) {

            });
        },

    },

    //监听
    watch: {
        //newval参数为开始选中的列表信息，oldval为原本选中的列表信息
        'entity.parentId': function (newval, oldval) {
            //此时监听第一级 变量,二级变量根据一级变量的变化从而查询二级列表所要展示的信息
            //判断一级列表的类目不为空
            if (newval != undefined) {
                var cat1List = this.itemCat1List;
                var itemCatList = this.list
                for (var i = 0; i <itemCatList.length ; i++) {
                    if (newval==itemCatList[i].name){
                        this.newEntity.parentId = itemCatList[i].parentId

                    }

                }

            }
        }, //监听模板下拉的的变化
        'entity.typeId':function (newval,oldval) {
            //通过名称获取对应模板的数据的id
            if (newval!=undefined){
                var obj = this.templateObj
                for (var i = 0; i <obj.length; i++) {

                    if (newval==obj[i].name){
                        this.newEntity.typeId = obj[i].id
                    }

                }
            }



        }



    },

        //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);
        this.findTemplate();
        //初始化的时候调用查询品牌列表的方法
   /*
        //初始化的时候调用查询规格列表的方法
        this.findAllSpecOptions();*/
    }

})

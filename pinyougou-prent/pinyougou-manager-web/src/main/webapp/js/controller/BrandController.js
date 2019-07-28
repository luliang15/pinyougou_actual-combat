//发送vue的axios请求
var app = new Vue({
    el:"#app",
    data:{
        //定义变量的地方  []表示数字，{}表示集合
        pages:15,   //总页数
        pageNo:1,   //当前页初始值为第一页
        list:[],     //获取到服务器响应数据，数组形式
        entity:{} ,  //品牌对象,绑定页面、
        ids:[] ,  //为品牌绑定删除的ids数组参数
        searchEntity:{},   //搜索条件对象
        jsonList:[],
        message:"暂无数据请先导入数据",

    },
    methods:{

        //审核状态
        updateStatus: function (ids,status) {
            axios.post("/brand/updateStatus.shtml?status="+status,ids).then((resp)=>{
                app.ids=[]
                app.searchList(1)
            })


        },

        searchList:function(curPage){


            //发送axios请求,发送post请求，传入当前页(默认为1)的参数，回调函数
            axios.post('/brand/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {

                //获取数据,将服务器响应回来的数据赋给前端的list中
                app.list = response.data.list;



                //当前页
                app.pageNo = response.data.pageNum;
                //总页数
                app.pages = response.data.pages;
            });

            /*//发送axios请求,发送post请求，传入当前页(默认为1)的参数，回调函数
           axios.post('/brand/findPage.shtml?pageNo='+curPage).then(function (response) {
               //获取数据,将服务器响应回来的数据赋给前端的list中
               app.list = response.data.list;

               //当前页
               app.pageNo = curPage;
               //总页数
               app.pages = response.data.pages;
           });*/
        },

        //添加品牌的方法
        add:function(){
            //this.entity，entity已经绑定了页面，所以有数据的
            axios.post('/brand/add.shtml',this.entity).then(function (response) {

                //判断添加是否成
                if(response.data.success){
                    //添加成功，刷新页面
                    app.searchList(1);
                }else{
                    //添加失败，响应失败信息
                    alert(response.data.message);
                }
            })
        },

        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/brand/findAll.shtml').then(function (response) {

                console.log(response);
                //注意；this 在axios中就不再是vue实例了
                app.list  = response.data;
            }).catch(function (error) {
                //服务器报异常的地方
            })
        },

        //当点击修改的时候，根据点击到的品牌的ID发送请求 获取数据赋值给变量entity
        findOne:function (id) {

            axios.get('/brand/findOne/'+id+'.shtml').then(function (response) {
                //将服务器响应回来的数据赋值给变量entity
                app.entity = response.data;
            })
        },
        //修改更新品牌的方法
        update:function () {

            axios.post('/brand/update.shtml',this.entity).then(function (response) {
                //将获取的响应数据进行判断更新是否成功
                if(response.data.success){
                    //更新成功,刷新页面
                   app.searchList(1);
                }else {
                    //更新失败，响应失败信息
                    alert(response.data.message);
                }
            });
        },

        //判断是更新还是新增
        save:function () {
            //如果为空或者没定义
            if(this.entity.id == null || this.entity.id == undefined){
            //新增保存
                this.add();
            }else {
              //更新
              this.update();
            }
        },
        //删除的方法
        dele:function () {
            //发送请求
            axios.post('/brand/delete.shtml',this.ids).then(function (response) {
                //判断删除是否成功
                if(response.data.success){
                    //删除成功,刷新页面，并清空ids参数数组的值
                    app.ids = [];  //清空
                    app.searchList(1);
                }else {
                    //删除失败、显示报错信息
                    alert(response.data.message);
                }
            })
        },

        uploadTemplate:function(){
            location.href="../../download/brandsql.xls"

        },

        uploadBefore:function(){
            var formData = new FormData() // 声明一个FormData对象
            this.formData = new window.FormData() // vue 中使用 window.FormData(),否则会报 'FormData isn't definded'
            this.formData.append('file', document.querySelector('input[type=file]').files[0]) // 'userfile' 这个名字要和后台获取文件的名字一样;
            let file = document.querySelector('input[type=file]').files[0]
            let fileName = file.name.substring(file.name.lastIndexOf(".")+1,file.name.length)
            const fileType = fileName == 'xls';
            if (!fileType) {
                alert('上传文件格式为xls，请检查文件格式');
            }
        },

        //模拟form表单文件上传
        //excle文件上传
        uploadFile:function(){

            //创建一个表单对象，html中的
            var formData = new FormData();
            //添加字段 类似：<input type="text" name="username">
            //file ===> <input type="file" name="file">
            //file.files[0]
            formData.append('file', file.files[0]);


            axios({
                url:'http://localhost:9101/brand/upload.shtml',
                //表示模拟一个表单上传，表单数据
                data: formData,
                method: 'post',
                //设置表单提交的数据类型
                headers: {
                    'Content-Type': 'multipart/form-data'
                },


            }).then(function (response) {
                if (response.data.success) {
                    app.jsonList = JSON.parse(response.data.message)
                    //上传成功，会打印出上传的路径

                } else {
                    //上传失败
                    alert(response.data.message);
                }
            })
        },
        //poi数据导入
        daoRu:function () {
            if (this.jsonList==''){
                alert("不要作死，请先上传数据文件")
                return false
            } else {
                axios.post("/brand/into.shtml",this.jsonList).then((resp)=>{
                    if (resp.data.message){
                        alert("导入成功")
                        location.reload()
                    } else {
                        alert("导入失败")
                    }
                })
            }

        },


        /* //根据条件进行模糊查询的方法
         searchEntity:function () {

         }*/

    },
    //钩子函数，起初始化的作用
    created:function () {

        //发送请求 获取JSON数据赋值给 变量 绑定到 表格中 循环遍历
        //this.findAll();
        this.searchList(1);//给1是因为默认查询的就是第一页
    }

})


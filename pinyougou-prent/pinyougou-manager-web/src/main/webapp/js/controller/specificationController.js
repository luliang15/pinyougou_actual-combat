var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        //specification:{}代表一个规格选项表的对象
        //optionList:[]代表一个规格可能有多个选项，使用数组容器装
        entity: {specification: {}, optionList: [{}]},
        ids: [],
        searchEntity: {},
        jsonList:[],
        message:"暂无数据请先导入数据",
        fileName:"",
        fileNameErr:"",
    },
    methods: {

        /*
        *    axios.get('/seller/updateStatus.shtml',{
                params:{
                        sellerId:id,
                       status:status
                }
            })
        *
        * */

        updateStatus: function (ids,status) {
            axios.post("/specification/updateStatus.shtml?status="+status,ids).then((resp)=>{
                app.ids=[]
                app.searchList(1)
            })


        },


        searchList: function (curPage) {
            axios.post('/specification/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
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
            axios.get('/specification/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/specification/findPage.shtml', {
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
            axios.post('/specification/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/specification/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        //判断是新增还是修改，有参数时修改，没参数时新增
        save: function () {
            if (this.entity.specification.id != null) {
                this.update();
            } else {
                this.add();
            }
        },
        findOne: function (id) {
            axios.get('/specification/findOne/' + id + '.shtml').then(function (response) {
                //服务器响应的查询结果
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        dele: function () {
            axios.post('/specification/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //当点击新增按钮的时候调用，向已有的数组中添加一个{} (json对象)
        addTableRow: function () {
            //表示向数组中添加一个对象
            this.entity.optionList.push({});
        },

        //删点击删除按钮的时候，向已有的数组中删除对应的那个对象
        removeTableRow: function (index) {
            //第一个参数表示索引号(下标)
            //第二个参数表示要删除的个数
            this.entity.optionList.splice(index, 1);
        },


        //规格模板下载
        uploadTemplate: function () {
            location.href = "../../download/specification.xls"

        },

        //excle文件上传
        uploadFile: function () {
            //创建一个表单对象，html中的
            var formData = new FormData();

            //添加字段 类似：<input type="text" name="username">
            //file ===> <input type="file" name="file">
            //file.files[0]
            formData.append('file', file.files[0]);

            axios({
                url: 'http://localhost:9101/specification/upload.shtml',
                //表示模拟一个表单上传，表单数据
                data: formData,
                method: 'post',
                //设置表单提交的数据类型
                headers: {
                    'Content-Type': 'multipart/form-data'
                },


            }).then(function (response) {
                if (response.data.success) {
                    app.jsonList =JSON.parse(response.data.message)

                } else {
                    //上传失败
                    alert(response.data.message);
                }
            })
        },
        //格式校验
        uploadBefore:function(){

            var formData = new FormData() // 声明一个FormData对象
            this.formData = new window.FormData() // vue 中使用 window.FormData(),否则会报 'FormData isn't definded'
            this.formData.append('file', document.querySelector('input[type=file]').files[0]) // 'userfile' 这个名字要和后台获取文件的名字一样;
            let file = document.querySelector('input[type=file]').files[0]
            let fileName = file.name.substring(file.name.lastIndexOf(".")+1,file.name.length)
            const fileType = fileName == 'xls';
            this.fileName="";
            this.fileNameErr="";
            if (!fileType) {
                this.fileNameErr="文件格式为xls,请检查文件格式";
            }else {
                this.fileName = file.name
            }
        },

        //poi数据导入
        daoRu:function () {
            if (this.jsonList==''){
                alert("不要作死，请先上传数据文件")
                return false
            } else {
                axios.post("/specification/into.shtml",this.jsonList).then((resp)=>{
                    if (resp.data.success){
                        alert("导入成功")
                        location.reload()
                    } else {
                        alert("导入失败")
                        location.reload()
                    }
                })
            }

        },


    },
    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);

    }

})

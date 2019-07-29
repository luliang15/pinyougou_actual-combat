var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        searchEntity: {},
        loginName: '',
        entity_1: {},
        seckillGoods: {
            title: '',
            price: '',
            costPrice: '',
            num: '',
            status: '0',
            smallPic: '',
            introduction: '',
            stockCount: ''
        },
        newStartTime: '',
        newEndTime: '',
    },
    methods: {
        /*
        *     params.append(' startTime ',this.seckillGoods.startTime);
                    params.append(' endTime ',this.seckillGoods.endTime);
                    params.append(' seckillGoods ',this.seckillGoods);
        *
        * */
        add: function () {
            this.newStartTime = $("#test04").val();
            this.newEndTime = $("#test05").val();
            //拼接格式，方便后台转换
            axios.post('/seckillGoods/add.shtml?startTime=' + this.newStartTime + '&endTime=' + this.newEndTime, this.seckillGoods).then(function (response) {
                if (response.data.success) {
                    alert("添加审核成功")
                    app.seckillGoods = {},
                        app.newStartTime = '',
                        app.newEndTime = '',
                        app.findPage();
                }
            }).catch(function (error) {
            });
        },


        searchList: function (curPage) {
            axios.post('/seckillGoods/findPageGoods.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                //注意：this 在axios中就不再是 vue实例了。
                var newData = response.data.list;
                for (var i = 0; i < newData.length; i++) {
                    var newEndTime = app.formatDate(newData[i].endTime);
                    var newStartTime = app.formatDate(newData[i].startTime);
                    //格式时间戳格式
                    newData[i].startTime = newStartTime;
                    newData[i].endTime = newEndTime;
                }
                app.list = newData;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            axios.get('/seckillGoods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                //注意：this 在axios中就不再是 vue实例了。
                var newData = response.data.list;
                for (var i = 0; i < newData.length; i++) {
                    var newEndTime = app.formatDate(newData[i].endTime);
                    var newStartTime = app.formatDate(newData[i].startTime);
                    //格式时间戳格式
                    newData[i].startTime = newStartTime;
                    newData[i].endTime = newEndTime;
                }
                app.list = newData;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/seckillGoods/findPage.shtml', {
                params: {
                    pageNo: this.pageNo
                }
            }).then(function (response) {
                //注意：this 在axios中就不再是 vue实例了。
                var newData = response.data.list;
                for (var i = 0; i < newData.length; i++) {
                    var newEndTime = app.formatDate(newData[i].endTime);
                    var newStartTime = app.formatDate(newData[i].startTime);
                    //格式时间戳格式
                    newData[i].startTime = newStartTime;
                    newData[i].endTime = newEndTime;
                }
                app.list = newData;

                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            }).catch(function (error) {

            })
        },

        //格式化时间戳
        formatDate: function (value) {
            let date = new Date(value);
            let y = date.getFullYear();
            let MM = date.getMonth() + 1;
            MM = MM < 10 ? ('0' + MM) : MM;
            let d = date.getDate();
            d = d < 10 ? ('0' + d) : d;
            let h = date.getHours();
            h = h < 10 ? ('0' + h) : h;
            let m = date.getMinutes();
            m = m < 10 ? ('0' + m) : m;
            let s = date.getSeconds();
            s = s < 10 ? ('0' + s) : s;
            return y + '-' + MM + '-' + d + ' ' + h + ':' + m + ':' + s;
        },

        update: function () {
            axios.post('/seckillGoods/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.findPage();
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
            axios.get('/seckillGoods/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/seckillGoods/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.findPage();
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        //获取登录名
        getUsername: function () {
            axios.get('/login/name.shtml').then(function (response) {
                app.loginName = response.data;
                app.searchEntity.sellerId = response.data;
                app.searchList(1);
            }).catch(function (error) {
                console.log(error);
            });
        },


        //文件上传的函数方法
        uploadFile: function () {


            //创建一个表单对象，html中的
            var formData = new FormData();

            //添加字段 类似：<input type="text" name="username">
            //file ===> <input type="file" name="file">
            //file.files[0]
            formData.append('file', file.files[0]);

            axios({
                url: 'http://localhost:9110/upload/uploadFile.shtml',
                //表示模拟一个表单上传，表单数据
                data: formData,
                method: 'post',
                //设置表单提交的数据类型
                headers: {
                    'Content-Type': 'multipart/form-data'
                },

                //开启跨域请求携带相关认证信息
                withCredentials: true

            }).then(function (response) {

                if (response.data.success) {
                    //上传成功，会打印出上传的路径
                    //将图片路径赋值给变量
                    app.seckillGoods.smallPic = response.data.message;
                } else {
                    //上传失败
                    alert(response.data.message);
                }
            })
        },


        newPage: function () {
            window.location.href = "http://localhost:9102/admin/seckill_goods.html"
        }
    },
    //钩子函数 初始化了事件和
    created: function () {

        this.getUsername();
        this.findPage();
    }

})


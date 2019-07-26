
var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{},
        status:['审核未通过','审核通过']
    },
    methods: {
        //批量审核秒杀的商品
        updateStatus:function (status) {
            alert(666)
            axios.post('/seckillGoods/updateByStatus.shtml?status='+status,this.ids).then(function (response) {
                if(response.data.success){
                    //清空选择
                    app.ids=[];
                    app.searchList(1);
                }
            });
        },

        searchList:function (curPage) {
            axios.post('/seckillGoods/findPage.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                var aa=response.data.list;
                for(var i = 0; i < aa.length; i++){
                    var obj = app.formatDate(aa[i].startTime)
                    var obj1 = app.formatDate(aa[i].endTime)

                    aa[i].startTime = obj
                    aa[i].endTime = obj1
                }

                app.list = aa;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/seckillGoods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
        findPage:function () {
            var that = this;
            axios.get('/seckillGoods/findPage.shtml',{params:{
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
            axios.post('/seckillGoods/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.findPage();
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/seckillGoods/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.findPage();
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
            axios.get('/seckillGoods/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/seckillGoods/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.findPage();
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        formatDate: function (value) {// 时间戳转换日期格式方法
            if (value == null) {
                return '';
            } else {
                let date = new Date(value);
                let y = date.getFullYear();// 年
                let MM = date.getMonth() + 1;// 月
                MM = MM < 10 ? ('0' + MM) : MM;
                let d = date.getDate();// 日
                d = d < 10 ? ('0' + d) : d;
                let h = date.getHours();// 时
                h = h < 10 ? ('0' + h) : h;
                let m = date.getMinutes();// 分
                m = m < 10 ? ('0' + m) : m;
                let s = date.getSeconds();// 秒
                s = s < 10 ? ('0' + s) : s;
                return y + '-' + MM + '-' + d + ' ' + h + ':' + m + ':' + s;

            }
        },




    },
    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);
    }

})


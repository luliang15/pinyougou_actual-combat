﻿var app = new Vue({
    el: "#app",
    data: {
        pages:2,
        pageNo:1,
        list:[],
        entity:{},  //要封装的数据
        smsCode:'',// 页面输入的验证码
        ids:[],
        searchEntity:{},
        loginName:'',  //获取到的用户名
<<<<<<< HEAD
        num: 1,
        // sku:skuList[0],
        //定义一个变量来接受根据用户名查询到的订单列表的数据
        orderList:[],

=======
>>>>>>> 815ef515ae210cf603c1cbc95c6c7c8bac0b89bf
    },
    methods: {

        //该方法用来注册新用户的
        add:function () {

            var that=this;

            this.$validator.validate().then(

                function (result) {

                    if(result){
                        console.log(that);
                        axios.post('/user/add/'+that.smsCode+'.shtml',that.entity).then(function (response) {
                            if(response.data.success){
                                //跳转到其用户后台的首页
                                window.location.href="home-index.html";
                            }else{
                                that.$validator.errors.add(response.data.errorsList);
                            }

                        }).catch(function (error) {
                            console.log(error);
                        });
                    }
                }
            )
        },


        //当点击发送验证码的时候向用户发送验证码的函数方法
        sendCode:function () {

            axios.get('/user/sendCode.shtml?phone='+this.entity.phone).then(
                function (response) {
                    if(response.success.data){
                        //发送是否成功的消息
                        alert(response.data.message);
                    }else {
                        //如果发送失败
                        alert("验证码生成失败！！！")
                    }
                }
            )
        },
        //获取登录名
        getName:function () {

            axios.get('/login/name.shtml').then(function (response) {
                app.loginName=response.data;

            }).catch(function (error) {
                console.log(error);
            });
        },
<<<<<<< HEAD
        //添加购物车
        addGoodsToCart:function () {
            alert("addGoodsToCart")
            axios.get('http://localhost:9107/cart/addGoodsToCartList.shtml?itemId='+this.sku.id+'&num='+this.num).
            then(
                function (response) {//response.data=result
                    if(response.data.success){
                        window.location.href="http://localhost:9107/cart.html";
                    }else{
                        alert("错了");
                    }
                }
            )
        },

        //创建一个点击我的订单触发查询用户中心订单的函数方法http://localhost:9106/user/findUserIdOrder.shtml
        findByUserOrder:function (currPage) {

            axios.get('/user/findUserIdOrder.shtml?pageNo='+currPage).then(

                function (response) { //后台响应的list<List<Map>>的封装好的数据

                    //将数据赋予变量  获取到的是一个Map对象
                    //map中有两对键值对，第一个是订单需要展示的数据，第二个是分页展示需要的数据
                    app.orderList = response.data;

                    //第一页
                    app.pageNo = response.data.pageInfo.pageNum;

                    //每页显示条数
                    app.pages = response.data.pageInfo.pages;
                    //alert("66")

                }
            )

        },

=======
        getGoodsHref:function () {
            window.location.href='home-setting-info.html';
        }
>>>>>>> 815ef515ae210cf603c1cbc95c6c7c8bac0b89bf
    },
    //钩子函数 初始化了事件和
    created: function () {

        //初始化的时候就显示用户名称
        this.getName();
<<<<<<< HEAD

        //页面加载的时候就显示
        this.findByUserOrder('1');

=======
>>>>>>> 815ef515ae210cf603c1cbc95c6c7c8bac0b89bf
    }

})

var app = new Vue({
    //1.与前端对应的标识
    el:"#app",
    //2.放置属性的地方
    data:{
        pages:15,
        pageNo:1,
        //表示购物车列表的变量
        cartList:[],  // 定义的从后台获取到的购物车列表的数据的变量
        entity:{},
        totalMoney:0,  //设置变量，总小计的金额
        totalNum:0,  //总数量
        ids:[],
        searchEntity:[],
        username:'',
        addressList:[],    //定义一个变量，用户的地址集合
        address:{},    //定义一个变量
        //这里需要给paymentType定义一个默认值。如果不给默认值，页面不会渲染
        order:{/*paymentType:'1'*/},     //定义订单对象，用于绑定页面上所有需要提交的数据

    },
    //3.放置方法的地方
    methods:{

        //此函数表示页面加载时，从后台根据用户从cookie或者redis中获取到购物车列表的数据，然后将值赋予给购物车列表的变量
        findCartList: function () {

            alert("送货清单")
            //变量初始化都是0
            this.totalMoney = 0;
            this.totalNum = 0;

            axios.get('/cart/findCartList.shtml').then(function (response) {

                //获取购物车列表数据
                app.cartList = response.data;

                //获取到购车列表的数据
                var obj = app.cartList;

                console.log(obj)

                //遍历购物车的明细列表
                for (var i = 0; i <obj.length; i++) {
                    //每一个购物车对象
                        var cart = obj[i];
                        //还需遍历购物车对象
                    for (var j = 0; j <cart.orderItemList.length ; j++) {
                        //累计总数量
                        app.totalNum+=cart.orderItemList[i].num;

                        //累计总金额
                        app.totalMoney+=cart.orderItemList[i].totalFee;

                    }
                }




            });
        },

        /**
         *       向已有的购物车添加商品，或者减去商品
         * @param itemId  商品id
         * @param num  商品数量
         */
        addGoodsToCartList:function (itemId,num) {
            //向后台发送请求
            axios.get('/cart/addGoodsToCartList.shtml', {
                params: {
                    itemId:itemId,  //这里是传递参数
                    num:num
                }
            }).then(function (response) {

                if(response.data.success){
                    //添加成功，刷新页面
                    app.findCartList();
                }else{
                    //添加失败，响应错误信息
                    alert(response.data.message);
                }
            });
        },

        //页面加载的时候显示登录的用户名称
        getName:function () {
            //发送请求获取用户名称
            axios.get('/cart/getName.shtml').then(function (response) {

                app.username = response.data;

                if(app.username == 'anonymousUser'){
                        app.username = '';
                }
            })
        },

        //，当页面加载时，定义一个根据用户名查询用户地址的函数方法
        findAddressList:function () {

            axios.get('/address/findAddressListByUserId.shtml').then(
                function (response) {

                    app.addressList = response.data;

                    //遍历地址列表 找出是否是默认地址值，如果是，默认勾选
                    for (var i = 0; i <app.addressList.length ; i++) {

                        //判断如果是默认地址值
                        if(app.addressList[i].isDefault=='1'){
                            app.address = app.addressList[i];
                            break;
                        }
                    }

                }
            )
        },
        //定义一个函数，传入一个参数， 当点击这个变量的时候，影响这个变量
        selectAddress:function (address) {
            this.address = address;

        },
        //再定义一个函数，当点击的时候传入地址参数与之对比，判断匹不匹配，如果匹配，则默认勾选
        isSelectedAddress:function (address) {

            //判断
            if(this.address==address){
                return true;  //勾选
            }
            return false;
        },

        //定义一个绑定支付类型的并传入返回类型的函数，用于指定订单的支付方式
        selectType:function (type) {

            //使用页面渲染的方式
            this.$set(this.order,'paymentType',type)

            //指定支付类型
            //this.order.paymentType = type;
        },

        //购物车添加订单的函数方法
        submitOrder:function () {
            //设置值
            this.$set(this.order,'receiverAreaName',this.address.address);
            this.$set(this.order,'receiverMobile',this.address.mobile);
            this.$set(this.order,'receiver',this.address.contact);
            axios.post('/order/add.shtml',this.order).then(
                function (responde) {
                    //判断提交是否成功
                    if(responde.data.success){
                        //跳转到支付页面
                        window.location.href="pay.html";
                    }else {
                        alert("提交失败！！")
                    }

                }
            )
        },

        //定义一个点击函数，点将商品移到我的收藏中,点击移到我的关注的时候跳转到用户中心的我的收藏
        MoveMyFavorite:function (itemId) {

            axios.get('http://localhost:9106/user/favorite.shtml',{
                params:{
                    itemId:itemId
                },
                //客户端在ajax的时候也要携带cookie到服务器
                withCredentials:true
                }
               ).then(
                function (response) {  //获取到的map对象

                    //判断移到我的收藏是否成功
                    if(response.data.success){

                        window.location.href = "http://localhost:9106/home-index.html"
                    }else {
                        alert("移到我的收藏失败！！！")
                    }
                }
            )
        }

    },

    //4.钩子函数，一些显示数据可初始化的地方
    created:function () {

        //页面加载时调用
        this.findCartList();

        //判断调用哪一个页面时加载
        if(window.location.href.indexOf("getOrderInfo.html")!=-1)

        this.findAddressList();

        this.getName();
    }


});
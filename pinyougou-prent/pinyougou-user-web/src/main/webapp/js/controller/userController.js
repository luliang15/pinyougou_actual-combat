var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},  //要封装的数据
        smsCode:'',// 页面输入的验证码
        ids:[],
        searchEntity:{},
        loginName:'',  //获取到的用户名
        num: 1,
        // sku:skuList[0],
        //定义一个变量来接受根据用户名查询到的订单列表的数据
        orderList:[],

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

                alert("999")
                app.loginName=response.data;

            }).catch(function (error) {
                console.log(error);
            });
        },//循环遍历SKU的列表数组
        // 判断 当前的变量的值是否在数组中存在,如果存在,将对应的数组的元素赋值给变量sku

        search:function () {
            for(var i=0;i<skuList.length;i++){
                //{"id":14383881,"title":"iphonex60 移动3G 16G","price":0.01,spec:{"网络":"移动3G","机身内存":"16G"}}
                var obj = skuList[i];//
                if(JSON.stringify(this.specificationItems)==JSON.stringify(obj.spec)){
                    this.sku=obj;
                    break;
                }
            }
        },
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
        findByUserOrder:function () {

            axios.get('/user/findUserIdOrder.shtml').then(

                function (response) { //后台响应的list<List<Map>>的封装好的数据

                    alert("66")
                    //将数据赋予变量  获取到的List<ListMap>
                     app.orderList = response.data;



                }
            )

        },

    },

    //钩子函数 初始化了事件和
    created: function () {

        //初始化的时候就显示用户名称
        this.getName();

        //页面加载的时候就显示
        this.findByUserOrder();

    }

})

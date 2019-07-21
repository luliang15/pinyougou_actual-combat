var app = new Vue({
    //1.与前端对应的标识
    el:"#app",
    //2.放置属性的地方
    data:{
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        timeString:'',  //定义一个绑定页面的倒计时的变量
        ids:[],
        searchEntity:[],
        seckillId:0,   //秒杀商品的id
        goodsInfo:{count:''} ,  //设置秒杀商品的存库

        messageInfo:'',  //接收后台响应的抢购成功或者失败的消息
    },
    //3.放置方法的地方
    methods:{


        /**
         *
         * @param alltime 为 时间的毫秒数。
         * @returns {string}
         */
        convertTimeString:function(alltime){
            var allsecond=Math.floor(alltime/1000);//毫秒数转成 秒数。
            var days= Math.floor( allsecond/(60*60*24));//天数
            var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小数数
            var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
            var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
            if(days>0){
                days=days+"天 ";
            }
            if(hours<10){
                hours="0"+hours;
            }
            if(minutes<10){
                minutes="0"+minutes;
            }
            if(seconds<10){
                seconds="0"+seconds;
            }

            if(days<=0){
                days='';
            }
            return days+hours+":"+minutes+":"+seconds;
        },



        //定义一个倒计时的函数方法， time:代表倒计时的时间毫秒数
        caculate:function (time) {

            //setInterval中的参数：function倒计时的函数   1000：表示倒计时的毫秒数
           var clock =  window.setInterval(function () {

                time=time-1000;

                app.timeString = app.convertTimeString(time);   //从毫秒数转成秒数

               if(time<=0){
                   //停止计秒
                    window.clearInterval(clock);
               }

            },1000)
        },

        //秒杀商品提交下单的函数
        submitOrder:function () {
            //alert(66666666666)

            axios.get('/seckillOrder/submitOrder/'+this.seckillId+'.shtml').then(
                function (response) {

                    if(response.data.success){
                        //如果在登录的情况下，则订单抢购成功
                       //alert(response.data.message)
                        app.messageInfo=response.data.message

                    }else {

                        if (response.data.message == '403') {
                            //否则报403需要登录再抢购 从秒杀抢购页面跳转到cas登录页面，
                            // 登录过后又重定向跳回秒杀抢购页面
                            var url = window.location.href;
                            //跳转到一个在未登录的情况下会被拦截的页面下
                            window.location.href = "http://localhost:9111/page/login.shtml?url="+url;

                        }else {
                            //或者报错。抢购失败
                          app.messageInfo = response.data.message;
                        }
                    }
                }
            )
        },

        //定义一个从后台获取秒杀结束时间与秒杀商品库存符的函数 ,根据商品id
        getGoodsById:function (id) {


            axios.get('/seckillGoods/getGoodsById.shtml?id='+id).then(
                function (response) { //map

                    //获取到秒杀结束时间
                    app.caculate(response.data.time);
                    //获取到秒杀商品存库
                    app.goodsInfo.count = response.data.count;
                }
            )
        },

        //查询订单的状态 当点击立即抢购之后执行。
        queryStatus:function () {

            var count =0;
            //三秒钟执行一次发送请求
            var queryorder = window.setInterval(function () {
                count+=3;
                axios.get('/seckillOrder/queryOrderStatus.shtml').then(
                    function (response) {
                        if(response.data.success){
                            //跳转到支付页面,此时订单创建成功，就停止循环发送请求
                            window.clearInterval(queryorder);

                            alert("订单创建成功");
                            window.location.href="pay/pay.html";

                        }else{
                            if(response.data.message=='403'){
                                //要登录
                            }else{
                                //不需要登录需要提示
                                app.messageInfo=response.data.message+"....."+count;
                            }
                        }
                    }
                )
            },3000)//表示3秒

        },


    },

    //4.钩子函数，一些显示数据可初始化的地方
    created:function () {

        var obj = this.getUrlParam();

        //获取秒杀商品的距离结束时间  ,传入秒杀商品id
        this.getGoodsById(obj.id)

        //获取秒杀商品的id
        this.seckillId = obj.id;

        //alert(obj.id);

    }


});
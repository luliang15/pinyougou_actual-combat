var app = new Vue({
    //1.与前端对应的标识
    el:"#app",
    //2.放置属性的地方
    data:{
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:[],
        //封装支付的金额 二维码连接 交易订单号  code_url:'',total_fee:0,out_trade_no:''
        payObject:{code_url:'',total_fee:0,out_trade_no:''},

        money:0,
    },
    //3.放置方法的地方
    methods:{

        //用于生成支付宝二维码的函数方法
        createNative:function () {

            axios.get('/pay/createNative.shtml').then(
                function (response) {

                    //alert("88")
                    app.payObject = response.data;

                    //使用qrious生成二维码的插件,此时二维码生成成功
                    var qr = new QRious({
                        element:document.getElementById('qrious'),
                        size:250,
                        level:'H',
                        value:app.payObject.code_url
                    });

                    //支付的二维码生成成功，就一直调用查询订单是否支付完成的状态
                    app.queryStatus(app.payObject.out_trade_no);

                }
            )
        },

        //根据订单号查询该订单号的支付的状态
        queryStatus:function (out_trade_no) {

            axios.get('/pay/queryStatus.shtml?out_trade_no='+out_trade_no).then(
                function (response) {
                    //判断是否支付成功
                    if(response.data.success){
                        //支付成功  并显示支付金额
                        window.location.href = "paysuccess.html?money="+app.payObject.total_fee;


                    }else {
                        //支付失败有两种情况，一种失败，一种超时
                        if(response.data.message == "Payment overTime"){
                            //超时的错误
                            //1.设置 二维码过期 调用微信的关闭订单的接口
                            //2.直接生成新的二维码  替换掉原来的二维码 并且 关闭之前的订单 。采用这种
                            app.createNative();

                        }else {
                            //支付失败的错误
                            window.location.href = "payfail.html";
                        }
                    }
                }
            )

        },




    },

    //4.钩子函数，一些显示数据可初始化的地方
    created:function () {

        var urlParam = this.getUrlParam();

        var money = urlParam.money;

        this.money = money;

        //alert(money)

        this.createNative();
    }


});
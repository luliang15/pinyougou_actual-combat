var app = new Vue({
    //1.与前端对应的标识
    el:"#app",
    //2.放置属性的地方
    data:{
        //因为前端的html要绑定变量，这里定义一个变量
        entity:{},

    },
    //3.放置方法的地方
    methods:{

        //商家申请入住的注册账户的方法
        register:function () {

            //前端绑定点击申请注册的事件，这里发送请求给服务器,this.entity相当于一个JavaBean
            axios.post('/seller/add.shtml',this.entity).then(function (response) {

                //判断注册是否成功
                if(response.data.success){
                    //申请注册成功跳转到登录页面
                    window.location.href='shoplogin.html';
                }else {
                    //注册失败。可弹框显示失败信息
                    alert(response.data.message);
                }
            })
        }
    },

    //4.钩子函数，一些显示数据可初始化的地方
    created:function () {
        
    }


});
var app = new Vue({
    //对应html的标识
    el:"#app",

    //放属性的地方
    data:{
        username:''
    },

    //定义方法的地方
    methods:{
        //获取用户名的方法
        getUserInfo:function () {
            alert(6766)
            //向服务器发送axios请求
            axios.get('/login/user/info.shtml').then(function (response) {

                //将服务器响应过来的结果赋值到username中
                app.username = response.data;
            })
        }

    },

    //定义钩子函数，页面初始化时加载
    created:function () {
        //调用获取到用户名的方法，让它一初始化就加载

        this.getUserInfo();
    }

});
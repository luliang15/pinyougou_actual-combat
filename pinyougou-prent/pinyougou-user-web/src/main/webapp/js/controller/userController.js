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
        },

    },
    //钩子函数 初始化了事件和
    created: function () {

        //初始化的时候就显示用户名称
        this.getName();

    }

})

var app = new Vue({
    el: "#app",
    data: {
        add: {province:'北京市',city:'北京市市辖区',district:'东城区'},//地址
        date: {year:2010,month:05,day:04},//生日
        entity: {sex:'男',brithday:'',nickName:'',address:'',post:'',username:'',headPic:''},  //封装用户信息

        loginName:'',
    },
    methods: {
        //该方法用来注册新用户的
        regist: function () {
            this.entity.address = this.add.province +"/"+ this.add.city + "/"+this.add.district;
             this.entity.brithday = this.date.year+"/" + this.date.month+"/"+ this.date.day;
            this.entity.username=this.loginName;
            axios.post('/user/addUser.shtml?birthday='+this.entity.brithday, this.entity).then(function (result) {
                if (result.data.success) {
                    //跳转到其用户后台的首页
                    alert(result.data.message)
                } else {
                    alert(result.data.message)
                }
            })
        },
        addpost:function(value){
            this.entity.post=value;
        },
        //获取登录名
        getUsername: function () {
            axios.get('/login/name.shtml').then(function (response) {
                app.loginName = response.data;
            }).catch(function (error) {
                console.log(error);
            });
        },
        upload:function () {
            var formData=new FormData();
            //参数formData.append('file' 中的file 为表单的参数名  必须和 后台的file一致
            //file.files[0]  中的file 指定的时候页面中的input="file"的id的值 files 指定的是选中的图片所在的文件对象数组，这里只有一个就选中[0]
            formData.append('file', file.files[0]);
            axios({
                url: 'http://localhost:9110/upload/uploadFile.shtml',
                data: formData,
                method: 'post',
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                //开启跨域请求携带相关认证信息
                withCredentials:true
            }).then(function (response) {
                if(response.data.success){
                    //上传成功
                    console.log(this);
                    app.entity.headPic=response.data.message;
                    console.log(JSON.stringify(app.entity.headPic));
                }else{
                    //上传失败
                    alert(response.data.message);
                }
            })
        },
    },
    //钩子函数 初始化了事件和
    created: function () {
        //初始化的时候就显示用户名称
        this.getUsername();
    }
})

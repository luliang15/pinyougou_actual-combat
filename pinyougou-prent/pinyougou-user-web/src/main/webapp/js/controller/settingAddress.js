var app = new Vue({
    el: "#app",
    data: {
        entity:{},
        loginName:'',
        add:{},
        entity_1:{},
        active:'',
        active1:''
    },
    methods: {
        //查询
        findAllByUserName: function () {

            axios.post('/user/findAllAddress.shtml').then(function (response) {
                //获取数据
                app.entity = response.data;
            });
        },
        //获取登录名
          getUsername: function () {
              axios.get('/login/name.shtml').then(function (response) {
                  app.loginName = response.data;
              }).catch(function (error) {
                  console.log(error);
              });
          },
          findOne:function(id){
            axios.post('/user/findOneAddress/'+id+'.shtml').then(function (response) {
                app.entity_1=response.data;
                app.active=app.entity_1.alias;
                app.active1=app.entity_1.isDefault;

            })
          },
          update: function () {
              axios.post('/user/updateAddress.shtml', this.entity_1).then(function (response) {
                  if (response.data.success) {
                      alert(response.data.message)
                      app.findAllByUserName();
                  } else {
                      alert(response.data.message)
                  }
              })
          },
          dele: function (id) {
              axios.post('/user/deleteAddress.shtml?id=' + id).then(function (response) {
                  console.log(response);
                  if (response.data.success) {
                      alert(response.data.message)
                      app.findAllByUserName();
                  } else {
                      alert(response.data.message)
                  }
              })
          },
          addAddress: function () {
              axios.post('/user/addAddress.shtml', this.entity_1).then(function (response) {
                  console.log(response);
                  if (response.data.success) {
                      alert(response.data.message);
                      app.findAllByUserName();
                  }else{
                      alert(response.data.message);
                  }
              });
          },
          save: function () {
              if (this.entity_1.id != null) {
                  this.update();
              } else {
                  this.addAddress();
              }
          },
          addAlias:function (value) {
              this.entity_1.alias=value;
              this.active=value;
          },
          addIsDefault:function (value) {
              this.entity_1.isDefault=value;
              this.active1=value;
          },
          addId:function (value) {
              this.entity_1.id=value;
              this.findOne(value);
          }
      },
    //钩子函数
    created: function () {
        this.getUsername();
        this.findAllByUserName();
    }
});
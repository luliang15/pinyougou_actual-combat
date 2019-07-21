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

    },
    //3.放置方法的地方
    methods:{


        //从redis中获取秒杀列表的所有数据在页面展示
       findAll:function () {

           axios.get('/seckillGoods/findAll.shtml').then(
               function (response) {

                   app.list = response.data;
               }
           )
       },



    },

    //4.钩子函数，一些显示数据可初始化的地方
    created:function () {


        this.findAll();
    }


});
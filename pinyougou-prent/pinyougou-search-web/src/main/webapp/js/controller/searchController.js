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
        //用于绑定搜索的的条件的参数对象
        //定义过滤查询的变量，keywords,category,初始化的值都是空值，brand表示品牌的变量参数
        //sprc:{} 规格的变量参数，不过是格式不一样 {"网络":"移动4G","机身内存":"16G"}
        //price: 价格的变量。用来价格区间的展示
        //分页的变量：'pageNo'代表当前页码，默认为第一页，'pageSize'代表每页显示行数，默认最多显示40行
        //定义两个变量，一个查询时或升序或降序的类型，一个要进行查询的字段
        searchMap:{'keywords':'','category':'','brand':'',spec:{},'price':'','pageNo':1,'pageSize':40,'sortType':'','sortField':''},
        //  用于接收后台返回出来的map结果对象
        resultMap:{brandList:[]},
        //定义一个分页页码标签的变量
        pageLabels:[],
        //判断...是否显示
        preDott:false,      //当在第一页时，第1页前面的...默认不显示
        nextDott:false,    //当在最后一页时，最后一页的...默认不显示


    },
    //3.放置方法的地方
    methods:{

        //定义一个函数，根据页面的参数向后台发送请求，查询对应的数据，添加一个搜索项
        searchList:function () {

            axios.post('/itemSearch/search.shtml',this.searchMap).then(
                function (response) {


                    app.resultMap = response.data;
                    //查询完之后再调用显示分页页码标签
                    app.buildPageLabel();
                }
            )
        },

        //显示分页页码的标签的函数方法
        buildPageLabel:function(){

            //每次重新赋值为空值，每次调用查询方法时
            this.pageLabels=[]

        //初始化页数，与总页数
            var firstPage = 1;//第一页的页数为1
            var lastPage = this.resultMap.totalPages;//最后一页就是总页数

            //需要判断
            //总页数>5页
            if(this.resultMap.totalPages>5){
                if(this.searchMap.pageNo<=3){
                    //判断  如果当前的页码<=3 那么就显示前5页的数据
                firstPage=1;   //当前页=1
                lastPage =5;  //最后一页=5
                    this.preDott=false;  //这时是第一页，第一页的前的...不显示
                    this.nextDott=true;  //后面的...显示
                }else if(this.searchMap.pageNo>=this.resultMap.totalPages-2){
                    //判断 如果 当前页码>=总页数-2  显示最后的5页数据
                    firstPage = this.resultMap.totalPages-4;  //当前页=总页数-4
                    lastPage = this.resultMap.totalPages;    //当前页=总页数，也就是最后一页
                    this.preDott=true;  //这是最后的页码,反方向显示...
                    this.nextDott=false;  // 接下去的已经没有...

                }else {
                    //就显示中间的5页数据
                    firstPage = this.searchMap.pageNo-2;  //当前页-2
                    lastPage = this.searchMap.pageNo+2;   //当前页+2
                    this.preDott=true;
                    this.nextDott=true;   //中间的5页，两边都显示...
                }

            }else {
                //总页数<=5页，重新赋值
                firstPage = 1;
                lastPage = this.resultMap.totalPages;
                this.preDott=false;
                this.nextDott=false;//当页数小于5时。默认没有...
            }

            //经过重重判断之后再循环遍历分页的结果
            for (var i=firstPage;i<=lastPage;i++){
                //将获取的总页数分成一页一页地遍历，并将数据赋予显示分页页码标签的变量
                this.pageLabels.push(i);//因为变量是数组形式，使用push添加数据
            }
        },

        //定义一个函数，当点击当前页码的时候，此页码向后台发送请求，查询当前页的数据进行展示\
        queryByPage:function(page){

            //因为前面是文本输入框传过来的参数，这里需要转换成数字形式参数
            var number = parseInt(page);

            //做个判断，当输入的参数大于总页数或者页数小于0的情况
            if(number>this.resultMap.totalPages){
                number = this.resultMap.totalPages;
            }
            //当输入的数字小于0
            if(number<1){
                //默认第一页
                number=1;
            }

              //将查询得到数据赋予当前页码
            this.searchMap.pageNo = number;
            //刷新查询页面
                this.searchList();

        },

        //过滤查询，第一层，category的查询，商品分类的查询
        addSearchItem:function (key,value) {//html页面点击输入时传过来的值

            //因为spec的数据的格式不一样,是{}对象，得进行判断
            if(key=='category' || key=='brand'||key=='price'){
                //这里[key]代表一个随时可变的变量，比如点击时给的时category,key就是category,给的时brand,key就是brand
                this.searchMap[key]=value;
            }else {
                //这种是给spec，规格得数据展示  因为数据格式不一杨  [key]是随时动态变化
                this.searchMap.spec[key]=value;
            }



            //发送请求，执行搜索
            this.searchList();

        },

        //移出搜索项的函数   传过来要清空的参数，如:商品分类、品牌
        removeSearchItem:function (key) {
                //因为spec的数据的格式不一样,是{}对象，得进行判断
            if(key=='category' || key=='brand'||key=='price'){
                //这里[key]代表一个随时可变的变量，比如点击时给的时category,key就是category,给的时brand,key就是brand
                this.searchMap[key]='';
            }else {
                //这里不能使用空字符串也不能使用null，因为key的字段不能为空，只能删除 如：{key:value}
                //delete是js内置的删除关键字，就是在这种情况下使用来删除某个对象的属性
               delete this.searchMap.spec[key];
            }


            //2.重新发送请求
            this.searchList();
        },

        //定义一个函数，每次点击搜索时都会先清空之前搜索的数据，再进行搜索查询
        clear:function () {
            this.searchMap={'keywords':this.searchMap.keywords,'category':'','brand':'',spec:{},'price':'','pageNo':1,'pageSize':40,'sortType':'','sortField':''};
        },
        //绑定对价格进行或升序或降序的查询函数,点击变量时
        doSort:function (sortField,sortType) {

            this.searchMap.sortField = sortField;
            this.searchMap.sortType = sortType;

            //点击变量后刷新显示页面
            this.searchList();
        },

        //此函数用来 判断要点击搜索的关键字是否为品牌列表中的品牌
        isKeywordsIsBrand:function () {

            //判断品牌集合不为空
            if(this.resultMap.brandList!=null && this.resultMap.brandList.length>0){
                //循环品牌列表
                for (var i = 0; i <this.resultMap.brandList.length ; i++) {
                    //循环遍历 品牌列表  判断 关键字是否 包含品牌
                    if(this.searchMap.keywords.indexOf(this.resultMap.brandList[i].text)!=-1){
                        //将搜索到的值赋予此品牌变量
                        this.searchMap.brand=this.resultMap.brandList[i].text;
                        //包含则展示
                        return true;
                    }
                }
            }

            return false;  //循环一圈没有找到，则返回false，则不展示
        }

    },

    //4.钩子函数，一些显示数据可初始化的地方
    created:function () {

        //1.获取url中的参数列表值
        var urlParam = this.getUrlParam();

        if(urlParam!=undefined&&(urlParam.keywords!=undefined || urlParam.keywords!=null)){
            //2.将参数的值赋值给变量keywords
            //这边需要将传递过来的中文参数进行解码decodeURIComponent
            this.searchMap.keywords= decodeURIComponent(urlParam.keywords);
            //3.执行搜索方法
            this.searchList();
        }
    }


});
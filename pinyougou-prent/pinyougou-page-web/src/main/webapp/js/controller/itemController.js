var app = new Vue({
    //1.与前端对应的标识
    el: "#app",
    //2.放置属性的地方
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        searchEntity: [],
        num: 1,
        //获取默认的第一个的SKU的规格的数据赋值给他，涉及到浅克隆
        specificationItems:JSON.parse(JSON.stringify(skuList[0].spec)),//定义一个变量用于存储规格的数据
        sku: skuList[0]  //绑定SKU的对象，也要默认把第一个SKU的数据赋值给它，这里如果要改变它的值，需深克隆
    },
    //3.放置方法的地方
    methods: {
        /**
         *
         * @param data  data  就是1 或者-1
         */
        add: function (data) {
            //json格式的轉換
            data = parseInt(data);
            this.num = this.num + data;
            //判斷不能小於1
            if (this.num <= 1) {
                this.num = 1;
            }
        },

        //規格選型的方法
        selectSpecifcation: function (name, value) {
            //设置值 需要渲染
            this.$set(this.specificationItems, name, value);

            //点击规格时，勾选，并改变商品的规格信息，调用点击到规格数据的函数
            this.search();

        },

        //判斷點擊到的規格的選項是否在當前的規格的變量中存在，如果存在返回true，否則返回false
        isSelected: function (name, value) {

            if (this.specificationItems[name] == value) {
                return true;
            } else {
                return false;
            }
        },
        //目的，就是循环遍历SKU的列表数据，判断点击到的规格数据 是否在SKU列表中存在，如果存在，直接影响变量赋值
        search: function () {
            //此时遍历获取到的SKU列表的数据，判断存在即赋值
            for (var i = 0; i < skuList.length; i++) {

                var obj = skuList[i];
                //此时因为要比较的两个数据都是对象类型，VUE中没有equeles的对象比较
                //需要先将两个数据转成字符串做比较
                if (JSON.stringify(this.specificationItems) == JSON.stringify(obj.spec)) {
                    //如果存在
                    this.sku = obj;
                    //匹配上就结束循环
                    break;

                }
            }
        },
        //将商品添加进购物车中
        addGoodsToCartList:function () {

            alert("88")
            //因为是静态页面，跨域发送请求将商品添加进购物车中
            axios.get('http://localhost:9107/cart/addGoodsToCartList.shtml',{
                params:{
                    itemId:this.sku.id,
                    num:this.num
                },
                //客户端在ajax的时候也要携带cookie到服务器
                withCredentials:true
            }).then(function (response) {

                if(response.data.success){
                    //商品添加成功，跳转到购物车订单页面
                    window.location.href = "http://localhost:9107/cart.html";
                }else {
                    //添加失败
                    alert(response.data.message)
                }
            })
        }


    },

    //4.钩子函数，一些显示数据可初始化的地方
    created: function () {

    }


});
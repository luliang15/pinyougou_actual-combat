var app = new Vue({
    el: "#app",
    data: {

        catNo1: '',
        catNo2: '',
        //定义一个变量，1级列表信息的标量，使用[]数组容器接收内容
        itemCat1List: [],
        //定义一个变量，2级列表信息的标量，使用[]数组容器接收内容
        itemCat2List: [],

        entity: {goods: {}, goodsDesc: {itemImages:[],customAttributeItems:[],specificationItems:[]}, itemList: []},

    },
    methods: {

        //根据一级分类的类别的方法
        findItemCat1List: function () {
            //向服务器发送请求，默认一级等级为0
            axios.get('/itemCat/findByParentId/0.shtml').then(
                function (response) {
                    //将从服务器端获取的一级类目信息赋值给变量itemCat1List
                    app.itemCat1List = response.data;
                }
            )
        },

        createPicture: function () {

            var myChart = echarts.init(document.getElementById('chart'));
            var myChart2 = echarts.init(document.getElementById('chart2'));

            var start_year = document.getElementById('select_year2').value
            var start_month = document.getElementById('select_month2').value
            var start_day = document.getElementById('select_day2').value
            var end_year = document.getElementById('select_year1').value
            var end_month = document.getElementById('select_month1').value
            var end_day = document.getElementById('select_day1').value

            var startTime = start_year + '-' + start_month + '-' + start_day
            var endTime = end_year + '-' + end_month + '-' + end_day

            if (startTime == '--') {
                startTime = '2019-7-15'
            }
            if (endTime == '--') {
                endTime = '2019-7-31'
            }


            axios.get("/seller/findCategorySell.shtml", {
                params: {
                    startTime: startTime,
                    endTime: endTime,
                    catNo1: this.entity.goods.category1Id,
                    catNo2: this.entity.goods.category2Id


                }
            }).then((res) => {

                var datas = []
                for (let i = 0; i < res.data.goodsNames.length; i++) {

                    var obj = {value: '', name: ''}


                    obj.name = res.data.goodsNames[i]
                    obj.value = res.data.num[i]

                    datas.push(obj)
                }

                var datas2 = []

                for (let i = 0; i < res.data.goodsNames.length; i++) {

                    var obj = {value: '', name: ''}


                    obj.name = res.data.goodsNames[i]
                    obj.value = res.data.money[i]

                    datas2.push(obj)
                }

                myChart.setOption({
                        title: {
                            text: '商品销量占比',
                            subtext: '按照同一类别不同商品统计（单位：件）',
                            left: 'center'
                        },
                        tooltip: {//提示框组件
                            trigger: 'item',//触发类型，在饼形图中为item
                            formatter: "{a} <br/>{b} : {c} ({d}%)"//提示内容格式
                        },
                        legend: {
                            orient: 'vertical',
                            type: 'scroll',
                            // top: 'middle',
                            bottom: 0,
                            left: 50,
                            data: res.data.goodsNames
                        },
                        series: [{
                            name: '每天销量占比',
                            // radius : '55%',
                            // center: ['50%', '60%'],
                            /* label: {
                                 normal: {
                                     position: 'inner'
                                 }
                             },*/
                            selectedMode: 'single',
                            data: datas,
                            type: 'pie',
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }]
                    }
                )


                myChart2.setOption({
                        title: {
                            text: '商品销售额占比',
                            subtext: '按照同一类别不同商品统计（单位：元）',
                            left: 'center'
                        },
                        tooltip: {//提示框组件
                            trigger: 'item',//触发类型，在饼形图中为item
                            formatter: "{a} <br/>{b} : {c} ({d}%)"//提示内容格式
                        },
                        legend: {
                            orient: 'vertical',
                            type: 'scroll',
                            // top: 'middle',
                            bottom: 0,
                            left: 50,
                            data: res.data.goodsNames
                        },
                        series: [{
                            name: '每天销售额占比',
                            // radius : '55%',
                            // center: ['50%', '60%'],
                            /* label: {
                                 normal: {
                                     position: 'inner'
                                 }
                             },*/
                            selectedMode: 'single',
                            data: datas2,
                            type: 'pie',
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }]
                    }
                )
            })


        },

        turnSellTable: function () {
            var start_year = document.getElementById('select_year2').value
            var start_month = document.getElementById('select_month2').value
            var start_day = document.getElementById('select_day2').value
            var end_year = document.getElementById('select_year1').value
            var end_month = document.getElementById('select_month1').value
            var end_day = document.getElementById('select_day1').value

            var startTime = start_year + '-' + start_month + '-' + start_day
            var endTime = end_year + '-' + end_month + '-' + end_day

            window.location.href = 'sellTable.html?startTime=' + startTime + '&endTime=' + endTime + '&catNo1=&catNo2='

        }

    },
    //在这里定义一个监听器，用来监听 某一个变量的变化从而触发一个函数
    watch: {
        //newval参数为开始选中的列表信息，oldval为原本选中的列表信息
        'entity.goods.category1Id': function (newval, oldval) {
            //此时监听第一级 变量,二级变量根据一级变量的变化从而查询二级列表所要展示的信息
            //判断一级列表的类目不为空

            if (newval != undefined) {
                axios.get('/itemCat/findByParentId/' + newval + '.shtml').then(
                    function (response) {
                        //将从服务器端获取的一级类目信息赋值给变量itemCat1List
                        app.itemCat2List = response.data;
                    }
                )
            }
        }
    },

    //钩子函数 初始化了事件和
    created: function () {
        this.findItemCat1List()
        // this.createPicture()
    }

})



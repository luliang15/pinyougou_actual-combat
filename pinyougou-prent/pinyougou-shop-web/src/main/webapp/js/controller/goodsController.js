var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        //定义一个变量，存放数据列表的变量
        specList:[],   //此变量的格式：[{id:1,'text':"网络",options:[{},{}]}]
        list: [],
        //goods表示商品SPU,goodsDesc表示商品描述，itemList表示商品列表SKU
        //goods表示商品SPU,goodsDesc表示商品描述，itemList表示商品列表SKU
        //specificationItems表示规格列表的数据变量，是否勾选复选框
        //customAttributeItems表示扩展属性的变量
        //再定义一个模块Id的可扩展属性
        //itemList: []表示SKU规格列表的数据
        entity: {goods: {}, goodsDesc: {itemImages:[],customAttributeItems:[],specificationItems:[]}, itemList: []},
        //绑定一个变量 ，做回显图片名与图片使用
        image_entity: {url: '', color: ''},
        //定义一个变量，1级列表信息的标量，使用[]数组容器接收内容
        itemCat1List: [],
        //定义一个变量，2级列表信息的标量，使用[]数组容器接收内容
        itemCat2List: [],
        //定义一个变量，3级列表信息的标量，使用[]数组容器接收内容
        itemCat3List: [],
        //定义一个变量，来接收品牌列表的信息、
        brandTextList:[],

        ids: [],
        searchEntity: {}
    },
    methods: {
        searchList: function (curPage) {
            axios.post('/goods/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/goods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/goods/findPage.shtml', {
                params: {
                    pageNo: this.pageNo
                }
            }).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data.list;
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add: function () {

            alert(8888)
            //获取富文本编辑器中的内容传递给对象
            this.entity.goodsDesc.introduction = editor.html();
            axios.post('/goods/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    //新增成功，跳转到首页进行数据的展示，因为在同一级目录，使用相对路径就可以
                    window.location.href = "goods.html";
                }
            }).catch(function (error) {

                alert("报错"+error)
                console.log("1231312131321");
            });
        },
        update: function () {
            //获取富文本编辑器中的内容传递给对象
            this.entity.goodsDesc.introduction = editor.html();
            axios.post('/goods/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    //修改成功，跳转到首页进行数据的展示。因为在同一级目录，使用相对路径就可以
                    window.location.href = "goods.html";
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //判断是添加还是更新
        save: function () {
            if (this.entity.goods.id != null) {
                this.update();
            } else {
                this.add();
            }
        },

        findOne: function (id) {

            axios.get('/goods/findOne/' + id + '.shtml').then(function (response) {

               alert("测试"+response.data)

                app.entity = response.data;

                //赋值到富文本编辑器
                editor.html(app.entity.goodsDesc.introduction);

                //此时获取到的数据中，图片与扩展信息在数据库中是json字符串的形式，
                // 需要转成json对象并展示在页面,将变量转成json对象后又赋值回给此变量
                app.entity.goodsDesc.itemImages = JSON.parse(app.entity.goodsDesc.itemImages);

                //转换扩展属性
                app.entity.goodsDesc.customAttributeItems = JSON.parse(app.entity.goodsDesc.customAttributeItems)

                //转换规格列表的数据
                app.entity.goodsDesc.specificationItems=JSON.parse(app.entity.goodsDesc.specificationItems);

                //将SKU列表中的规格的数据转成json
                var itemList = app.entity.itemList;//获取到的数据格式{spec:\{\},price:0.01}

                for (var i=0;i<itemList.length;i++){

                    var obj = itemList[i]; //{spec:\{\},price:0.01}
                    //转换成json对像
                    obj.spec = JSON.parse(obj.spec);
                }


            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        dele: function () {
            axios.post('/goods/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //文件上传的函数方法
        uploadFile: function () {

            //alert("8888")

            //创建一个表单对象，html中的
            var formData = new FormData();

            //添加字段 类似：<input type="text" name="username">
            //file ===> <input type="file" name="file">
            //file.files[0]
            formData.append('file', file.files[0]);

            axios({
                url: 'http://localhost:9110/upload/uploadFile.shtml',
                //表示模拟一个表单上传，表单数据
                data: formData,
                method: 'post',
                //设置表单提交的数据类型
                headers: {
                    'Content-Type': 'multipart/form-data'
                },

                //开启跨域请求携带相关认证信息
                withCredentials: true

            }).then(function (response) {
                if (response.data.success) {
                    //上传成功，会打印出上传的路径

                    console.log(response.data.message)
                    //将图片路径赋值给变量
                    app.image_entity.url = response.data.message;
                } else {
                    //上传失败
                    alert(response.data.message);
                }
            })
        },

        //添加保存图片的函数
        addImageEntity: function () {
            //添加图片
            this.entity.goodsDesc.itemImages.push(this.image_entity);
        },

        //移出图片的函数
        removeImage: function (index) {
            //alert(888)
            //移除图片
            this.entity.goodsDesc.itemImages.splice(index, 1);
        },


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

        //函数的作用，当点击复选框的时候调用 并影响变量：entity.goodsDesc.specficationItems的值
        /**
         *
         * @param specName 表示text :‘网络’
         * @param specValue  表示optionName：‘移动4G’
         */
        updateChecked:function ($event,specName,specValue) {

            var obj = this.searchObjectByKey(this.entity.goodsDesc.specificationItems,'attributeName',specName);

            if(obj!=null){
                //1.如果有对象，直接设置对象里面的属性值，如果勾选，添加数据
                //判断勾不勾选，这里是勾选
                if($event.target.checked){
                    obj.attributeValue.push(specValue);  //设置属性值
                }else {
                    //如果取消勾选，删除数据
                    //indexOf(specValue,1)方法的作用是:从数组中查找值为specValue返回对应的下标
                    obj.attributeValue.splice(obj.attributeValue.indexOf(specValue),1);

                    //这里再判断，删除完了数据，将对象也清空或者说删除
                     if(obj.attributeValue.length==0){
                         //根据从数组中查找值为specValue返回对应的下标的
                         this.entity.goodsDesc.specificationItems.splice
                         (this.entity.goodsDesc.specificationItems.indexOf(obj),1);

                     }
                }


            }else {

                //2.如果没有对象，就直接添加属性值
                this.entity.goodsDesc.specificationItems.push(
                    {"attributeName":specName,"attributeValue":[specValue]}
                )
            }
        },

        /**
         *
         * @param list :指定的是要搜索的对象数组
         * @param specName ：要找的属性的名称
         * @param key   ：要找的属性的名称， 对应的值,匹配 ,键=‘attributeName’
         * @returns {*}
         */
        //再定义一个函数，作用用来判断此时该复选框有没有对象,key这个参数代表与specName匹配去寻找
        searchObjectByKey:function (list,key,specName) {
            //{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
            //获取对象的初始化形式
            var items = list;  //[]

            //循环遍历对象
            for(var i=0;i<items.length;i++){
                //获取到出来的格式{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
                var obj = items[i];
                //使用传入的key去与specName做匹配判断有没有此对象
                if(specName == obj[key]){
                        //匹配上，返回这个对象
                    return obj;
                }
            }
            //如果没有匹配到，返回null
            return null;
        },

        //每次点击复选框的时候调用此函数,调用生成 sku列表的的变量
        //点击复选框的时候 调用生成 sku列表的的变量
        createList:function () {
            //1.定义初始化的值
            this.entity.itemList=[{'spec':{},'price':0,'num':0,'status':'0','isDefault':'0'}];


            //获取specificationItems
            var specificationItems=this.entity.goodsDesc.specificationItems;

            //2.循环遍历 specificationItems
            for(var i=0;i<specificationItems.length;i++){
                //3.获取 规格的名称 和规格选项的值 拼接 返回一个最新的SKU的列表
                var obj = specificationItems[i];

                this.entity.itemList=this.addColumn(
                    this.entity.itemList,
                    obj.attributeName,
                    obj.attributeValue);
            }
        },


        /**
         *获取 规格的名称 和规格选项的值 拼接 返回一个最新的SKU的列表 方法
         * @param list
         * @param columnName  网络
         * @param columnValue  [移动3G,移动4G]
         */
        addColumn: function (list, columnName, columnValue) {

            var newList=[];

            for (var i = 0; i < list.length; i++) {
                var oldRow = list[i];//
                for (var j = 0; j < columnValue.length; j++) {
                    var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                    var value = columnValue[j];//移动3G
                    newRow.spec[columnName] = value;
                    newList.push(newRow);
                }
            }

            return newList;
        },
        //此函数方法用来判断 规格选项做数据回显时有没有值，需不需要勾选
        isChecked:function (specName,specValue) {
        //判断 循环到的 规格选项 的值 是否 在已有的变量中存在，如果，存在就要勾选，否则就不
            //获取到规格列表的选项字段的值
            var specificationItems = this.entity.goodsDesc.specificationItems;

            //调用根据属性名称与要找的属性名称匹配的方法，寻找属性名叫：attributeName
            var searchObjectByKey = this.searchObjectByKey(specificationItems,"attributeName",specName);

            //searchObjectByKey如果等于空，searchObjectByKey代表attributeName这个key对应的value值
            if(searchObjectByKey==null){
                return false; // 不勾选
            }

            //遍历这个字段的数据信息
            for (var i=0;i<specificationItems.length;i++){
                //判断{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
                //属性的名称与要找的属性名称是否匹配
                if(specificationItems[i].attributeValue.indexOf(specValue)!=-1){
                    //如果匹配
                    return true;
                }
            }
            return false;
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
        },

        //newval参数为开始选中的列表信息，oldval为原本选中的列表信息
        'entity.goods.category2Id': function (newval, oldval) {
            //此时监听第二级 变量,三级变量根据二级变量的变化从而查询三级列表所要展示的信息
            //判断二级列表的类目不为空
            if (newval != undefined) {
                axios.get('/itemCat/findByParentId/' + newval + '.shtml').then(
                    function (response) {
                        //将从服务器端获取的一级类目信息赋值给变量itemCat1List
                        app.itemCat3List = response.data;
                    }
                )
            }
        },

        //newval参数为开始选中的列表信息，oldval为原本选中的列表信息
        'entity.goods.category3Id': function (newval, oldval) {
            //此时监听第三级 变量,查询三级变量中的模板id,并在页面展示
            //判断三级列表的类目不为空
            if (newval != undefined) {
                axios.get('/itemCat/findOne/' + newval + '.shtml').then(
                    function (response) {
                        //要获取三级类目中模块的id值，且要将这个模块id一起添加到数据库中
                       // app.entity.goods.typeTemplateId = response.data.typeId;这个直接赋予值得方式不会
                        //对值造成渲染，需要刷新页面才会渲染，模块id会显示不出来
                        //需要使用一下这种方式赋值才会对模块ID造成渲染
                        //第一个参数为：需要改变变量的值的对象变量
                        //第二个参数为：需要赋值的属性名
                        //第三个参数为：获取到的值
                        app.$set(app.entity.goods,'typeTemplateId',response.data.typeId);
                    }
                )
            }
        },
        //newval参数为开始选中的列表信息，oldval为原本选中的列表信息
        'entity.goods.typeTemplateId': function (newval, oldval) {
            //此时监听模块ID的变化，根据模块ID去查询模块对象，展示模块对象的brand的text
            //判断模块ID不等于空
            if (newval != undefined) {
                axios.get('/typeTemplate/findOne/' + newval + '.shtml').then(
                    function (response) {
                        //1.根据服务器端响应的数据，获取模块对象
                        var typeTemplate  = response.data;

                        //2.定义一个变量，来接收模块id所获取到的品牌列表的信息
                        //但是，数据库中品牌列表的信息的数据为json字符串的数组，在这里需要将json字符串转换成JSON对象
                        //JSON.parse 解析把一个字符串转换成JSON对象
                        app.brandTextList = JSON.parse(typeTemplate.brandIds);
                        //typeTemplate.brandIds获取模块中的模块属性值，数据格式['id',1]
                        //根据监听到模块ID去扩展可扩展的属性
                        //判断当新增的时候需要监听扩展属性，修改的时候不需要
                        if(app.entity.goods.id==null){
                            app.entity.goodsDesc.customAttributeItems =
                                JSON.parse(typeTemplate.customAttributeItems);
                        }
                       //否则修改不需要监听
                    }
                )

                //规格列表数据的展示也是根据模板id  typeTemplateId的变化而变化
                axios.get('/typeTemplate/findSpecList/'+newval+'.shtml').then(
                    function (response) {
                        //将获取到的值赋予给specList变量
                        app.specList = response.data;
                    }
                )


            }
        },
    },

    //钩子函数 初始化了事件和
    created: function () {

        /*this.searchList(1);*/
        //当页面加载时，加载|调用一级类目列表查询方法
        this.findItemCat1List();

        //调用自定义获取id参数的函数
        var obj = this.getUrlParam();
        //打印获取参数
        console.log(obj)
        //调用根据商品id查询回显的函数
        this.findOne(obj.id);
    }

})


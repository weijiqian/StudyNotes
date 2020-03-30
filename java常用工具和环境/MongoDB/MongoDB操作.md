### 库与表操作

#### 1.创建、查看、删除数据库

- 查看当前的数据库：`db`
- 查看所有的数据库：`show dbs`
- 切换数据库：`use 数据库名称`
- 删除当前的数据库：`db.dropDatabase()`



#### 2.创建、查看、删除集合

- 手动创建集合：`db.createCollection(name,options)`
  *参数说明：*
  `name`：要创建的集合的名称
  `option`：可选参数

```
        db.createCollection("praite")
        db.createCollection("praite",{capped:true,size:10})
```

*参数说明：*
`capped`：默认为false，如果设置为true，则创建固定大小的集合，当达到最大值时，会自动覆盖最早的文档。当设置为true时，必须指定`size`参数。
`size`：为固定大小的集合指定一个最大值，以字节计。

- 不手动创建集合：向不存在的集合第一次加入数据时，集合会被自动创建出来。
- 查看集合：`show collectinos`
- 删除集合：`db.集合名称.drop()`

#### 3.插入数据

- 语法：`db.集合名称.insert(document)`

```
        db.pirate.insert({name:"路飞",gender:1,hometown:"风车村",age:19})
        db.pirate.insert({_id:"20181126",name:"路飞",gender:1,hometown:"风车村",age:19})
```

显示结果如下：
![img](https://i.imgur.com/esTpxQb.png)

- **插入文档时，如果不指定`_id`参数，MongoDB会为文档分配一个唯一的`ObjectID`，如果`_id`存在则会报错**如下图所示。



#### 4.保存数据

- 语法：`db.集合名称.save(document)`
- **与`insert`不同的是，如果文档的`_id`存在则修改，如果文档的`_id`不存在则添加，**如下图所示。

```
        db.pirate.save({_id:"20181126",name:"山治",gender:0,hometown:"杰尔马66王国",age:21})
        db.pirate.save({_id:"2018112601",name:"布鲁克",gender:1,hometown:"西海",age:90})
```

![img](https://i.imgur.com/2pI0Bjn.png)

#### 5.更新数据

- 语法：`db.集合名称.update(,,{multi:})`
  *参数说明*：
  `query`：update的查询条件
  `update`：update的对象和一些更新的操作符
  `multi`：可选，默认为false，表示只更新找到的第一条记录，若值为true表示把满足条件的文档全部更新。

```
        db.pirate.update({name:"路飞"},{name:"蒙奇·D·路飞"})
        db.pirate.update({name:"索隆"},{$set:{name:"罗罗诺亚·索隆"}})
        db.pirate.updata({gender:1},{$set:{gender:0}},{multi:true})
```

- **注意：`$set`表示只更新响应的值**，如下图所示。
  ![img](https://i.imgur.com/JHQVDlr.png)

#### 6.删除数据

- 语法：`db.集合名称.remove(,{justOne:})`
  *参数说明*：
  `query`：可选，删除的文档的条件
  `justOne`：可选，过个设为true或1，则只删除一个文档

```
        db.pirate.remove({age:19})
```

![img](https://i.imgur.com/EaKpwBD.png)

#### 7.查询数据

- 语法：
  db.集合名称.find({条件})
- 如果要是显示内容美观化，可以使用pretty()方法：

```
        db.集合名称.find({条件}).pretty()
        db.pirate.find({age:17}).pretty()
```

![img](https://i.imgur.com/WbQqttf.png)

- 比较运算符
  - 小于：`$lt`
  - 小于等于：`$lte`
  - 大于：`$gt`
  - 大于等于：`$gte`
  - 不等于：`$ne`

```
            db.pirate.find({age:{$gte:20}})
```

![img](https://i.imgur.com/GJtiA4P.png)

- 逻辑运算符
  - AND： 在find()方法中传入多个键(key)，每个键(key)以逗号隔开，语法如下：

```
            db.集合名称.find({key1:value1, key2:value2})  
- OR：使用关键字`$or`，语法如下：
            db.集合名称.find({$or:[{key1:value1}, {key2:value2}]})
- AND和OR可以一起使用：
            db.pirate.find({$or:[{age:{$gte:20}}, {gender:1}],hometown:"西海"})
```



- limit()和skip()
  - limit()方法语法：

```
            db.集合名称.find().limit(NUMBER)
- skip()方法语法：
            db.集合名称.find().skip(NUMBER)
- limit()方法和skip()方法可以一起使用：
            db.pirate.find().skip(4).limit(2)
```

![img](https://i.imgur.com/2wPctsN.png)

#### 8.排序

- 语法：`db.集合名称.find().sort({字段名称:1,字段名称:-1,...})`
  *参数说明*：1表示升序，-1表示降序

```
        db.pirate.find().sort({age:1,gender:-1})
```

![img](https://i.imgur.com/C5gwyIO.png)

#### 9.统计个数

- 语法：

```
        db.集合名称.find({条件}).count()
        db.集合名称.count({条件})
        db.pirate.find({gender:0}).count()
        db.pirate.count({age:{$gt:20},gender:1})
```
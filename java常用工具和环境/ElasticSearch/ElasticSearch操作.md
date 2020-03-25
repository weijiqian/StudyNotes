### 基本概念

- Index: 一系列文档的集合，类似于mysql中数据库的概念
- Type: 在Index里面可以定义不同的type，type的概念类似于mysql中表的概念，是一系列具有相同特征数据的结合。
- Document: 文档的概念类似于mysql中的一条存储记录，并且为json格式，在Index下的不同type下，可以有许多document。
- Shards: 在数据量很大的时候，进行水平的扩展，提高搜索性能
- Replicas: 防止某个分片的数据丢失，可以并行得在备份数据里及搜索提高性能

| ElasticSearch   | MYSQL             |
| --------------- | ----------------- |
| 索引库(indices) | 数据库(databases) |
| 类型(type)      | 表(table)         |
| 文档(document)  | 行(row)           |
| 字段(field)     | 列(column)        |



### 1 保存数据

不需要创建 index 和 type  ,直接保存

```
http://hadoop1:9200/bigdata/student/
# 选择post  index ==> bigdata,  type ==> student
{
  "name": "zhang wu",
  "age": "29",
  "gender": "F"
}
```



### 2 查询数据

```
http://hadoop1:9200/
bigdata/student/_search
{
  "query": {
    "match": {
      "age": "20"
    }
  }
}
```


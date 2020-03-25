query 查询： 模糊匹配，并对匹配出来的数据进行评分。

“took”: 查询花费的时间
_score： 匹配相识度评分 满分 5 分

常用查询：
全文本查询：针对文本
1、查询全部：match_all
2、模糊匹配： match (类似sql 的 like)
3、全句匹配： match_phrase (类似sql 的 = )
4、多字段匹配：muti_match （多属性查询）
5、语法查询：query_string (直接写需要配置的 关键字 )
6、字段查询 ： term (针对某个属性的查询，这里注意 term 不会进行分词，比如 在 es 中 存了 “火锅” 会被分成 “火/锅” 当你用 term 去查询 “火时能查到”，但是查询 “火锅” 时，就什么都没有，而 match 就会将词语分成 “火/锅”去查)
7、范围查询：range ()
字段查询：针对结构化数据，如数字，日期 。。。

分页：
“from”: 10,
“size”: 10

constant_score: 固定分数。

filter: 查询： （query 属于类似就可以查出来，而 filter 类似 = 符号，要么成功，要么失败，没有中间值，查询速度比较快）

下面是 demo：
全局匹配：（默认返回10条）

```
GET 127.0.0.1:9200/shop-index/_search
    {
      "query": { "match_all": {}}
    }

```



POST 请求 ip:9200/shop/_search
match 匹配： title = “串串” 分页 from 10 共 size 10

```{
{
	"query": {
    "match": {"title": "串"}
  },
  "from": 10,
  "size": 10
}
```


POST 请求 ip:9200/shop/_search
match 匹配： title = “串串” 排序 order = desc

```
{
  "query": {
    "match": {"title": "串"}
  },
  "sort": [
        {"id": {"order": "desc" }}
  ],
  "from": 10,
  "size": 10
}
```



mutil_match 查询：“query”: “串串”, 为要查寻的关键字，“fields”: [ “title”, “tag”] 从 title 和 tag 属性中去找。有一个匹配就算成功。

```
{
  "query": {
    "multi_match": {
      "query": "串串",
      "fields": [ "title", "tag"]
    }
  }
}

```



query_string 语法查询： “query”: “(关键字 and 关键字) or 关键字” 各种关键字 全局搜索

```
{
  "query": {
    "query_string": {
      "query": "(水煮肉 and 回锅肉) or 西葫芦"
    }
  }
}

```



query_string 可以限定 查询字段（默认查询所有字段）

```
{
  "query": {
    "query_string": {
      "query": "(水煮肉 and 回锅肉) or 西葫芦",
      "fields": ["title" ]
    }
  }
}

```



filter 查询：

```
{"query": {
    "bool": {
      "filter": {
        "term": {"id": "13"}
      }
    }
 }
}

```

constant_score: 固定分数。 （指定 boost 匹配分数是 2 的结果，默认不填是 1）
固定分数查询不支持 match 只支持 filter

```
{
  "query": {
    "constant_score": {
      "filter": {
        "match": {
          "title": "火锅"
        }
      },
      "boost": 2
    }
  }
}

```



bool 查询
must: 是 类似 and
must_not ：不等于
should: 类似 or

```
{
  "query": {
    "bool": {
      "must": [
        {
         "match": {
            "title": "火锅"
          }
        },
        {
          "match": {
            "tag": "串串"
          }
        }
      ]
    }
  }
}
```


should 语法查询

```
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "title": "串串"
          }
        },
        {
          "match": {
            "tag": "西葫芦"
          }
        }
      ]
    }
  }
}

```

————————————————
版权声明：本文为CSDN博主「没事偷着乐琅」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/zhanglinlang/article/details/82891547
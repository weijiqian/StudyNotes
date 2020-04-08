map{}+array []
{ww:[]}

[{}]

对象：属性
json：属性：对应值[]|{}|值

案例：
[[{"movie":"1193","rate":"5","timeStamp":"978300760","uid":"1"}]]
[[{"movie":"661","rate":"3","timeStamp":"978302109","uid":"1"}]]
[[{"movie":"914","rate":"3","timeStamp":"978301968","uid":"1"}]]
[[{"movie":"3408","rate":"4","timeStamp":"978300275","uid":"1"}]]
[[{"movie":"2355","rate":"5","timeStamp":"978824291","uid":"1"}]]
[[{"movie":"1197","rate":"3","timeStamp":"978302268","uid":"1"}]]
[[{"movie":"1287","rate":"5","timeStamp":"978302039","uid":"1"}]]
[[{"movie":"2804","rate":"5","timeStamp":"978300719","uid":"1"}]]
[[{"movie":"594","rate":"4","timeStamp":"978302268","uid":"1"}]]
将上面的数据建表关联
movie	rate	timeStamp	uid
1193	5		978300760	1

- 1）将文本的数据建表关联  一个字段

```
create table rate_json(line string);
load data local inpath '/home/hadoop/tmpdata/rate.json' into table rate_json;

```



- 2)解析表  --- 最终表
  直接对json解析
  get_json_object  json数据解析的
  get_json_object(json_txt, path)
  	参数1：json串   参数2：需要解析的路径
  	参数2：
  	  $   :   根节点   json的最外层
  	  .   :   子节点   map集合
  	  []  :    数组元素的  下标  0开始

  : Wildcard for []
  [[{"movie":"1193","rate":"5","timeStamp":"978300760","uid":"1"}]]
  $[0][0].movie
  select get_json_object('[[{"movie":"1193","rate":"5","timeStamp":"978300760","uid":"1"}]]','$[0][0].movie'),get_json_object('[[{"movie":"1193","rate":"5","timeStamp":"978300760","uid":"1"}]]','$[0][0].rate');

```
create table rate as 
select 
get_json_object(line,"$0.movie") movieid,
get_json_object(line,"$0.rate") rate,
get_json_object(line,"$0.timeStamp") timedate,
get_json_object(line,"$0.uid") uid  
from rate_json;

```



了解：
json_tuple

注意：json解析完成的都是字符串类型的
ctas--->create table .. as ...select ...



========================================
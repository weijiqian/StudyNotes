### 1窗口函数

（1） OVER()：指定分析函数工作的数据窗口大小，这个数据窗口大小可能会随着行的变而变化。常用partition by 分区order by排序。

（2）CURRENT ROW：当前行

（3）n PRECEDING：往前n行数据

（4） n FOLLOWING：往后n行数据

（5）UNBOUNDED：起点，UNBOUNDED PRECEDING 表示从前面的起点， UNBOUNDED FOLLOWING表示到后面的终点

（6） LAG(col,n)：往前第n行数据

（7）LEAD(col,n)：往后第n行数据

（8） NTILE(n)：把有序分区中的行分发到指定数据的组中，各个组有编号，编号从1开始，对于每一行，NTILE返回此行所属的组的编号。注意：n必须为int类型。

### 2排序函数

（1）RANK() 排序相同时会重复，总数不会变

（2）DENSE_RANK() 排序相同时会重复，总数会减少

（3）ROW_NUMBER() 会根据顺序计算



举例

```sql
select 
    mid_id,
    time,
    rank() over(partition by mid_id order by time) rank
from dws_uv_detail_day
      
```

| mid_id | time       | rank |
| ------ | ---------- | ---- |
| user1  | 2020-02-20 | 1    |
| user1  | 2020-02-22 | 2    |
| user1  | 2020-02-23 | 3    |
| user2  | 2020-02-21 | 1    |
| user2  | 2020-02-22 | 2    |
| user3  | 2020-02-22 | 2    |
| user3  | 2020-02-24 | 4    |
|        |            |      |


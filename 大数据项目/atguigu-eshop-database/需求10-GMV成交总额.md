###  概念 

GMV 电商成交总额



### 2 建表

```shell
hive (gmall)>
drop table if exists ads_gmv_sum_day;
create external table ads_gmv_sum_day(
    `dt` string COMMENT '统计日期',
    `gmv_count`  bigint COMMENT '当日gmv订单个数',
    `gmv_amount`  decimal(16,2) COMMENT '当日gmv订单总金额',
    `gmv_payment`  decimal(16,2) COMMENT '当日支付金额'
) COMMENT 'GMV'
row format delimited fields terminated by '\t'
location '/warehouse/gmall/ads/ads_gmv_sum_day/'
;
```

### 3 计算

```shell
hive (gmall)>
insert into table ads_gmv_sum_day
select 
'2019-02-10' dt,
    sum(order_count) gmv_count,
    sum(order_amount) gmv_amount,
    sum(payment_amount) payment_amount 
from dws_user_action
where dt ='2019-02-10'
group by dt
;
```


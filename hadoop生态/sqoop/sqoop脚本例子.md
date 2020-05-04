mysql_to_hdfs.sh

```shell
#! /bin/bash
sqoop=/opt/module/sqoop/bin/sqoop do_date=`date -d '-1 day' +%F`
if [[ -n "$2" ]]; then fi do_date=$2
import_data(){
$sqoop import \
--connect jdbc:mysql://hadoop102:3306/gmall \ --username root \
--password 000000 \
--target-dir /origin_data/gmall/db/$1/$do_date \ --delete-target-dir \
--query "$2 and \$CONDITIONS" \
--num-mappers 1 \
--fields-terminated-by '\t' \
--compress \
--compression-codec lzop \
--null-string '\\N' \
--null-non-string '\\N'
hadoop jar /opt/module/hadoop-2.7.2/share/hadoop/common/hadoop-lzo-0.4.20.jar com.hadoop.compression.lzo.DistributedLzoIndexer /origin_data/gmall/db/$1/$do_date
}
import_order_info(){ import_data order_info "select
}
id, final_total_amount, order_status,
user_id,
out_trade_no, create_time, operate_time, province_id, benefit_reduce_amount, original_total_amount, feight_fee
from order_info
where (date_format(create_time,'%Y-%m-%d')='$do_date' or date_format(operate_time,'%Y-%m-%d')='$do_date')"

import_coupon_use(){ import_data coupon_use "select
}
id,
coupon_id, user_id, order_id, coupon_status, get_time, using_time, used_time
from coupon_use
where (date_format(get_time,'%Y-%m-%d')='$do_date' or date_format(using_time,'%Y-%m-%d')='$do_date' or date_format(used_time,'%Y-%m-%d')='$do_date')"
import_order_status_log(){ import_data order_status_log "select
id,
order_id, order_status, operate_time
                           from order_status_log
where date_format(operate_time,'%Y-%m-%d')='$do_date'"
}

import_activity_order(){ import_data activity_order "select
id, activity_id, order_id, create_time
from activity_order
where date_format(create_time,'%Y-%m-%d')='$do_date'"
}
#省略若干.......

case $1 in "order_info")
    import_order_info
;;
"base_category1")
 		import_base_category1
;;
"base_category2")
    import_base_category2
;;
"base_category3")
    import_base_category3
;;
"order_detail")
    import_order_detail
;;
"sku_info") import_sku_info
;; "user_info")
    import_user_info
;;
"payment_info")
    import_payment_info
;;
"base_province")
import_base_province ;;
"base_region")
    import_base_region
;;
"base_trademark")
    import_base_trademark
;;
"activity_info")
import_activity_info ;;
"activity_order")
import_activity_order ;;
"cart_info")
     import_cart_info
;;
"comment_info")
     import_comment_info
;;
"coupon_info")
     import_coupon_info
;;
"coupon_use")
     import_coupon_use
;;
"favor_info")
import_favor_info ;;
"order_refund_info")
     import_order_refund_info
;;
"order_status_log")
     import_order_status_log
;;
"spu_info")
     import_spu_info
;;
"activity_rule")
import_activity_rule ;;
"base_dic")
     import_base_dic
;;
"first") 
import_base_category1 
import_base_category2 
import_base_category3 
import_order_info 
import_order_detail 
import_sku_info 
import_user_info 
import_payment_info 
import_base_province
import_base_region
import_base_trademark 
import_activity_info 
import_activity_order 
import_cart_info 
import_comment_info 
import_coupon_use 
import_coupon_info 
import_favor_info 
import_order_refund_info 
import_order_status_log 
import_spu_info 
import_activity_rule
import_base_dic;; 

"all") import_base_category1
import_base_category2
import_base_category3
import_order_info 
import_order_detail 
import_sku_info 
import_user_info 
import_payment_info 
import_base_trademark 
import_activity_info 
import_activity_order 
import_cart_info 
import_comment_info 
import_coupon_use 
import_coupon_info 
import_favor_info 
import_order_refund_info 
import_order_status_log 
import_spu_info 
import_activity_rule
import_base_dic 
;; 
esac
```

```
说明 1:
[ -n 变量值 ] 判断变量的值，是否为空 -- 变量的值，非空，返回 true
-- 变量的值，为空，返回 false

说明 2:
查看 date 命令的使用，
[atguigu@hadoop102 ~]$ date --help

参数说明:
参数1:表名-->具体表
		first-->第一次导入
		all-->全部导入
		
参数2:
	导入数据的时间.

2)修改脚本权限
[atguigu@hadoop102 bin]$ chmod 777 mysql_to_hdfs.sh 

3)初次导入
[atguigu@hadoop102 bin]$ mysql_to_hdfs.sh first 2020-03-10 

4)每日导入
[atguigu@hadoop102 bin]$ mysql_to_hdfs.sh all 2020-03-11
```


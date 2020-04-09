#### 1 import.job

```shell
type=command
do_date=${dt}
command=/home/atguigu/bin/sqoop_import.sh all ${do_date}
```

#### 2 ods.job

```shell
type=command
do_date=${dt}
dependencies=import
command=/home/atguigu/bin/ods_db.sh ${do_date}
```

#### 3 dwd.job

```shell
type=command
do_date=${dt}
dependencies=ods
command=/home/atguigu/bin/dwd_db.sh ${do_date}
```

#### 4 dws.job

```shell

type=command
do_date=${dt}
dependencies=dwd
command=/home/atguigu/bin/dws_db_wide.sh ${do_date}
```

#### 5 ads.job文件

```shell
type=command

do_date=${dt}

dependencies=dws

command=/home/atguigu/bin/ads_db_gmv.sh ${do_date}

```
#### 6 export.job文件

```shell

type=command

dependencies=ads

command=/home/atguigu/bin/sqoop_export.sh ads_gmv_sum_day 
```

#### 7 将以上6个文件压缩成gmv-job.zip文件

 放入azkaban中运行.
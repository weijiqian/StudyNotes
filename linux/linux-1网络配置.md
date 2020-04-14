### ifconfig

查看网络

### ping 

测试主机的连通性

### 修改ip

vim  /etc/sysconfig/network-scripts/ifcfg-eth0

```shell
DEVICE=eth0                #接口名（设备,网卡）
HWADDR=00:0C:2x:6x:0x:xx   #MAC地址 
TYPE=Ethernet               #网络类型（通常是Ethemet）
UUID=926a57ba-92c6-4231-bacb-f27e5e6a9f44  #随机id
#系统启动的时候网络接口是否有效（yes/no）
ONBOOT=yes                
# IP的配置方法[none|static|bootp|dhcp]（引导时不使用协议|静态分配IP|BOOTP协议|DHCP协议）
BOOTPROTO=static      
#IP地址
IPADDR=192.168.1.100   
#网关  
GATEWAY=192.168.1.2      
#域名解析器
DNS1=114.114.114.114
DNS2=8.8.8.8
```

### 查看主机名

```
hostname
```



### 修改主机名

```shell
vi /etc/sysconfig/network
```



### 修改域名映射

```shell
vim /etc/hosts
```

### 关闭防火墙

```shell
service  服务名 start			（功能描述：开启服务）
service  服务名 stop			（功能描述：关闭服务）
service  服务名 restart			（功能描述：重新启动服务）
service  服务名 status			（功能描述：查看服务状态）
```

### 自动启动配置

```shell
chkconfig   			 （功能描述：查看所有服务器自启配置）
chkconfig 服务名 off   （功能描述：关掉指定服务的自动启动）
chkconfig 服务名 on   （功能描述：开启指定服务的自动启动）
chkconfig 服务名 --list	（功能描述：查看服务开机启动状态）
```

### 关机

流程 sync > shutdown > reboot > halt

```shell
sync  			（功能描述：将数据由内存同步到硬盘中）
halt 			（功能描述：关闭系统，等同于shutdown -h now 和 poweroff）
reboot 			（功能描述：就是重启，等同于 shutdown -r now）
shutdown [选项] 时间	
```


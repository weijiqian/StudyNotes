

在两台机器上,自己测试用的

es节点ip：
node-1: 192.168.8.101
node-2: 192.168.8.102



elasticsearch.yml

node-1

```

# ---------------------------------- Cluster -----------------------------------
cluster.name: mycluster
#
# ------------------------------------ Node ------------------------------------
#
# Use a descriptive name for the node:
#
node.name: node-1

path.data: /app/data/elastic/data
path.logs: /app/data/elastic/logs
#
# ----------------------------------- Memory -----------------------------------
# ---------------------------------- Network -----------------------------------
network.host: 192.168.8.101
http.port: 9200
transport.tcp.port: 9300
discovery.zen.ping.unicast.hosts: ["192.168.8.101:9300","192.168.8.102:9300"]  #发现新的节点的ip
# --------------------------------- Discovery ---------------------------------- the cluster using an initial set of master-eligible nodes:

cluster.initial_master_nodes: ["node-1"]
```



node-2 配置

```

# ---------------------------------- Cluster -----------------------------------
cluster.name: mycluster

# ------------------------------------ Node ------------------------------------
node.name: node-2

path.data: /app/data/elastic/data
path.logs: /app/data/elastic/logs
#
# ----------------------------------- Memory -----------------------------------
# ---------------------------------- Network -----------------------------------
network.host: 192.168.8.102
http.port: 9200
transport.tcp.port: 9300
discovery.zen.ping.unicast.hosts: ["192.168.8.101:9300","192.168.8.102:9300"]  #发现新的节点的ip
# --------------------------------- Discovery ---------------------------------- 

cluster.initial_master_nodes: ["node-1"]
```


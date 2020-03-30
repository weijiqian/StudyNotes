查看帮助
db.yourColl.help();

查看当前集合的数据条数
db.yourColl.count();

查看数据空间大小
db.userInfo.dataSize();

得到当前聚集集合所在的db
db.userInfo.getDB();

得到当前聚集的状态
db.userInfo.stats();

得到聚集集合总大小
db.userInfo.storageSize();

聚集集合存储空间大小
db.userInfo.storageSize();

Shard版本信息
db.userInfo.getShardVersion();

聚集集合重命名
db.userInfo.renameCollection("users"); // 将userInfo重命名为users

删除当前聚集集合
db.userInfo.drop();

显示数据库列表
show dbs;

显示当前数据库中的集合（类似关系数据库中的表）
show collections;

显示用户
show users;

切换当前数据库，这和MS-SQL里面的意思一样
user <db name>;

显示数据库操作命令，里面有很多的命令
db.help();

显示集合操作命令，同样有很多的命令。
db.foo.help();

对于当前数据库中的foo集合进行数据查找。
db.foo.find();

对于当前数据库中的foo集合进行查找，条件数据中有一个属性a，且a的值为1
db.foo.find({a : 1});

查询之前的错误信息
db.getPrevError();

清除错误记录
db.resetError();
————————————————
版权声明：本文为CSDN博主「孤芳不自賞」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/en_joker/article/details/75040323
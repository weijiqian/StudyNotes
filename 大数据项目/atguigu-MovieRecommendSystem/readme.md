原理:

1.以用户评分为基础,计算电影的相似度.给用户做推荐.

例如:小红给"速度与激情","指环王"评分为5分

​	当小张也对"指环王"评分为5分时,就给小张也推荐"速度与激情".

2. 以内容为基础.进行分词处理,计算电影相似度.

例如: 小红点击了"速度与激情1",那么就立马给他推荐"速度与激情2".



步骤

- 1 recommender/DataLoader/dataloader 加载数据到mongdb和ES中
- 2 recommender/StatisticsRecommender 计算 历史热门统计
  *    近期热门统计
  *    优质电影统计 等数据.存入mongdb中.





mongdb中的数据表

| 建表的时间                  | 表名                   | 描述                                        | 数据来源                           |
| --------------------------- | ---------------------- | ------------------------------------------- | ---------------------------------- |
| 第1步,dataloader            | Movie                  | 电影表                                      | 从文件中加载                       |
| 第1步,dataloader            | Rating                 | 评分表                                      | 从文件中加载                       |
| 第1步,dataloader            | Tag                    | 标签表                                      | 从文件中加载                       |
| 第2步,StatisticsRecommender | RateMoreMovies         | 每一部电影的评分次数                        | Movie中按电影分组                  |
| 第2步,StatisticsRecommender | RateMoreRecentlyMovies | 最近几个月中,每一部电影在一个月中评分的次数 | RateMoreMovies中按月分组           |
| 第2步,StatisticsRecommender | AverageMovies          | 统计电影的平均评分                          | Rating                             |
| 第2步,StatisticsRecommender | GenresTopMovies        | 类别top10                                   | 对类别和电影做笛卡尔积,求类别top10 |
| 第3步,OfflineRecommender    | UserRecs               |                                             |                                    |
| 第3步,OfflineRecommender    | MovieRecs              | 电影相似度                                  | movies表                           |
| 第5步,ContentRecommender    | ContentMovieRecs       | 根据内容分词的相似度                        |                                    |
|                             |                        |                                             |                                    |
|                             |                        |                                             |                                    |
|                             |                        |                                             |                                    |
|                             |                        |                                             |                                    |
|                             |                        |                                             |                                    |
|                             |                        |                                             |                                    |
|                             |                        |                                             |                                    |
|                             |                        |                                             |                                    |
|                             |                        |                                             |                                    |

ES中的表

| 建表时机         | 表名  | 描述               | 数据来源   |
| ---------------- | ----- | ------------------ | ---------- |
| 第1步,dataloader | Movie | 电影与标签的结合表 | 电影与标签 |
|                  |       |                    |            |
|                  |       |                    |            |
|                  |       |                    |            |
|                  |       |                    |            |
|                  |       |                    |            |
|                  |       |                    |            |
|                  |       |                    |            |
|                  |       |                    |            |





### 不懂的问题

第3步中  训练隐语义模型
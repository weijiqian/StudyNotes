写数据

```scala
df.write
      .format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/commerce?useUnicode=true&characterEncoding=utf8")
      .option("dbtable", "area_top3_product")
      .option("user", "root")
      .option("password", "root")
      .mode(SaveMode.Append)
      .save()
```


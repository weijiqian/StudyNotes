
    map  遍历每一个元素
    flatten  就是将多个集合展开，组合成新的一个集合
    flatmap = map + flatten
      

### 什么时候使用flatmap?

当map的结果是嵌套集合时(result1),可以用flatmap转为单层集合(result2)    

result1经过flatten 后就跟result2一样了

***总结 :如果map的结果是嵌套的集合,想变为单层的,就用flatmap***

```
result1 = List(List(1,2,3),List(4,5,6),List(7,8,9))
```

```
result2 = List(1,2,3,4,5,6,7,8,9)
```

```
result3 = result1.flatten
result3 = List(1,2,3,4,5,6,7,8,9)
```


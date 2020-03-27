### Scala里面有三种排序方法，分别是： sorted，sortBy ，sortWith

- （1）sorted

对一个集合进行自然排序，通过传递隐式的Ordering

- （2）sortBy

对一个属性或多个属性进行排序，通过它的类型。

- （3）sortWith

基于函数的排序，通过一个comparator函数，实现自定义排序的逻辑。


sorted：适合单集合的升降序

sortBy：适合对单个或多个属性的排序，代码量比较少，推荐使用这种

sortWith：适合定制化场景比较高的排序规则，比较灵活，也能支持单个或多个属性的排序，但代码量稍多，内部实际是通过java里面的Comparator接口来完成排序的。

具体代码见 Scala13_sort
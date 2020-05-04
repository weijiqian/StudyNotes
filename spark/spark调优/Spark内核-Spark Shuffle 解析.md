作者：十一喵先森
链接：https://juejin.im/post/5e1c41c6f265da3e152d1e62
来源：掘金
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

对上文的总结.

### Shuffle 的核心要点

##### 什么是shuffle?

需要统筹全局的算子,sort就是一个shuffle算子.

##### 什么是stage

以shuffle算子为届,例如,sort前面一部分是一个stage,sort后面是一个stage.

#### ShuffleMapStage与ResultStage

ShuffleMapStage :	 sort前面的就是.

ResultStage: 	sort后面的部分就是.

#### ShuffleRead阶段和ShuffleWrite阶段

一个shuffle分为ShuffleRead阶段和ShuffleWrite阶段阶段,

一个是读数据,一个是写数据.



作者：十一喵先森
链接：https://juejin.im/post/5e1c41c6f265da3e152d1e62
来源：掘金
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
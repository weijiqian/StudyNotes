### 1. Alibaba Java Coding Guidelines

【**`阿里巴巴代码规范检查插件`**】

### 2. FindBugs-IDEA

【**`Bug检查插件`**】

***① 功能：***

> 这个插件可以帮助我们查找隐藏的bug,比较重要的功能就是查找潜在的null指针。
>
> 可以分析当前文件，包下面的所有文件，整个module下的文件，整个project下的文件。可以帮助我们检查隐藏的Bug。

***② 使用方法：***

> 在文件上或文件里面点击鼠标右键，选择FingBugs



### 3. Key promoter

【**`快捷键提示插件`**】

***① 功能：***

> 当您在IDE内的按钮上使用鼠标时，键启动器X会显示您本该使用的键盘快捷键。

***② 使用方法：***

> 当你点击鼠标一个功能的时候，可以提示你这个功能快捷键是什么。
>
> 比如我点击过debug,当我下次再次点击的时候，它会提示 debug的快捷键是 Ctrl + Shift + F10

### 4. Translation

【**`翻译的插件`**】

***① 功能：***

> 在平时的开发中，有时候对于变量的命名是否很头疼，这款插件可以帮你忙。

***② 使用方法：***

> 选中你要翻译的汉语或英文，点击鼠标右键，选择Translate, （快捷键是Ctrl + Shift +Y）就会实现翻译，不用再去切换屏幕使用翻译软件翻译了。

### 5. Maven Helper

【**`分析依赖冲突的插件`**】

***① 功能：***

> 此插件可用来方便显示maven的依赖树，和显示冲突，在我们梳理依赖时帮助很大。

***② 使用方法：***

> 安装好后在pom文件的左下角有两个tab，打开Dependency Analyzer:

### 6. Free Mybatis plugin

【**`增强idea对mybatis支持的插件`**】

***① 功能：***

> - 生成mapper xml文件
> - 快速从代码跳转到mapper及从mapper返回代码
> - mybatis自动补全及语法错误提示
> - 集成mybatis generator gui界面

这个插件超级实用，可以从mapper接口跳转到mybatis的xml文件中，还能找到对应的方

***② 使用方法：***

> 点击箭头可以实现跳转

### 7. Grep Console

【**`日志高亮显示插件`**】

***① 功能：***

> 当你密密麻麻一大片的日志，去查看起来，很容易看花眼；使用该插件实现高亮显示

### 8. Rainbow Brackets

***功能：***

> 可以实现配对括号相同颜色，并且实现选中区域代码高亮的功能。

### 9. Lombok

***功能：***

> 当我们创建一个实体时，通常对每个字段去生成GET/SET方法，但是万一后面需要增加或者减少字段时，又要重新的去生成GET/SET方法，非常麻烦。可以通过该插件，无需再写那么多冗余的get/set代码。

注意：需要在pom引入依赖

<!--lombok用来简化实体类：需要安装lombok插件-->
<dependency>
	<groupId>org.projectlombok</groupId>
	<artifactId>lombok</artifactId>
</dependency>

### 10. CodeGlance

【**`代码编辑区缩略图插件`**】

***功能***：

> 可以快速定位代码，使用起来比拖动滚动条方便多了





————————————————
版权声明：本文为CSDN博主「扬帆向海」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_43570367/article/details/103978005
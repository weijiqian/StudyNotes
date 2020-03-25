## 安装

下载地址 : [[清华大学开源软件镜像站](https://mirrors.tuna.tsinghua.edu.cn/)](https://mirrors.tuna.tsinghua.edu.cn/help/anaconda/)



## 操作



#### 常用命令

```
conda --version 查看conda版本
conda -V
conda --help   获取帮助
conda -h
conda update --help 
conda remove --help
```



- 1 查看环境

  ```
  conda env list
  或者
  conda info --envs
  ```

- 2 创建环境

  ```
  conda create --name your_env_name python=3.5 
  或者  
  conda create -n your_env_name
  ```

- 3 更新环境

  ```
  conda update --all
  conda create -n learningpy python=3.7
  ```

- 4 切换环境

  ```
  windows
  activate 环境名
  
  退出时记得退出命令哦
  deactivate
  
  linux和mac用户的命令不一样
  source activate 环境名
  source deactivate 环境名
  
  ```

- 5 其他命令

  ```shell
  创建一个新环境想克隆一部分旧的环境
  conda create -n your_env_name --clone oldname
  删除某个环境
  conda remove -n your_env_name --all
  导出环境配置（非常有用，比如你想帮朋友安装和你一模一样的环境，你可以直接导出一个配置文件给他，就能免除很多人力安装调试)
  conda env export > environment.yml
  将会在当前目录生成一个environment.yml,你把它交给小伙伴或拷到另一台机器，小伙伴只需要对这个文件执行命令  
  conda env create -f environment.yml
  就可以生成和你原来一模一样的环境啦
  
  
  
  ```

- 6 包管理

  ```
  conda list 列举当前环境下的所有包
  conda list -n packagename 列举某个特定名称包
  conda install packagename 为当前环境安装某包
  conda install -n envname packagename 为某环境安装某包
  conda search packagename 搜索某包
  conda updata packagename 更新当前环境某包
  conda update -n envname packagename 更新某特定环境某包
  conda remove packagename 删除当前环境某包
  conda remove -n envname packagename 删除某环境环境某包
  
  
  
  ```

- 7 添加其他源

  ```shell
  conda config --add channels https://mirrors.tuna.tsinghua.edu.cn/anaconda/pkgs/free/
  conda config --add channels https://mirrors.tuna.tsinghua.edu.cn/anaconda/pkgs/main/
  conda config --set show_channel_urls yes 在包后面显示来源
  ```

  



作者：MachinePlay
链接：https://www.jianshu.com/p/742dc4d8f4c5
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。


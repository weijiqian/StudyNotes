#!/usr/bin/python
# encoding: utf-8
"""
@author: Tom
@file: 疫情图.py
@time: 2020-03-18 21:47
"""

import pandas as pd
from pyecharts.charts import Pie
from pyecharts import options as opts

provinces = ['北京','上海','黑龙江','吉林','辽宁','内蒙古','新疆','西藏','青海','四川','云南','陕西','重庆',
             '贵州','广西','海南','澳门','湖南','江西','福建','安徽','浙江','江苏','宁夏','山西','河北','天津']
num = [1,1,1,17,9,22,23,42,35,7,20,21,16,24,16,21,37,12,13,14,13,7,22,8,16,13,13]
color_series = ['#FAE927','#E9E416','#C9DA36','#9ECB3C','#6DBC49',
                '#37B44E','#3DBA78','#14ADCF','#209AC9','#1E91CA',
                '#2C6BA0','#2B55A1','#2D3D8E','#44388E','#6A368B'
                                                        '#7D3990','#A63F98','#C31C88','#D52178','#D5225B',
                '#D02C2A','#D44C2D','#F57A34','#FA8F2F','#D99D21',
                '#CF7B25','#CF7B25','#CF7B25']



# 创建数据框
df = pd.DataFrame({'provinces': provinces, 'num': num})
# 降序排序
df.sort_values(by='num', ascending=False, inplace=True)

# 提取数据
v = df['provinces'].values.tolist()
d = df['num'].values.tolist()

# 实例化Pie类
pie1 = Pie(init_opts=opts.InitOpts(width='1350px', height='750px'))
# 设置颜色
pie1.set_colors(color_series)
# 添加数据，设置饼图的半径，是否展示成南丁格尔图
pie1.add("", [list(z) for z in zip(v, d)],
         radius=["30%", "135%"],
         center=["50%", "65%"],
         rosetype="area"
         )
# 设置全局配置项
pie1.set_global_opts(title_opts=opts.TitleOpts(title='玫瑰图示例'),
                     legend_opts=opts.LegendOpts(is_show=False),
                     toolbox_opts=opts.ToolboxOpts())
# 设置系列配置项
pie1.set_series_opts(label_opts=opts.LabelOpts(is_show=True, position="inside", font_size=12,
                                               formatter="{b}:{c}天", font_style="italic",
                                               font_weight="bold", font_family="Microsoft YaHei"
                                               ),
                     )
# 生成html文档
pie1.render('南丁格尔玫瑰图.html')


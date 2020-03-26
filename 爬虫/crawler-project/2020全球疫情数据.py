#!/usr/bin/python
# encoding: utf-8
"""
@author: Tom
@file: 2020全球疫情数据.py
@time: 2020-03-25 21:51
"""

import time
import json
import requests
from datetime import datetime
import pandas as pd
import numpy as np

'''
疫情数据源
    丁香园:https://ncov.dxy.cn/ncovh5/view/pneumonia?scene=2&clicktime=1579579384&enterid=1579579384&from=groupmessage&isappinstalled=0
    腾讯:  https://view.inews.qq.com/g2/getOnsInfo?name=disease_h5&callback=jQuery341001657575837432268_1581070969707&_=1581070969708


https://github.com/wmathor/2019-nCoV

'''


# 抓取数据
def catch_data():
    url = 'https://view.inews.qq.com/g2/getOnsInfo?name=disease_h5'
    reponse = requests.get(url=url).json()
    #返回数据字典
    data = json.loads(reponse['data'])
    return data

#!/usr/bin/python
# encoding: utf-8
"""
@author: Tom
@file: 2020中国疫情数据.py
@time: 2020-03-25 21:42
————————————————
版权声明：本文为CSDN博主「Hakuna_Matata_001」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_43130164/article/details/104113559
"""
import requests
import json
def get_data():
    url = 'https://view.inews.qq.com/g2/getOnsInfo?name=disease_h5&callback=jQuery341001657575837432268_1581070969707&_=1581070969708'
    headers = {'user-agent': 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Mobile Safari/537.36'}
    res = requests.get(url, headers=headers).text
    a = res.split('jQuery341001657575837432268_1581070969707(')[1].split(')')[0]
    c = json.loads(a)
    data = json.loads(c['data'])
    return data

def print_data_china():
    data = get_data()
    print('统计截至时间：'+str(data['lastUpdateTime']))
    print('全国确诊人数：'+str(data['chinaTotal']['confirm']))
    print('相较于昨天确诊人数：'+str(data['chinaAdd']['confirm']))
    print('全国疑似病例：'+str(data['chinaTotal']['suspect']))
    print('相较于昨天疑似人数：'+str(data['chinaAdd']['suspect']))
    print('全国治愈人数：'+str(data['chinaTotal']['heal']))
    print('相较于昨天治愈人数：'+str(data['chinaAdd']['heal']))
    print('全国死亡人数：'+str(data['chinaTotal']['dead']))
    print('相较于昨天死亡人数：'+str(data['chinaAdd']['dead']))

def print_data_path_china():
    data = get_data()['areaTree'][0]['children']
    path_data = []
    path_china = []
    path = str(input('请输入你要查询的省份：'))
    for i in data:
        path_china.append(i['name'])
        path_data.append(i['children'])
    if path in path_china:
        num = path_china.index(path)
        data_path = path_data[num]
        print('{:^10}{:^10}{:^10}{:^10}{:^10}{:^10}{:^10}{:^10}{:^10}'.format('地区','累计确诊人数','相较于昨日确诊人数','累计疑似病例','相较于昨日疑似病例','累计治愈人数','相较于昨日治愈人数','累计死亡人数','相较于昨日死亡人数'))
        for i in data_path:
            name = i['name']
            today = i['today']
            total = i['total']
            a = '{:^10}{:^15}{:^15}{:^15}{:^15}{:^15}{:^15}{:^15}{:^15}'
            print(a.format(name, str(total['confirm']), str(today['confirm']), str(total['confirm']), str(today['suspect']), str(total['heal']), str(today['heal']), str(total['dead']), str(today['dead'])))

if __name__ == '__main__':
    get_data()
    print_data_china()
    print_data_path_china()

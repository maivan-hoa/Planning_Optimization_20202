# -*- coding: utf-8 -*-
"""
Created on Sat Apr 24 22:05:49 2021

@author: HoaMV
"""

def read_data(filename):
    with open(filename) as f:
        n = int(f.readline())
        e = []
        l = []
        d = []
        for i in range(n):
            temp = list(map(int, f.readline().split()))
            e.append(temp[0])
            l.append(temp[1])
            d.append(temp[2])
        t = []
        for i in range(n):
            t.append(list(map(int, f.readline().split())))

        c = []
        for i in range(n):
            c.append(list(map(int, f.readline().split())))

    return n, e, l, d, t, c

'''
    n: số khách hàng
    e: thời điểm đầu của yêu cầu giao hàng
    l: thời điểm cuối của yêu cầu giao hàng
    d: thời gian giao hàng
    t: thời gian đi từ i->j
    c: khoảng cách từ i->j
'''
# -*- coding: utf-8 -*-
"""
Created on Sun Apr 25 12:37:08 2021

@author: HoaMV
"""

# Tạo một đường đi hợp lệ về ràng buộc thời gian 

import random
import numpy as np
import math

# Số điểm trên đồ thị
n = 500

route = [i for i in range(1, n)]
random.shuffle(route)
route.insert(0, 0)

# Tạo ma trận thời gian di chuyển
t = np.random.randint(1, 100, (n, n))

# Tạo ma trận khoảng cách thỏa mãn bất đằng thức tam giác
# tọa độ x
x = []
for i in range(n):
    a = random.randint(1, 50)
    b = random.randint(1, 50)
    x.append(a+b)
# tọa độ y
y = []
for i in range(n):
    a = random.randint(1, 50)
    b = random.randint(1, 50)
    y.append(a+b)


c = np.zeros((n, n), dtype=int)
for i in range(n):
    for j in range(n):
        c[i][j] = round(math.sqrt((x[i]-x[j])**2 + (y[i]-y[j])**2))

for i in range(n):
    t[i][i] = 0
    c[i][i] = 0

# thời gian đầu tại mỗi node
e = [0]*n
# thời gian cuối tại mỗi node
l = [0]*n
# thời gian phục vụ
d = [0]*n

sumtime = 0 # thời điểm đến node nào đó
for i in range(1, n):
    # tạo ngẫu nhiên thời gian phục vụ tại node i
    d[route[i]] = random.randint(1, 10)
    # thời điểm bắt đầu phục vụ tại node i-1
    sumtime = max(sumtime, e[route[i-1]])
    # thời điểm đến node i
    sumtime += t[route[i-1]][route[i]] + d[route[i-1]]
    # tạo thời điểm bắt đầu tại node i
    e[route[i]] = random.randint(max(0, sumtime-5), sumtime+5)
    # tạo thời điểm kết thúc tại node i
    l[route[i]] = random.randint(max(sumtime, e[route[i]])+5, max(sumtime, e[route[i]])+d[route[i]] + 10)


sumtime = max(sumtime, e[route[i]])
sumtime += t[route[i]][route[0]] + d[route[i]]

# Thời điểm cuối trở về 0
l[0] = sumtime + 5

# ghi ra file
with open('./data/data_'+str(n)+'.txt', 'w') as f:
    f.write(str(n)+ '\n')
    for i in range(n):
        f.write(str(e[i]) + ' ' + str(l[i]) + ' ' + str(d[i]) + '\n')
    
    for i in range(n):
        f.write(' '.join([str(j) for j in t[i]]) + '\n')
    
    for i in range(n):
        f.write(' '.join([str(j) for j in c[i]]) + '\n')
    
    cost = 0
    for i in range(1, n):
        cost += c[route[i-1]][route[i]]
    cost += c[route[i]][0]
    f.write('Cost:  ' + str(cost) + '\n')
    f.write('Route:  ')
    for i in range(n):
        f.write(str(route[i]) + '  ')















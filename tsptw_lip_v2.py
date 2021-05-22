# -*- coding: utf-8 -*-
"""
Created on Wed Apr 28 21:00:00 2021

@author: HoaMV

TSPTW giải bằng mô hình quy hoạch tuyến tính, tạo điểm giả n+1 là điểm trở về
"""

from ortools.linear_solver import pywraplp
from read_data import read_data
import numpy as np
import time

solver = pywraplp.Solver.CreateSolver('CBC')
INF = 1e5

file_name = 'data_500.txt'
n, e, l, d, t, c = read_data('./data/'+file_name)

# Thêm điểm giả trở về 0
e.append(e[0])
l.append(l[0])
d.append(d[0])

C = np.zeros((n+1, n+1))
T = np.zeros((n+1, n+1))
C[:n, :n] = c
T[:n, :n] = t

for i in range(n):
    C[i, n] = C[i, 0]
    C[n, i] = C[0, i]

    T[i, n] = T[i, 0]
    T[n, i] = T[0, i]

'''
    n: số khách hàng + depot
    e: thời điểm đầu của yêu cầu giao hàng
    l: thời điểm cuối của yêu cầu giao hàng
    d: thời gian giao hàng 
    t: thời gian đi từ i->j
    c: khoảng cách từ i->j
'''

# Định nghĩa các biến quyết định và phụ trợ

# x[i][j] = 1 nếu hành trình có đường đi từ i đến j
x = [[solver.IntVar(0, 1, 'x['+str(i)+']['+str(j)+']') if i!=j else 
      solver.IntVar(0, 0, 'x['+str(i)+']['+str(j)+']') for j in range(n+1)] for i in range(n+1)]

# y[i] là độ dài đường đi từ 0 đến i
y = [solver.IntVar(0, int(np.sum(C)), 'y['+str(i)+']') for i in range(n+1)]

# p[i] là thời điểm người giao hàng đến điểm i
p = [solver.IntVar(0, int(np.sum(T)), 'p['+str(i)+']') for i in range(n+1)]

# w[i] là thời điểm bắt đầu phục vụ tại điểm i, w[i] = max(p[i], e[i])
w = [solver.IntVar(0, int(np.sum(T)), 'w['+str(i)+']') for i in range(n+1)]

# b[i] là biến nhị phân, b=1 có nghĩa p[i] >= e[i] và ngược lại
b = [solver.IntVar(0, 1, 'b['+str(i)+']') for i in range(n+1)]

# Ràng buộc

cos = solver.Constraint(0, 0)
cos.SetCoefficient(w[0], 1)

cos = solver.Constraint(0, 0)
cos.SetCoefficient(p[0], 1)

cos = solver.Constraint(0, 0)
cos.SetCoefficient(y[0], 1)

# Ràng buộc cân bằng luồng
for i in range(1, n):
    cos = solver.Constraint(1, 1)
    for j in range(1, n+1):
        if i != j:
            cos.SetCoefficient(x[i][j], 1)

for i in range(1, n):
    cos = solver.Constraint(1, 1)
    for j in range(0, n):
        if i != j:
            cos.SetCoefficient(x[j][i], 1)

cos = solver.Constraint(1, 1)
for j in range(1, n):
    cos.SetCoefficient(x[0][j], 1)

cos = solver.Constraint(1, 1)
for j in range(1, n):
    cos.SetCoefficient(x[j][n], 1)

# Ràng buộc thời gian
for i in range(n+1):
    for j in range(n+1):
        if i != j:
            # Thời gian             
            cos = solver.Constraint(-INF - d[i] - T[i][j], INF)
            cos.SetCoefficient(w[i], 1)
            cos.SetCoefficient(x[i][j], -INF)
            cos.SetCoefficient(p[j], -1)
            
            cos = solver.Constraint(-INF, INF - d[i] -T[i][j])
            cos.SetCoefficient(w[i], 1)
            cos.SetCoefficient(p[j], -1)
            cos.SetCoefficient(x[i][j], INF)
            
            # Khoảng cách
            cos = solver.Constraint(-INF - C[i][j], INF)
            cos.SetCoefficient(y[i], 1)
            cos.SetCoefficient(x[i][j], -INF)
            cos.SetCoefficient(y[j], -1)
            
            cos = solver.Constraint(-INF, INF - C[i][j])
            cos.SetCoefficient(y[i], 1)
            cos.SetCoefficient(y[j], -1)
            cos.SetCoefficient(x[i][j], INF)
            
            
            
for i in range(n+1):
    # Ràng buộc với p
    cos = solver.Constraint(-INF, l[i])
    cos.SetCoefficient(p[i], 1)
    
    # Ràng buộc với biến b
    cos = solver.Constraint(-INF, -e[i] + INF)
    cos.SetCoefficient(p[i], -1)
    cos.SetCoefficient(b[i], INF)
    
    cos = solver.Constraint(-INF, e[i])
    cos.SetCoefficient(p[i], 1)
    cos.SetCoefficient(b[i], -INF)
    
    # Ràng buộc với w
    cos = solver.Constraint(0, INF)
    cos.SetCoefficient(w[i], 1)
    cos.SetCoefficient(p[i], -1)
    
    cos = solver.Constraint(e[i], INF)
    cos.SetCoefficient(w[i], 1)

    cos = solver.Constraint(-INF, INF)
    cos.SetCoefficient(w[i], 1)
    cos.SetCoefficient(p[i], -1)
    cos.SetCoefficient(b[i], INF)
    
    cos = solver.Constraint(-INF, e[i])
    cos.SetCoefficient(w[i], 1)
    cos.SetCoefficient(b[i], -INF)



# Hàm mục tiêu       
obj = solver.Objective()
obj.SetCoefficient(y[n], 1)

start = time.time()
status = solver.Solve()
assert status == pywraplp.Solver.OPTIMAL
end = time.time()
print('Time execute: ', end-start)
print('Number node: ', n)
print('Cost: ', obj.Value())

res = [0]
tmp = 0
while tmp != n:
    for i in range(n+1):
        if x[tmp][i].solution_value() == 1:
            res.append(i)
            tmp = i
            break
print('Route:')
print(res[:-1])

























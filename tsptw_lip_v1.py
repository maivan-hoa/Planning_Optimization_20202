# -*- coding: utf-8 -*-
"""
Created on Tue Apr 13 14:59:00 2021

@author: HoaMV

TSPTW giải bằng mô hình quy hoạch tuyến tính, thêm dần ràng buộc SEC
"""

from ortools.linear_solver import pywraplp
from read_data import read_data
import numpy as np
import time

solver = pywraplp.Solver.CreateSolver('CBC')
# INF = solver.infinity()
INF = 1e5

file_name = 'data_10.txt'
n, e, l, d, t, c = read_data('./data/'+file_name)

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
      solver.IntVar(0, 0, 'x['+str(i)+']['+str(j)+']') for j in range(n)] for i in range(n)]

# p[i] là thời điểm người giao hàng đến điểm i
p = [solver.IntVar(0, int(np.sum(t)), 'p['+str(i)+']') for i in range(n)]

# w[i] là thời điểm bắt đầu phục vụ tại điểm i, w[i] = max(p[i], e[i])
w = [solver.IntVar(0, int(np.sum(t)), 'w['+str(i)+']') for i in range(n)]

# b[i] là biến nhị phân, b=1 có nghĩa p[i] >= e[i] và ngược lại
b = [solver.IntVar(0, 1, 'b['+str(i)+']') for i in range(n)]


# Ràng buộc cân bằng luồng
for i in range(n):
    cos = solver.Constraint(1, 1)
    for j in range(n):
        if i != j:
            cos.SetCoefficient(x[i][j], 1)

for i in range(n):
    cos = solver.Constraint(1, 1)
    for j in range(n):
        if i != j:
            cos.SetCoefficient(x[j][i], 1)


# Ràng buộc thời gian
for i in range(n):
    for j in range(n):
        if i != j:            
            cos = solver.Constraint(-INF - d[i] - t[i][j], INF)
            cos.SetCoefficient(w[i], 1)
            cos.SetCoefficient(x[i][j], -INF)
            cos.SetCoefficient(p[j], -1)
            
            cos = solver.Constraint(-INF, INF - d[i] -t[i][j])
            cos.SetCoefficient(w[i], 1)
            cos.SetCoefficient(p[j], -1)
            cos.SetCoefficient(x[i][j], INF)
            
            
            
for i in range(n):
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
    if i != 0: # Tại điểm 0, không cần ràng buộc này
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
    

cos = solver.Constraint(0, 0)
cos.SetCoefficient(w[0], 1)


# Hàm mục tiêu       
obj = solver.Objective()
for i in range(n):
    for j in range(n):
        if i != j:
            obj.SetCoefficient(x[i][j], c[i][j])
            

# tìm chu trình bắt đầu từ đỉnh i
def extract_cycle(i, x):
    cyc = []
    while i not in cyc:
        cyc.append(i)
        for j in range(n):
            if x[i][j] == 1:
                i = j
                break
    return cyc

# giải bài toán TSP với một tập các ràng buộc SEC tìm được
def Solver(SEC):
    for cyc in SEC:
        cos = solver.Constraint(-INF, len(cyc)-1)
        for i in cyc:
            for j in cyc:
                if i != j:
                    cos.SetCoefficient(x[i][j], 1)
    status = solver.Solve()
    assert status==pywraplp.Solver.OPTIMAL
    
    res = [[x[i][j].solution_value() for j in range(n)] for i in range(n)]
    #print(res)
    return res


# tìm và thêm dần các ràng buộc SEC
def SolverDyn(n):
    SEC = []
    while True:
        res = Solver(SEC)
        mark = [False for i in range(n)] # đánh dấu các node của kết quả hiện tại đã thuộc một cycle nào đó, không cần xét đến nữa
        for i in range(n):
            if mark[i] == False:
                cycle = extract_cycle(i, res)
                if len(cycle) == n:
                    return cycle # trả về kết quả nếu chu trình đúng bằng n
                elif len(cycle) > 1:
                    SEC.append(cycle)
                    for j in cycle:
                        mark[j] = True # đánh dấu k cần xét nữa


start = time.time()
res = SolverDyn(n)
end = time.time()
print('Time execute: ', end-start)
print('Number node: ', n)
print('Cost: ', obj.Value())
print('Route')
print(res)









        
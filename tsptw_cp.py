# -*- coding: utf-8 -*-
"""
Created on Tue Apr 13 14:59:00 2021

@author: HoaMV

Tạo điểm logic n tham chiếu đến điểm 0 là điểm trở về
"""

from ortools.sat.python import cp_model
import numpy as np
from read_data import read_data
import time

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


model = cp_model.CpModel()

# x[i] = j có nghĩa j là điểm tiếp theo của i trên hành trình
x = [model.NewIntVar(1, n, 'x['+str(i)+']') for i in range(n)]

# y[i] là độ dài hành trình từ điểm 0 đến i
y = [model.NewIntVar(0, int(np.sum(C)), 'y['+str(i)+']') for i in range(n+1)]

# p[i] là thời điểm người giao hàng đến i
p = [model.NewIntVar(0, int(np.sum(T)), 'p['+str(i)+']') for i in range(n+1)]

# w[i] là thời điểm bắt đầu phục vụ tại điểm i
w = [model.NewIntVar(0, int(np.sum(T)), 'w['+str(i)+']') for i in range(n)]

for i in range(n):
    model.Add(x[i] != i)

model.AddAllDifferent(x)
# model.Add(y[0] == 0)
# model.Add(p[0] == 0)

# Ràng buộc đến sớm hơn e[i]
for i in range(n):
    model.AddMaxEquality(w[i], [p[i], e[i]])

for i in range(n):
    for j in range(1, n+1):
        if i != j:
            b = model.NewBoolVar('b')
            model.Add(x[i] == j).OnlyEnforceIf(b)
            model.Add(x[i] != j).OnlyEnforceIf(b.Not())
            model.Add(y[j] == y[i] + int(C[i][j])).OnlyEnforceIf(b)
            model.Add(p[j] == w[i] + d[i] + int(T[i][j])).OnlyEnforceIf(b)


for i in range(n+1):
    model.Add(p[i] <= l[i])

model.Minimize(y[n])
solver = cp_model.CpSolver()

print('start')
start = time.time()
print(start)
status = solver.Solve(model)

end = time.time()
print(end)
if status == cp_model.FEASIBLE or status == cp_model.OPTIMAL:
    print('Time execute:  ', end-start)
    print('Number node: ', n)
    print('Cost: ', solver.Value(y[n]))
    res = [0]
    tmp = 0
    print('Route: ')
    for i in range(1, n):
        res.append(solver.Value(x[tmp]))
        tmp = solver.Value(x[tmp])
    print(res)
    # for i in range(n):
    #     print('{} --> {}'.format(i, solver.Value(x[i])))
elif status == cp_model.INFEASIBLE:
    print('INFEASIBLE')
elif status == cp_model.MODEL_INVALID:
    print('MODEL_INVALID')
elif status == cp_model.UNKNOWN:
    print('UNKNOWN')





















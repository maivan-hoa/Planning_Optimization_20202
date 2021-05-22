import numpy as np
import random as rd
import time


#đọc file
def read_data(filename):
    with open(filename) as f:
        n = int(f.readline())
        timeviolations = []
        timegive = []

        for i in range(n):
            temp = list(map(int, f.readline().split()))
            timeviolations.append([temp[0], temp[1]])
            timegive.append(temp[2])
        timedrive = []
        for i in range(n):
            timedrive.append(list(map(int, f.readline().split())))

        quangduong = []
        for i in range(n):
            quangduong.append(list(map(int, f.readline().split())))

    return n, timeviolations,timegive, timedrive, quangduong





n,timeviolations,timegive,timedrive,quangduong= read_data('./data/data_20.txt')
x_min=np.zeros(n)#Cấu hình đường đi chi phí nhỏ nhất và số vi phạm nhỏ nhất
x=np.zeros(n)#cấu hình đường đi hiện tại

#timeviolations[][]  thời gian giao hàng không vi phạm ràng buộc (từ e[i] đến l[i])
#timegive[]  thời gian giao hàng tại điểm i
#timedrive[][] thời gian di chuyển từ i đến j
#quangduong[][] Quãng đường đi từ i đến j

#hàm đổi chỗ 2 vị trí di chuyển q và r cho nhau
def swap(q,r):
    a=x[q]
    x[q]=x[r]
    x[r]=a

#Cài đặt cấu hình ban đầu
def gennerate_initial_solution():
    for i in range(n):
        x[i]=i

def violations():
    v = 0
    time = 0.0
    for i in range(n - 2):
        diemdau = int(x[i])
        diemden = int(x[i + 1])
        time = time + timedrive[diemdau][diemden]
        if time < timeviolations[diemden][0]:
            time = timeviolations[diemden][0]
        time = time + timegive[diemden]


        vi_tri_j = int(x[i+2])
        if time + timedrive[diemden][vi_tri_j] > timeviolations[vi_tri_j][1]:
            v = v + 1


    return v


def violations_point(q):
    v = 0
    time = 0.0
    if q!=n-1:
        for i in range(q):
            diemdau = int(x[i])
            diemden = int(x[i + 1])
            time = time + timedrive[diemdau][diemden]
            if time < timeviolations[diemden][0]:
                time = timeviolations[diemden][0]
            time = time + timegive[diemden]
        diemden=int(x[q])
        vi_tri_j = int(x[q+1])
        if time + timedrive[diemden][vi_tri_j] > timeviolations[vi_tri_j][1]:
            v = v + 1

    return v
'''def violations_point(q):
    v=0
    diemden=int(x[q])
    time=timegive[diemden]+timeviolations[diemden][0]
    for i in range(q+1,n):
        next_point= int(x[i])
        if time+timedrive[diemden][next_point]>timeviolations[next_point][1]:
          v= v+1
    return v'''
def select_most_violating_point():
    sel_q = -1
    max_violations = 0
    cand = []
    for q in range(1,n):
        vp = violations_point(q)
        if max_violations < vp:
            max_violations= vp
            cand.clear()
            cand.append(q)
        elif max_violations == vp:
            cand.append(q)

    idx = rd.randint(0, len(cand)-1)
    sel_q = cand[idx]

    return sel_q


def violations_swap_q_r(q,r):
    swap(q,r)
    v = violations()
    swap(q,r)
    return v

def select_most_promissing_point(q):
    min_violations = int(1e9)
    sel_p = -1
    cand = []
    for r in range(1,n):
        if r!=q:
            vr = violations_swap_q_r(q,r)
            if min_violations > vr:
                min_violations = vr
                cand.clear()
                cand.append(r)
            elif min_violations == vr:
                cand.append(r)
    idx = rd.randint(0,len(cand) - 1)
    sel_r = cand[idx]
    return  sel_r


def tongchiphi():
    chiphi=0
    for i in range(n-1):
        diemdau=int(x[i])
        diemden = int(x[i+1])
        chiphi= chiphi+ quangduong[diemdau][diemden]
    n_0 = int(x[n-1])
    chiphi = chiphi + quangduong[n_0][0]
    return chiphi

def solve():
    gennerate_initial_solution()
    min_chiphi = int(1e9)
    vipham=(1e9)
    k=0
    while 1:
        q = select_most_violating_point()
        v = select_most_promissing_point(q)
        swap(q,v)
        h = violations()
        a = tongchiphi()
        print ('step', k,': swap ',q,'to',v,'||violations = ',h,'||chiphi=',a)
        k=k+1
        #if min_chiphi> a:
        if vipham>=h:
            vipham=h
            min_chiphi= a
            for i in range(n):
                x_min[i]=x[i]
        if h==0:
            break
    for i in range(n):
        print(x_min[i])
        x[i]=x_min[i]
start= time.time()
solve()
end = time.time()
print('thoi gian thuc hien',end-start)
print('chi phi min:',tongchiphi())
print('violations khi chi phi min:',violations())


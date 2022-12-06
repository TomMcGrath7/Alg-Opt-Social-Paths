from scipy.optimize import linprog # min c'x s.t A_ub <= b_ub and A_eq = b_eq
import numpy as np
import random
import itertools


""" 4.1 Exercise 1 """

# def knapsack_LP(n, p, w, C):
#     # np.expand_dims(w, 1) in A_ub and [C] in b_ub is the weight limit ,→constraint
#     # np.eye(len(w)) in A_ub and np.ones(len(w)) in b_ub is <=1 constraint
#     A_ub = np.concatenate((np.expand_dims(w, 1).transpose(), np.eye(len(w))))
#     b_ub = np.concatenate(([C], np.ones(len(w))))
#     sol = linprog(p*-1, A_ub=A_ub, b_ub=b_ub)
#     value = sol["fun"]*-1
#     solution = sol["x"]
#     return (value, solution)
#
#
# def variable_knapsack_LP(n, p, w, C, v, V):
#     A_ub = np.concatenate((np.expand_dims(w, 1).transpose(), np.expand_dims(v,1).transpose(), np.eye(len(w))))
#     b_ub = np.concatenate(([C, V], np.ones(len(w))))
#     sol = linprog(p*-1, A_ub=A_ub, b_ub=b_ub)
#     value = sol["fun"]*-1
#     solution = sol["x"]
#     return (value, solution)
#
#
# n = 20
# C = 879
# p = np.array([91, 72, 90, 46, 55, 8, 35, 75, 61, 15, 77, 40, 63, 75, 29, 75,17, 78, 40, 44])
# w = np.array([84, 83, 43, 4, 44, 6, 82, 92, 25, 83, 56, 18, 58, 14, 48, 70, 96,32, 68, 92])
# np.set_printoptions(suppress=True)
# print("Knapsack:\n",knapsack_LP(n, p, w, C))
# print("\nVariable knapsack:\n",variable_knapsack_LP(n, p, w, C, w*12, C))
#
#
# """ 4.2 Exercise 1 """
#
# def knapsack_enum(n, p, w, C):
#     count = 0
#     items = np.arange(0, len(p))
#     cur_best_val = 0
#     cur_best = list()
#     for i in range(0, len(p)):
#         for bag in itertools.combinations(items, i):
#         count+=1
#         bag = list(bag)
#         weight = sum(w[bag])
#         if weight <= C:
#             value = sum(p[bag])
#             if value > cur_best_val:
#                 cur_best_val = value
#                 cur_best = bag
#     print(count,"iterations.")
#     return cur_best_val, cur_best
#
#
# run = knapsack_enum(n, p, w, C)
# print(run)
#
# p_new = np.append(p, np.random.randint(10, 100, 3))
# w_new = np.append(w, np.random.randint(10, 100, 3))
# print(p_new,"\n", w_new)
#
# run = knapsack_enum(n, p_new, w_new, C)
# print(run)
#
#
# """ 5.2 Exercise 1"""
#
# import scipy.optimize as opt
#
# def knapsack_lp_bnb(items, max_weight):
#     n = len(items)
#     best_value = 0
#     for subset in itertools.combinations(range(n), n//2):
#         remain = [i for i in range(n) if i not in subset]
#         lp_sol = opt.linprog([item[2] for item in items],[item[1] for item in items],[max_weight for i in range(n)], [1 for i in range(n)])
#         if lp_sol.fun > best_value:
#             best_value = lp_sol.fun
#             best_subset = subset
#     return best_subset
#
#
# items = [(1, 5, 10), (2, 3, 5), (3, 4, 7), (4, 6, 12)]
# max_weight = 10
# best_subset = knapsack_lp_bnb(items, max_weight)
# print(best_subset) # should return (0, 2, 3) for items with indices 0, 2, and 3


import itertools
import scipy.optimize as opt

def knapsack_lp_bnb(items, max_weight):
    n = len(items)
    best_value = 0
    best_subset = []
    for subset in itertools.combinations(range(n), n//2):
        remain = [i for i in range(n) if i not in subset]
        lp_sol = opt.linprog([item[2] for item in items],
                             [item[1] for item in items],
                             [max_weight for i in range(n)],
                             [1 for i in range(n)])
        if lp_sol.fun > best_value:
            best_value = lp_sol.fun
            best_subset = subset
    return best_subset

# Example usage
items = [(1, 5, 10), (2, 3, 5), (3, 4, 7), (4, 6, 12)]
max_weight = 10
best_subset = knapsack_lp_bnb(items, max_weight)
print(best_subset) # should return (0, 2, 3) for items with indices 0, 2, and 3

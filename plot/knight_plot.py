#!/usr/bin/python2

import sys
import csv
import numpy as np
import matplotlib.pyplot as plt

class KnightRow():
  def __init__(self, pathLength, numNodes, time):
    self.pathLength = pathLength
    self.numNodes = numNodes
    self.time = time

data = []
with open(sys.argv[1]) as fp:
  for line in fp:
    cols = line.split()
    data += [KnightRow(float(cols[0]), float(cols[1]), float(cols[2]))]

print("Slurp done")

pathLength = np.array([x.pathLength for x in data])
numNodes = np.array([x.numNodes for x in data])
time = np.array([x.time for x in data])

print(numNodes)
print(pathLength)

def scatter(xs, ys, xl, yl, fn):
  fig = plt.figure()
  ax = fig.add_subplot(111)
  ax.set_xlabel(xl)
  ax.set_ylabel(yl)
  ax.scatter(xs, ys)
  plt.savefig(fn)

scatter(pathLength, numNodes, 'Path Length', 'Number of Nodes Expanded', 'knight.nodes.png')
scatter(pathLength, time, 'Path Length', 'Computation Time (s)', 'knight.compTime.png')
#fig = plt.figure()
#ax = fig.add_subplot(111)
#ax.set_xlabel('Path Length')
#ax.set_ylabel('Number of Nodes Expanded')
#ax.scatter(pathLength, numNodes)
#plt.savefig('knight.pathLength.png')

print("Plot done")


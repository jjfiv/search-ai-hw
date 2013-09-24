#!/usr/bin/python2

import sys
import csv
import numpy as np
import matplotlib.pyplot as plt

class TSPRow():
  def __init__(self, numCities, numNodes, time):
    self.numCities = numCities
    self.numNodes = numNodes
    self.time = time

data = []
with open(sys.argv[1]) as fp:
  for line in fp:
    cols = line.split()
    data += [TSPRow(float(cols[0]), float(cols[1]), float(cols[2]))]

print("Slurp done")

numCities = np.array([x.numCities for x in data])
numNodes = np.array([x.numNodes for x in data])
time = np.array([x.time for x in data])

print(numNodes)
print(numCities)

def scatter(xs, ys, xl, yl, fn):
  fig = plt.figure()
  ax = fig.add_subplot(111)
  ax.set_xlabel(xl)
  ax.set_ylabel(yl)
  ax.scatter(xs, ys)
  plt.savefig(fn)

scatter(numCities, numNodes, 'Number of Cities', 'Number of Nodes Expanded', 'tsp.nodes.png')
scatter(numCities, time, 'Number of Cities', 'Computation Time (s)', 'tsp.compTime.png')

print("Plot done")


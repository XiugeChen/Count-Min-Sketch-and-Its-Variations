import numpy as np
import re
import matplotlib.pyplot as plt
import pandas as pd

File = "./output.txt"

# Zipf
ROW = 9
COL = 7
X = [3,4,5,6,7,8,9]

def draw(dataType):
    fp = open(File, "r")
    # ztif
    default_weighted_mean_data = np.zeros([ROW, COL], dtype="double")
    default_median_data = np.zeros([ROW, COL], dtype="double")
    default_percentile_data = np.zeros([ROW, COL], dtype="double")
    conser_weighted_mean_data = np.zeros([ROW, COL], dtype="double")
    conser_median_data = np.zeros([ROW, COL], dtype="double")
    conser_percentile_data = np.zeros([ROW, COL], dtype="double")
    morris_weighted_mean_data = np.zeros([ROW, COL], dtype="double")
    morris_median_data = np.zeros([ROW, COL], dtype="double")
    morris_percentile_data = np.zeros([ROW, COL], dtype="double")
    file = ""

    for line in fp:
        # line contain file information
        if "####file" in line:
            file = line
            continue
        
        if dataType not in file:
            continue
        
        # find the place to insert data
        len = int(re.search('len(.+)_runningResult', file).group(1))
        j = int(round(np.log10(len) - 3))
        
        i = 0
        if "zipf" in file:
            s = float(re.search('_s(.+)_len', file).group(1))
            i = int(round(s / 0.4 - 1))
        elif "normal" in file:
            std = float(re.search('_std(.+)_len', file).group(1))
            i = int(round(np.log10(std) / 2 - 2 + 3))
        elif "uniform" in file:
            num = int(re.search('_numItems(.+)_len', file).group(1))
            i = int(round(np.log10(num) / 2 - 2 + 6))
                
        data = line.split(',')
        
        # parse data 
        if "weightedMean" in line:
            conser_weighted_mean_data[i][j] = float(data[1].split(':')[1])
            default_weighted_mean_data[i][j] = float(data[2].split(':')[1])
            morris_weighted_mean_data[i][j] = float(data[3].split(':')[1])
        elif "median" in line:
            conser_median_data[i][j] = float(data[1].split(':')[1])
            default_median_data[i][j] = float(data[2].split(':')[1])
            morris_median_data[i][j] = float(data[3].split(':')[1])
        elif "percentile" in line:
            conser_percentile_data[i][j] = float(data[1].split(':')[1])
            default_percentile_data[i][j] = float(data[2].split(':')[1])
            morris_percentile_data[i][j] = float(data[3].split(':')[1])
        elif "uniqueNum" in line:
            print(file, line)
            
    # start drawing
    plot(default_weighted_mean_data, 1)
    #plt.legend(loc='upper right')
    plot(default_median_data, 2)
    plot(default_percentile_data, 3)
    plot(conser_weighted_mean_data, 4)
    plot(conser_median_data, 5)
    plot(conser_percentile_data, 6)
    plot(morris_weighted_mean_data, 7)
    plot(morris_median_data, 8)
    plot(morris_percentile_data, 9)
    plt.show()
    
def plot(data, index):
    plt.subplot(3, 3, index)
    df = pd.DataFrame({'x':X, 'zpif-s0.4':data[0], 'zpif-s0.8':data[1], 'zpif-s1.2':data[2],
        'normal-std10^4':data[3], 'normal-std10^6':data[4], 'normal-std10^8':data[5],
        'uniform-#10^4':data[6], 'uniform-#10^6':data[7], 'uniform-#10^8':data[8] })
    
    plt.plot('x', 'zpif-s0.4', data=df, marker='', color='#ECC2BE', linewidth=1)
    plt.plot('x', 'zpif-s0.8', data=df, marker='', color='#F81E09', linewidth=1)
    plt.plot('x', 'zpif-s1.2', data=df, marker='', color='#89423B', linewidth=1)
    plt.plot('x', 'normal-std10^4', data=df, marker='', color='#B9C7F3', linewidth=1)
    plt.plot('x', 'normal-std10^6', data=df, marker='', color='#1349F5', linewidth=1)
    plt.plot('x', 'normal-std10^8', data=df, marker='', color='#435797', linewidth=1)
    plt.plot('x', 'uniform-#10^4', data=df, marker='', color='#BAF5C5', linewidth=1)
    plt.plot('x', 'uniform-#10^6', data=df, marker='', color='#1DFA46', linewidth=1)
    plt.plot('x', 'uniform-#10^8', data=df, marker='', color='#43A956', linewidth=1)
            
draw("constant")
#draw("increment_pos")
#draw("increment_all")
import numpy as np
import random
import math
from scipy import sparse
from os import listdir
from os.path import isfile, join

MAX_INT = 2147483647
SOURCE_FOLDER = "./"
UPDATE_FOLDER = "./dataWithUpdate/"

# generate stream with F1 = cn with constant c and stream length n
def increment(file, min_value, max_value, sign):
    read_file = SOURCE_FOLDER + file
    write_file = UPDATE_FOLDER + "increment_" + sign + '_max' + str(max_value) + "_" + file
    
    record = np.zeros(MAX_INT, dtype = int)
    record_count = np.zeros(MAX_INT, dtype = int)
    
    read_fp = open(read_file, "r")
    write_fp = open(write_file, "w")
    
    print("####INFO: increment started")
    
    i = 1
    for line in read_fp:
        data = line.rstrip().split(',');
        sample = int(data[0])
        current_value = record[sample]
        update = random.randint(1, max_value)
        
        if "pos" not in sign:
            new_min = (int) (min_value / math.log10(-min_value))
            update = random.randint(new_min, max_value)
            
            while (update + current_value < 0):
                update = random.randint(new_min, max_value)
                
        if (update + current_value > MAX_INT):
            update = 0
        
        record[sample] += update
        record_count[sample] += 1
        write_fp.write(str(sample) + "," + str(update) + "\n")
        i += 1
    
    print("####INFO: increment finished, start write record")
    
    record_sp = sparse.csr_matrix(record)
    record_count_sp = sparse.csr_matrix(record_count)
    _, index = record_count_sp.nonzero()
    
    write_fp.write("########INFORMATION SECTION########\n") 
    for i, value, count in zip(index, record_sp.data, record_count_sp.data):
        write_fp.write("#," + str(i) + "," + str(value) + "," + str(count) + "\n") 
    
    write_fp.close()
    return

# generate stream with F1 = constant
def constant(file, min_value, max_value):
    read_file = SOURCE_FOLDER + file
    write_file = UPDATE_FOLDER + "constant_" + 'max' + str(max_value) + "_" + file
    
    record = np.zeros(MAX_INT, dtype = int)
    record_count = np.zeros(MAX_INT, dtype = int)
    
    read_fp = open(read_file, "r")
    write_fp = open(write_file, "w")
    
    print("####INFO: constant started")
    
    for line in read_fp:
        data = line.rstrip().split(',');
        sample = int(data[0])
        current_value = record[sample]
        update = random.randint(min_value, max_value)
        
        while (update + current_value < 0 or update + current_value > max_value):
            update = random.randint(min_value, max_value)
        
        record[sample] += update
        record_count[sample] += 1
        write_fp.write(str(sample) + "," + str(update) + "\n")
        
    print("####INFO: constant finished, start write record")
    
    record_sp = sparse.csr_matrix(record)
    record_count_sp = sparse.csr_matrix(record_count)
    _, index = record_count_sp.nonzero()
    
    write_fp.write("########INFORMATION SECTION########\n") 
    for i, value, count in zip(index, record_sp.data, record_count_sp.data):
        write_fp.write("#," + str(i) + "," + str(value) + "," + str(count) + "\n") 
    
    write_fp.close()
    return

# get all data files in source directory
only_files = [f for f in listdir(SOURCE_FOLDER) if isfile(SOURCE_FOLDER + f) and "txt" in (SOURCE_FOLDER + f)]

print("####INFO: start generating")
for file in only_files:
    print("####INFO: start file: " + file)
    for value in [100]:
        increment(file, 0, value, "pos")
        print("####INFO: finish pos increment")
        increment(file, -value, value, "all")
        print("####INFO: finish all increment")
        constant(file, -value, value)
        print("####INFO: finish constant")
        
        print("####INFO: finish range: " + str(value))
    
    print("####INFO: finish file: " + file)
    
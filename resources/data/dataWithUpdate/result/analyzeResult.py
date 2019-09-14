import numpy as np
from os import listdir
from os.path import isfile, join

SOURCE_FOLDER = "./"
SOURCE_FILE = "./constant_max100-zipf_s0.6_len1000_runningResult_error0.01_badprob0.001.txt"
WRITE_FILE = "./output/output.txt"
PERCENTILE_E = 1 - 0.001

def analyze(file, w_fp):
    read_fp = open(file, "r")

    sample, sample_count, conservative_error, default_error, morris_error = [], [], [], [], []
    distinct_count, F1, total_count = 0, 0, 0
    
    # loop all lines, extract data
    for line in read_fp:
        data = line.rstrip().split(',')

        if "sample" in data[0]:
            sample_id = data[0].split(':')[1]
            item_count = int(data[1].split(':')[1])
            correct_result = int(data[2].split(':')[1])
            conservative_result = int(data[3].split(':')[1])
            default_result = int(data[4].split(':')[1])
            morris_result = int(data[5].split(':')[1])
        
            #print(sample_id, item_count, correct_result, conservative_result, default_result, morris_result)
        
            sample.append(sample_id)
            sample_count.append(item_count)
            conservative_error.append(abs(conservative_result - correct_result))
            default_error.append(abs(default_result - correct_result))
            morris_error.append(abs(morris_result - correct_result))
            F1 += correct_result
            total_count += item_count
    
        if "numDistinctItems" in data[0]:
            distinct_count = int(data[0].split(':')[1])
    
    # vectorize function definition
    get_error = np.vectorize(lambda t: t / F1)
    get_weigtht = np.vectorize(lambda t: t / total_count)
    
    # calculate error
    conservative_error = get_error(np.array(conservative_error))
    default_error = get_error(np.array(default_error))
    morris_error = get_error(np.array(morris_error))
    # weighted sample count by total count
    weighted_sample_count = get_weigtht(np.array(sample_count))
    
    # calculate weighted average
    conser_e_weigthed_avg = np.average(conservative_error, weights=weighted_sample_count)
    default_e_weigthed_avg = np.average(default_error, weights=weighted_sample_count)
    morris_e_weigthed_avg = np.average(morris_error, weights=weighted_sample_count)
    
    # calculate worst percentile error
    conser_e_percentile = np.percentile(conservative_error, PERCENTILE_E, interpolation='nearest')
    default_e_percentile = np.percentile(default_error, PERCENTILE_E, interpolation='nearest')
    morris_e_percentile = np.percentile(morris_error, PERCENTILE_E, interpolation='nearest')
    
    # calculate 50 percentile error
    conser_e_median = np.median(conservative_error)
    default_e_median = np.median(default_error)
    morris_e_median = np.median(morris_error)
    
    w_fp.write("mean,")
    w_fp.write("conservative:" + str(np.mean(conservative_error)) + ",default:" + str(np.mean(default_error)) + ",morris:" + str(np.mean(morris_error)) + "\n")
    w_fp.write("weightedMean,")
    w_fp.write("conservative:" + str(conser_e_weigthed_avg) + ",default:" + str(default_e_weigthed_avg) + ",morris:" + str(morris_e_weigthed_avg) + "\n")
    w_fp.write("median,")
    w_fp.write("conservative:" + str(conser_e_median) + ",default:" + str(default_e_median) + ",morris:" + str(morris_e_median) + "\n")
    w_fp.write("percentile,")
    w_fp.write("conservative:" + str(conser_e_percentile) + ",default:" + str(default_e_percentile) + ",morris:" + str(morris_e_percentile) + "\n")
    w_fp.write("uniqueNum," + str(distinct_count) + "\n")

# get all data files in source directory
only_files = [f for f in listdir(SOURCE_FOLDER) if isfile(SOURCE_FOLDER + f) and "txt" in (SOURCE_FOLDER + f)]

fp = open(WRITE_FILE, "a")

print("####INFO: Start analyze")

for file in only_files:
    if "data" in file:
        continue
    
    print("####INFO: analyze file:", file)
    read_file = SOURCE_FOLDER + file
    fp.write("####file,"+file+"\n")
    analyze(file, fp)
    
print("####INFO: Finish analyze")
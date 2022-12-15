import csv
import math
import operator
import os.path
from hanspell import spell_checker
from konlpy.tag import Kkma

# searching .txt files' path
def search(dirname, files):
    try:
        filenames = os.listdir(dirname)
        for filename in filenames:
            full_filename = os.path.join(dirname, filename)
            if os.path.isdir(full_filename):
                search(full_filename, files)
            else:
                ext = os.path.splitext(full_filename)[-1]
                if ext == '.txt':
                    files.append(full_filename)
    except PermissionError:
        pass

# bitter.txt, sour.txt, sweet.txt의 한줄씩 bitter_1.txt, bitter_2.txt에 저장
file_path = []
search('./corpus', file_path)
for i in file_path:
    with open(i, 'r') as f:
        category = i.split('/')
        category = (category[-1].split('.'))[0]
        cnt = 0
        while True:
            line = f.readline()
            if not line:
                break

            if line[0] == '[' and (line[1] == '\'' or line[1] == '\"'):
                line = line[3:len(line)-3]
            unnecessary = line.split(' ')
            if unnecessary[-1] == '...':
                line = line[:len(line)-4]
            if unnecessary[-1] == '...\n':
                line = line[:len(line) - 5]

            cnt += 1
            new_path = './corpus/category/' + category + '/' + category + '_' + str(cnt) + '.txt'
            with open(new_path, 'w', encoding='utf8') as nf:
                nf.write(line)

    print('>> {} len: {}'.format(category, cnt))

# 형태소 분석
kkma = Kkma()
def morph(input):
    preprocessed = kkma.pos(input)
    return preprocessed

# input_path: bitter_1.txt ~ sweet_863.txt (총 2642개)
input_path = []
search('./corpus/category', input_path)
c = 0
for i in input_path:
    print(i)
    c += 1
    with open(i, 'r') as f:
        while True:
            line = f.readline()
            if not line:
                break

            morph_analy = morph(line)
            idx = 0
            morph_line = ''
            for j in morph_analy:
                morph_line = morph_line + '+' + (morph_analy[idx])[0] + '/' + (morph_analy[idx])[1]
                idx += 1
            morph_line = morph_line[1:]
            print(morph_line)

            new_path = i.split('/')
            new_path = new_path[len(new_path)-2:]
            new_path = './preprocessed/' + new_path[0] + '/' + new_path[1]
            print(new_path)
            print()
            with open(new_path, 'w', encoding='utf8') as wf:
                wf.write(morph_line)

def freq_top_1000(file_dirs):
    word = {}
    for file in file_dirs:
        wordList = []
        with open(file, 'r') as f:
            while True:
                line = f.readline()
                if not line:
                    break

                morpheme = line.split('+')
                for m in morpheme:
                    if 'MAG' in m:
                        wordList.append(m)
                    if 'XR' in m:
                        wordList.append(m)
                    if 'MDT' in m:
                        wordList.append(m)

        for i in wordList:
            try:
                word[i] += 1
            except:
                word[i] = 1

    top_1000 = sorted(word.items())
    top_1000 = sorted(top_1000, key=operator.itemgetter(1), reverse=True)
    top_1000 = top_1000[0:1000]
    # top_1000 = sorted(top_1000)

    return top_1000

def calculate_TF(file_dir, feature_set):
    tf = [0] * len(feature_set)
    with open(file_dir, 'r') as f:
        while True:
            line = f.readline()
            if not line:
                break

            m = line.split('+')
            for key in m:
                if key in feature_set:
                    tf[feature_set.index(key)] += 1

    for i in range(len(tf)):
        tf[i] = math.log10(tf[i] + 1)

    return tf

def calculate_IDF(file_dirs, feature_set):
    docNum = len(file_dirs)
    print(docNum)

    IDF_table = [0] * len(feature_set)
    df = [0] * len(feature_set)

    for i in file_dirs:
        v = [0] * len(feature_set)
        with open(i, 'r') as f:
            while True:
                line = f.readline()
                if not line:
                    break

                m = line.split('+')
                for key in m:
                    for j in range(len(feature_set)):
                        if feature_set[j] == key and v[j] == 0:
                            df[j] += 1
                            v[j] = 1

    for i in range(len(df)):
        IDF_table[i] = math.log10(docNum / (df[i]+1))

    return IDF_table

def TFIDF_norm(feature_set, TF, IDF):
    TFIDF = [0] * len(feature_set)
    sum = 0
    for i in feature_set:
        sum += (TF[feature_set.index(i)] * IDF[feature_set.index(i)])**2
    for i in feature_set:
        if sum == 0:
            sum = 0.00000000000000000000000001
        TFIDF[feature_set.index(i)] = (TF[feature_set.index(i)] * IDF[feature_set.index(i)]) / math.sqrt(sum)
    return TFIDF

trainData_path = []
search('./Data/TrainData', trainData_path)
testData_path = []
search('./Data/TestData', testData_path)

feature_set = freq_top_1000(trainData_path)
print("\n>> feature set")
print(feature_set)
print(len(feature_set))
print()

feature_set = dict(feature_set)
feature_set = list(feature_set.keys())
print(feature_set)
print(len(feature_set))
print(type(feature_set))

print("\n>> IDF table")
IDF_table = calculate_IDF(trainData_path, feature_set)
print(IDF_table)
print(len(IDF_table))
print(type(IDF_table))

train_txt = "all_train_features.txt"
test_txt = "all_test_features.txt"

# TrainData, TestData 각각에서 TF를 구하고
# TrainData에서 구한 IDF table을 이용해서
# TrainData, TestData 각각에서 TF_IDF를 구해 -> 'TF_IDF' 폴더에 저장
print("\n>> calculate TF_IDF for all paths and save")
all_paths = trainData_path + testData_path
for i in all_paths:
    num = i.split('/')

    TF = calculate_TF(i, feature_set)
    TF_IDF = TFIDF_norm(feature_set, TF, IDF_table)

    if 'bitter' in num[-1]:
        TF_IDF.append(0)
    if 'sour' in num[-1]:
        TF_IDF.append(1)
    if 'sweet' in num[-1]:
        TF_IDF.append(2)

    TF_IDF = list(map(str, TF_IDF))

    category = (num[-1].split('_'))[0]
    new_path = './TF_IDF/' + num[2] + '/' + category + '/' + num[-1]
    print(new_path)
    with open(new_path, 'w', encoding='utf8') as f:
        for value in TF_IDF:
            f.write(value + '\t')

    if num[2] == 'TrainData':
        with open(train_txt, 'a', encoding='utf8') as trainF:
            for v in TF_IDF:
                trainF.write(v + '\t')
            trainF.write('\n')
    if num[2] == 'TestData':
        with open(test_txt, 'a', encoding='utf8') as testF:
            for v in TF_IDF:
                testF.write(v + '\t')
            testF.write('\n')
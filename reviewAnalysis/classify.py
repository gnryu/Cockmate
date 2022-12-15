import pandas as pd
import numpy as np
from sklearn import svm
from sklearn.calibration import CalibratedClassifierCV
from sklearn.model_selection import GridSearchCV
from sklearn.metrics import confusion_matrix
from sklearn import metrics
from sklearn.naive_bayes import GaussianNB
from sklearn import tree
from sklearn.neighbors import KNeighborsClassifier

columns_idx = np.arange(0, 570)

'''
Train Set
    bitter: 779
    sour: 930
    sweet: 828
'''
train = pd.read_csv('./all_train_features.txt', sep='\t', names=columns_idx)
train = train.drop([569], axis=1)
X_train = train.iloc[:, :568]
y_train = train[568]

# Test set
test = pd.read_csv('./all_test_features.txt', sep='\t', names=columns_idx)
test = test.drop([569], axis=1)
X_test = test.iloc[:, :568]
y_test = test[568]

'''
Navie Bayes
'''
# gnb = GaussianNB()
# gnb.fit(X_train, y_train)
#
# y_pred = gnb.predict(X_test)

'''
K-nearest neighbors
'''
# classifier = KNeighborsClassifier(n_neighbors=3)
# classifier.fit(X_train, y_train)
#
# y_pred = classifier.predict(X_test)

'''
SVM
'''
svm_clf = svm.SVC(probability=True)
# svm_clf.fit(X_train, y_train)
# y_pred = svm_clf.predict(X_test)
param_dict = {
    'kernel': ['linear', 'rbf', 'poly'],
    'C': [0.1, 1, 10],
    'gamma': [0.1, 1, 10]
}
# gridsearch best param
# C:1, gamma:1, kernel: rbf

gs = GridSearchCV(svm_clf, param_grid=param_dict, cv=5)
gs.fit(X_train, y_train)

print(">> best params: ", gs.best_params_)
print(">> best score: ", gs.best_score_)

svm_gs = gs.best_estimator_

# y_proba = svm_gs.predict_proba(X_test)
# print("\n>> y probability")
# print(y_proba)
y_pred = svm_gs.predict(X_test)

print("\n>> confusion matrix")
print(confusion_matrix(y_test, y_pred))

print("\n>> precision, recall, F1-score")
print(metrics.classification_report(y_test, y_pred))

# print("\n>> y predict")
# print(y_pred)


'''
Decision Tree
'''
# dt_clf = tree.DecisionTreeClassifier(random_state=42)

'''
Grid Search 없이
'''
# dt_clf = dt_clf.fit(X_train, y_train)

# print("\n>> y probability")
# y_proba = dt_clf.predict_proba(X_test)
# print(y_proba)

# print("\n>> y predict")
# y_pred = dt_clf.predict(X_test)
# print(y_pred)

'''
Grid Search CV
'''
# param_dict = {
#     'criterion': ['gini', 'entropy'],
#     'max_depth': range(1,10),
#     'min_samples_split': range(1,10),
#     'min_samples_leaf': range(1,5)
# }
#
# gs = GridSearchCV(dt_clf, param_grid=param_dict, cv=5)
# gs.fit(X_train, y_train)
#
# print(">> best params: ", gs.best_params_)
# print(">> best score: ", gs.best_score_)
#
# dt_gs = gs.best_estimator_
#
# print(">> y probability")
# y_proba = dt_gs.predict_proba(X_test)
# print(y_proba)
#
# # print("\n>> y predict")
# y_pred = dt_gs.predict(X_test)
# # print(y_pred)

# print("\n>> confusion matrix")
# print(confusion_matrix(y_test, y_pred))
#
# print("\n>> precision, recall, F1-score")
# print(metrics.classification_report(y_test, y_pred))

# print("-------------------------------------------------------")
# print(f'type of y_prob: {type(y_proba)}')
# print(y_proba[0])
# print(len(y_proba[0]))
# for i in y_proba[0]:
#     print(i)

# df = pd.DataFrame(y_proba, columns=['bitter', 'sour', 'sweet'])
# print(df)
# print()
#
# label = pd.Series(y_test, name='label')
# pred = pd.Series(y_pred, name='pred')
# pd.set_option('display.max_rows', None)
# df = pd.concat([df, label, pred], axis=1)
# print(df)
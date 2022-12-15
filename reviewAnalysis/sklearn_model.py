import pandas as pd
import numpy as np
from sklearn import svm
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import DoubleTensorType

columns_idx = np.arange(0, 570)

train = pd.read_csv('all_train_features.txt', sep='\t', names=columns_idx)
train = train.drop([569], axis=1)
X_train = train.iloc[:, :568]
y_train = train[568]

X_arr = X_train.to_numpy()

svm_clf = svm.SVC(C=1, kernel='rbf', gamma=1, probability=True)
svm_clf.fit(X_train, y_train)

initial_type = [
    ('input_TFIDF_list', DoubleTensorType((None, X_arr.shape[1])))
]

convert_model = convert_sklearn(svm_clf, initial_types=initial_type, options={type(svm_clf): {'zipmap': False}})

with open("sklearn_model.onnx", "wb") as f:
    f.write(convert_model.SerializeToString())


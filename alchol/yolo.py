import cv2
import numpy as np
import os
from unicodedata import normalize
net = cv2.dnn.readNet("yolov3.weights", "yolov3.cfg")
classes = []
with open("coco.names", "r") as f:
    classes = [line.strip() for line in f.readlines()]
layer_names = net.getLayerNames()
output_layers = [layer_names[i[0] - 1] for i in net.getUnconnectedOutLayers()]
colors = np.random.uniform(0, 255, size=(len(classes), 3))

def imwrite(filename, img, params=None):
    try:
        ext = os.path.splitext(filename)[1]
        result, n = cv2.imencode(ext, img, params)

        if result:
            with open(filename, mode='w+b') as f:
                n.tofile(f)
            return True
        else:
            return False
    except Exception as e:
        print(e)
        return False
# 이미지 가져오기
folder_list = os.listdir('data/testing')
print(folder_list)
for foldername in folder_list:
    folder_root = 'data/testing/'+foldername
    file_list = os.listdir(folder_root)
    print(file_list)
    for filename in file_list:
        file_root = folder_root +'/' + filename

        img_array = np.fromfile(file_root, np.uint8)
        img = cv2.imdecode(img_array, cv2.IMREAD_COLOR)
        try:
            img = cv2.resize(img, None, fx=1, fy=1)
        except:
            continue
        height, width, channels = img.shape
        # Detecting objects
        blob = cv2.dnn.blobFromImage(img, 0.00392, (416, 416), (0, 0, 0), True, crop=False)
        net.setInput(blob)
        outs = net.forward(output_layers)


        # 정보를 화면에 표시
        class_ids = []
        confidences = []
        boxes = []
        for out in outs:
            for detection in out:
                scores = detection[5:]
                class_id = np.argmax(scores)
                confidence = scores[class_id]
                if confidence > 0.4:
                    # Object detected
                    center_x = int(detection[0] * width)
                    center_y = int(detection[1] * height)
                    w = int(detection[2] * width)
                    h = int(detection[3] * height)
                    # 좌표
                    x = int(center_x - w / 2)
                    y = int(center_y - h / 2)
                    boxes.append([x, y, w, h])
                    confidences.append(float(confidence))
                    class_ids.append(class_id)

        indexes = cv2.dnn.NMSBoxes(boxes, confidences, 0.5, 0.4)

        font = cv2.FONT_HERSHEY_PLAIN

        for i in range(len(boxes)):
            if i in indexes:
                x, y, w, h = boxes[i]
                label = str(classes[class_ids[i]])
                if(label == 'bottle'):
                    name_string = filename.split('.')
                    save_root = 'data/testing_cropped/' + foldername + '/' + name_string[0]+'_'+str(i)+'.'+name_string[1]
                    nfc =normalize("NFC",save_root)
                    os.makedirs('data/testing_cropped/' + foldername,exist_ok=True)
                    print(save_root)
                    color = colors[i]
                    #cv2.rectangle(img, (x, y), (x + w, y + h), color, 2)
                    #cv2.putText(img, label, (x, y + 30), font, 1.5, color, 3)
                    crop_image = img[y: y + h, x:x + w]
                    print(x)
                    print(y)
                    print(w)
                    print(h)

                    imwrite(save_root,crop_image,params=None)




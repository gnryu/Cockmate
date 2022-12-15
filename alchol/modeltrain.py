import torchvision
from torchvision import datasets, models, transforms
import torch
import numpy as np
import matplotlib.pyplot as plt
from PIL import ImageFile
import torch.nn as nn
import torch.optim as optim
from torch.optim import lr_scheduler
import numpy as np
import time
import os
import copy

device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
ImageFile.LOAD_TRUNCATED_IMAGES = True

def test_eval(model, data_iter, batch_size):
    with torch.no_grad():
        test_loss = 0
        total = 0
        correct = 0
        model.eval()
        for batch_img, batch_lab in data_iter:
            X = batch_img.to(device, non_blocking=True)
            Y = batch_lab.to(device, non_blocking=True)
            y_pred = model(X)
            _, predicted = torch.max(y_pred.data, 1)
            correct += (predicted == Y).sum().item()
            total += batch_img.size(0)
        val_acc = (100 * correct / total)
        model.train()
    return val_acc

def run():
    torch.multiprocessing.freeze_support()
    train_path = 'data/training'
    test_path = 'data/testing'



    data_transforms = transforms.Compose([
    transforms.Resize((224,224)),
    transforms.ToTensor(),
    transforms.Normalize([0.5, 0.5, 0.5], [0.5, 0.5, 0.5])
    ])

    train_dataset = datasets.ImageFolder(train_path,data_transforms)
    test_dataset = datasets.ImageFolder(test_path,data_transforms)


    print(train_dataset)
    print(test_dataset)
    class_names = train_dataset.classes # 클래스 출력
    #train_size = int(0.7 * len(image_datasets))
    #test_size = len(image_datasets) - train_size
    #train_dataset, test_dataset = torch.utils.data.random_split(image_datasets, [train_size, test_size])


    train_loader = torch.utils.data.DataLoader(train_dataset, batch_size=32,shuffle = True, num_workers = 8,pin_memory=True)

    validation_loader = torch.utils.data.DataLoader(test_dataset, batch_size=32,shuffle = False,pin_memory=True)

    pretrained_model = models.efficientnet_b3(pretrained=True)
    num_ftrs = pretrained_model.classifier[1].in_features
    pretrained_model.classifier[1] = nn.Linear(num_ftrs, 415)
    pretrained_model = pretrained_model.to(device)
    criterion = nn.CrossEntropyLoss().to(device)
    optimizer = optim.Adam(pretrained_model.parameters(), lr=0.0001)
    #model = torch.load('7_3.pth')
    #model.to('cpu')
    #model.eval()
    #input_tensor = torch.rand(1,3,224,224).to('cpu')
    #print(model)
    #from torch.utils.mobile_optimizer import optimize_for_mobile
    #script_model = torch.jit.trace(model,input_tensor)
    #opt = optimize_for_mobile(script_model)
    #opt._save_for_lite_interpreter("efc.ptl")
    #save model for pytorchlite

    EPOCHS = 10
    print_every=1
    start_time = time.time()
    for epoch in range(200):
        best_accuracy = 0
        loss_val_sum = 0
        batch_iter=0
        i = 1
        end = time.time()
        for batch_img, batch_lab in train_loader:
            X = batch_img.to(device,non_blocking=True)
            Y = batch_lab.to(device,non_blocking=True)
            batch_iter += 1
            # Inference & Calculate loss
            y_pred = pretrained_model.forward(X)
            loss = criterion(y_pred, Y)

            optimizer.zero_grad()
            loss.backward()
            optimizer.step()
            loss_val_sum += loss
            if i % 100 == 0:
                print("Train Step : {}\tLoss : {:3f}".format(i, loss.item()))
            i += 1

        if ((epoch % print_every) == 0) or (epoch == (EPOCHS - 1)):
            # accr_val = M.test(x_test, y_test, batch_size)
            loss_val_avg = loss_val_sum / len(train_loader)
            accr_val = test_eval(pretrained_model, validation_loader,64)
            print(f"epoch:[{epoch + 1}/{EPOCHS}] cost:[{loss_val_avg:.3f}] test_accuracy:[{accr_val:.3f}]")
            print("Time: {:.4f}sec".format((time.time() - start_time)))
            torch.save(pretrained_model, f"{epoch + 1}_3.pth")

        #if accr_val > best_accuracy:
            #torch.save(pretrained_model,f"{epoch + 1}.pth")
        best_accuracy = accr_val

if __name__ == '__main__':
    run()

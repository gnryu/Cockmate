import os.path
import os
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from webdriver_manager.chrome import ChromeDriverManager
from bs4 import BeautifulSoup
from selenium.webdriver.chrome.service import Service
import time
import random
import pandas as pd
import datetime
import urllib.request
from multiprocessing import Pool


def doScrollDown(whileSeconds):
    start = datetime.datetime.now()
    end = start + datetime.timedelta(seconds=whileSeconds)
    while True:
        driver.execute_script('window.scrollTo(0, document.body.scrollHeight);')
        time.sleep(1)
        if datetime.datetime.now() > end:
            break


def crawler(root):
    file_list = os.listdir(root) #폴더 넣어놓는 곧 경로
    print(file_list)
    return file_list
def get_image(name):
    print(name)
    chrome_options = webdriver.ChromeOptions()
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)  # 크롬드라이버 설치한 경로 작성 필요
    driver.get("https://www.google.co.kr/imghp?hl=ko&tab=wi&authuser=0&ogbl")  # 구글 이미지 검색 url
    elem = driver.find_element(By.NAME, "q")  # 구글 검색창 선택
    elem.send_keys(name)  # 검색창에 검색할 내용(name)넣기 << 원하면 여기에 name + "후기" 이런식으로
    elem.send_keys(Keys.RETURN)  # 검색할 내용을 넣고 enter를 치는것!

    imgs = driver.find_elements(By.CSS_SELECTOR,".rg_i.Q4LuWd")
    count = 0
    for img in imgs:
        try:
            img.click()
            time.sleep(1)
            Xpath = '//*[@id="Sva75c"]/div/div/div/div[3]/div[2]/c-wiz/div[2]/div[1]/div[1]/div[2]/div/a/img'
            print(Xpath)

            imgUrl = driver.find_element(By.XPATH,Xpath).get_attribute('src')  # 크게 뜬 이미지 선택하여 "src" 속성을 받아옴
            print(imgUrl)
            path = 'data\\2\\' + name + '/'  # 저장할 경로
            print(path + name + str(count) + ".jpg")
            urllib.request.urlretrieve(imgUrl,path + name +"_"+ str(count) + ".jpg")
            count = count + 1
            print(count)
            if count > 20:  # 다운 받을 이미지 갯수 조정
                break
        except:
            pass
if __name__=='__main__':

    pool = Pool(processes=4)
    pool.map(get_image ,crawler('2'))

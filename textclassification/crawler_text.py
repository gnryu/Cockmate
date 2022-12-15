
from urllib.parse import quote_plus
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By
f= open('bitter.txt','a',encoding='UTF-8') # 저장할 txt 이름
baseUrl = 'https://www.google.com/search?q='
plusUrl = input('검색어를 입력하세요: ')
url = baseUrl + quote_plus(plusUrl)

chrome_options = webdriver.ChromeOptions()
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

# chromedriver path input
driver.get(url)
driver.implicitly_wait(3)
crit = -1
for j in range(30):
    news_titles = driver.find_elements(By.CLASS_NAME,"VwiC3b.yXK7lf.MUxGbd.yDYNvb.lyLwlc.lEBKkf")
    print(news_titles)
    for i in news_titles:
        title = i.text
        if '—' in title:
            title = title.split('—')
            print(title[1:])
            f.write(str(title[1:]))
            f.write('\n')
        else:
            f.write(str(title))
            f.write('\n')
            print(title)
    if(crit == -1):
        xpath= '//*[@id="botstuff"]/div/div[2]/table/tbody/tr/td[' + str(j+3) +  ']/a'
    else:
        xpath = '//*[@id="botstuff"]/div/div[2]/table/tbody/tr/td[' + str(crit) + ']/a'
    try:
        btn = driver.find_element(By.XPATH,xpath)
        btn.click()
    except:
        if (crit == -1):
            show_all = '//*[@id="ofr"]/i/a'
            btn = driver.find_element(By.XPATH, show_all)
            btn.click()
            btn = driver.find_element(By.XPATH, xpath)
            btn.click()
            crit = int(j/2) + 4
            print(crit)
        else:
            break


print('done')


driver.close()
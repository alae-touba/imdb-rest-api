import json
import requests
import bs4
import re

url = "https://www.imdb.com/chart/top/?ref_=nv_mv_250"

res = requests.get(url)
res.raise_for_status()
html = bs4.BeautifulSoup(res.text, features="html.parser")

list_links = html.select(
    "#main > div > span > div > div > div.lister > table > tbody > tr > td.titleColumn > a")

urls = []

for i, v in enumerate(list_links):
    urls.append({"url": "https://www.imdb.com" + v.get("href")})


data_file = open("movies-urls1.json", "w")
data_file.write(json.dumps(urls))


# print(urls)
# print(list_links)

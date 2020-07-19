import json
import requests
import bs4
import re

url = "https://www.imdb.com/chart/toptv/?ref_=nv_tvv_250"

res = requests.get(url)
res.raise_for_status()
html = bs4.BeautifulSoup(res.text, features="html.parser")

shows_links = html.select(
    "#main > div > span > div > div > div.lister > table > tbody > tr > td.titleColumn > a")

urls = []
for i, v in enumerate(shows_links):
    urls.append({"url": "https://www.imdb.com" + v.get("href")})

shows_urls = open("./tv-shows-urls.json", "w")
shows_urls.write(json.dumps(urls))

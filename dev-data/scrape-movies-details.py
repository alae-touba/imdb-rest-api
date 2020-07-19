import json
import requests
import bs4
import re

f = open("./movies-urls1.json", 'r')
movies_urls = f.read()
movies_urls = json.loads(movies_urls)

movies_details = []

movie_id = 1
for movie in movies_urls:

    movie_detail = {"id": movie_id}
    movie_id += 1

    res = requests.get(movie["url"])
    res.raise_for_status()
    html = bs4.BeautifulSoup(res.text, features="html.parser")

    title = html.select("div.title_wrapper > h1")
    title = title[0].getText().strip()
    title = re.sub("\([0-9]{4}\)", "", title)
    movie_detail["title"] = title.strip()

    summary = html.select("div.plot_summary > div.summary_text")
    summary = summary[0].getText().strip()
    movie_detail["summary"] = summary

    release_year = html.select("#titleYear > a")
    release_year = release_year[0].getText()
    print("release year", release_year)
    movie_detail["releaseYear"] = int(release_year)

    ratingValue = html.select(
        "#title-overview-widget > div.vital > div.title_block > div > div.ratings_wrapper > div.imdbRating > div.ratingValue > strong > span")
    ratingValue = ratingValue[0].getText().strip()
    movie_detail["ratingValue"] = float(ratingValue)

    rating_count = html.select(
        "#title-overview-widget > div.vital > div.title_block > div > div.ratings_wrapper > div.imdbRating > a > span")
    rating_count = rating_count[0].getText().strip()
    rating_count = re.sub(",", "", rating_count)
    movie_detail['ratingCount'] = int(rating_count)

    genres = html.select(
        "#title-overview-widget > div.vital > div.title_block > div > div.titleBar > div.title_wrapper > div  a")
    genres.pop()

    for i, v in enumerate(genres):
        genres[i] = v.getText().strip()

    movie_detail["genres"] = genres

    stars = html.select("div.plot_summary_wrapper > div.plot_summary > div:nth-child(4) a")
    stars.pop()
    for i, v in enumerate(stars):
        stars[i] = v.getText().strip()
    movie_detail["stars"] = stars

    try:
        runtime = html.select("#titleDetails > div > time")
        runtime = runtime[0].getText().strip()
        runtime = re.sub("[a-zA-Z]", "", runtime).strip()
        movie_detail["runtime"] = int(runtime)
        # print("runtime:", runtime)
    except IndexError:
        movie_detail["runtime"] = -1

    director = html.select(
        "div.plot_summary_wrapper > div.plot_summary > div:nth-child(2) > a")[0].getText().strip()
    movie_detail["director"] = director

    languages = html.select("#titleDetails > div:nth-child(5) a")
    for i, v in enumerate(languages):
        languages[i] = v.getText().strip()

    if(len(languages) == 1 and languages[0] == "See more"):
        movie_detail['languages'] = []
    else:
        movie_detail['languages'] = languages

    movies_details.append(movie_detail)


data_file = open("./movies-details.json", "w")
data_file.write(json.dumps(movies_details))

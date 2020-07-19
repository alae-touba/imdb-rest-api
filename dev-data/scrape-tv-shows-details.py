import json
import requests
import bs4
import re

f = open("./tv-shows-urls.json", 'r')
shows_urls = f.read()
shows_urls = json.loads(shows_urls)

shows_details = []

show_id = 1
for show in shows_urls:
    url = show["url"]
    show_detail = {}

    show_detail["id"] = show_id
    show_id += 1

    res = requests.get(url)
    res.raise_for_status()
    html = bs4.BeautifulSoup(res.text, features="html.parser")

    title = html.select_one(
        "#title-overview-widget > div.vital > div.title_block > div > div.titleBar > div.title_wrapper > h1")
    title = title.text.strip()
    show_detail["title"] = title

    summary = html.select_one("div.plot_summary > div.summary_text")
    summary = summary.getText().strip()
    show_detail["summary"] = summary
    # print(summary)

    release_year = html.select(
        "div.title_bar_wrapper > div.titleBar > div.title_wrapper > div.subtext > a")
    release_year = release_year[-1].text.strip()
    release_year = re.search("[0-9]{4}", release_year).group(0)
    show_detail["release year"] = int(release_year)
    # print(release_year)

    rating_value = html.select_one(
        "div.ratingValue span[itemprop='ratingValue']")
    rating_value = rating_value.text.strip()
    show_detail["ratingValue"] = float(rating_value)

    rating_count = html.select_one("div.imdbRating > a > span.small ")
    rating_count = rating_count.getText().strip()
    rating_count = re.sub(",", "", rating_count)
    show_detail['ratingCount'] = int(rating_count)
    # print(rating_count)

    number_of_episodes = html.select_one(
        "div[class='button_panel navigation_panel'] > a > div.bp_content > div.bp_description > span.bp_sub_heading")
    number_of_episodes = number_of_episodes.getText().strip()
    number_of_episodes = re.sub(" episodes", "", number_of_episodes)
    show_detail['episodes'] = int(number_of_episodes)
    # print(number_of_episodes)

    try:

        genres = html.select(
            "div.titleBar > div.title_wrapper > div.subtext > a")
        genres.pop()

        for i, v in enumerate(genres):
            genres[i] = v.getText().strip()

        # print(genres)
    except IndexError:
        genres = []
    show_detail["genres"] = genres

    try:
        stars = html.select("div.plot_summary > div:nth-child(3) > a")
        stars.pop()

        for i, v in enumerate(stars):
            stars[i] = v.text
        show_detail["stars"] = stars
        # print(stars)
    except IndexError:
        stars = html.select("div.plot_summary > div:nth-child(2) > a")
        stars.pop()

        for i, v in enumerate(stars):
            stars[i] = v.text
        show_detail["stars"] = stars

    creator = html.select_one(
        "div.plot_summary > div.credit_summary_item > a").getText().strip()
    show_detail["creator"] = creator
    # print(creator)

    languages = html.select("#titleDetails > div:nth-child(5) > a")
    for i, v in enumerate(languages):
        languages[i] = v.text.strip()
    show_detail["languages"] = languages
    # print(languages)

    countries = html.select('#titleDetails > div:nth-child(4) > a')
    for i, v in enumerate(countries):
        countries[i] = v.text.strip()

    show_detail["countries"] = countries
    # print(countries)

    print(show_detail)
    shows_details.append(show_detail)

data_file = open("./tv-shows-details.json", "w")
data_file.write(json.dumps(shows_details))

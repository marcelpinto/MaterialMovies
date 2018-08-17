package com.hardsoftstudio.androidx.movies.test

val upcomingResponse = """
{
  "page": 1,
  "results": [
    {
      "poster_path": "/e1mjopzAS2KNsvpbpahQ1a6SkSn.jpg",
      "adult": false,
      "overview": "From DC Comics comes the Suicide Squad, an antihero team of incarcerated supervillains who act as deniable assets for the United States government, undertaking high-risk black ops missions in exchange for commuted prison sentences.",
      "release_date": "2016-08-03",
      "genre_ids": [
        14,
        28,
        80
      ],
      "id": 297761,
      "original_title": "Suicide Squad",
      "original_language": "en",
      "title": "Suicide Squad",
      "backdrop_path": "/ndlQ2Cuc3cjTL7lTynw6I4boP4S.jpg",
      "popularity": 48.261451,
      "vote_count": 1466,
      "video": false,
      "vote_average": 5.91
    },
    {
      "poster_path": "/lFSSLTlFozwpaGlO31OoUeirBgQ.jpg",
      "adult": false,
      "overview": "The most dangerous former operative of the CIA is drawn out of hiding to uncover hidden truths about his past.",
      "release_date": "2016-07-27",
      "genre_ids": [
        28,
        53
      ],
      "id": 324668,
      "original_title": "Jason Bourne",
      "original_language": "en",
      "title": "Jason Bourne",
      "backdrop_path": "/AoT2YrJUJlg5vKE3iMOLvHlTd3m.jpg",
      "popularity": 30.690177,
      "vote_count": 649,
      "video": false,
      "vote_average": 5.25
    }
  ],
  "dates": {
    "maximum": "2016-09-01",
    "minimum": "2016-07-21"
  },
  "total_pages": 33,
  "total_results": 649
}
""".trimIndent()

val year2017Response = """
{
  "page": 1,
  "results": [
    {
      "poster_path": null,
      "adult": false,
      "overview": "Go behind the scenes during One Directions sell out \"Take Me Home\" tour and experience life on the road.",
      "release_date": "2013-08-30",
      "genre_ids": [
        99,
        10402
      ],
      "id": 164558,
      "original_title": "One Direction: This Is Us",
      "original_language": "en",
      "title": "One Direction: This Is Us",
      "backdrop_path": null,
      "popularity": 1.166982,
      "vote_count": 55,
      "video": false,
      "vote_average": 8.45
    },
    {
      "poster_path": null,
      "adult": false,
      "overview": "",
      "release_date": "1954-06-22",
      "genre_ids": [
        80,
        18
      ],
      "id": 654,
      "original_title": "On the Waterfront",
      "original_language": "en",
      "title": "On the Waterfront",
      "backdrop_path": null,
      "popularity": 1.07031,
      "vote_count": 51,
      "video": false,
      "vote_average": 8.19
    }
  ],
  "total_results": 61,
  "total_pages": 4
}
""".trimIndent()

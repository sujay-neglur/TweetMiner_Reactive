$ ->
  searchTerms=[]
  counter = 0
  console.log 'Index js file'
  $('#getTweets').click ->
    if $('#searchBar').val().length == 0
      alert 'Please enter the search keyword'
      return
    else
      $.get '/update/' + $('#searchBar').val(), (event) ->
#        console.log event
        return
    return
  ws = new WebSocket($('body').data('ws-url'))

  ws.onmessage = (ev) ->
    jsob=JSON.parse(event.data)
    jsonTweetObjects= JSON.parse(jsob.tweet);
#    console.log jsonTweetObjects
    searchTopic= jsob.topic
    newsearctopic= searchTopic
    if ' ' in searchTopic
      newsearctopic=searchTopic.replace(RegExp(' '), '_')

    searchTermsLength= jsonTweetObjects.length
    i=0
    #$('#tweetDiv').empty()
    if searchTopic not in searchTerms
#      console.log searchTopic + ' '+ jsonTweetObjects.length
      searchTerms=searchTerms.concat searchTopic
      html = document.createElement("div")
      counter++
      html.id = newsearctopic #'innerTweet'+counter
      html.className = searchTopic #'container'+counter
      #html.title = searchTopic
      html.tagName = searchTopic
      sentiment = ""
      $.get

        url: 'analysis/'+searchTopic
        success: (data) ->
          sentiment = data
          return
        async: false
      html.appendChild document.createTextNode('Search Results for  '+ searchTopic + ' '+sentiment)

      a = document.createElement('a')
      statsText = document.createTextNode(' Statistics')
      a.appendChild statsText
      a.title = 'Statistics'
      a.href = 'statistics/'+searchTopic
      br = document.createElement('br')
      html.appendChild br
      html.appendChild a
      #refCode= '<a href="statistics/'+searchTopic+'">'+'Statistics'+'</a>'
      document.getElementById('tweetDiv').appendChild html

      allCards = document.createElement('div')
      allCards.id = "allCards"+newsearctopic
      allCards.className = "allCards"
      document.getElementById(newsearctopic).appendChild allCards

      while i<searchTermsLength
        screenName = jsonTweetObjects[i].screenName
        console.log screenName
        name = jsonTweetObjects[i].username
        tweet = jsonTweetObjects[i].tweet
        location = jsonTweetObjects[i].location
        latitude = jsonTweetObjects[i].latitude
        longtitude = jsonTweetObjects[i].longitude
        hashtags = jsonTweetObjects[i].hashtagEntities
        updatedTopic=jsonTweetObjects[i].topic
        if ' ' in updatedTopic
          updatedTopic = updatedTopic.replace(RegExp(' '), '_')
          console.log updatedTopic
        console.log updatedTopic+' '+tweet
        htmlCode = '<div class="card">' +
          '<a id="linkToDisplay" href="displayTweets/' + screenName + '">' + screenName + '</a>' +
          '<h4 class="card-title" id="userName">' + name +
          '</h4>' + '<a href="location/' + latitude + '/' +
          longtitude + '/' + location + '">' + location +
          '</a>' + '<p class="card-text" id="tweet">' +
          tweet + '</p>'
        j = 0
        while j < hashtags.length
          hashtagString = '<a href="hashtag/' + hashtags[j].text + '">' + hashtags[j].text + '</a>'
          htmlCode += hashtagString
          j++
        htmlCode += '</div>'
        ###html = document.getElementById(searchTopic)
        console.log html###

        $('#allCards'+updatedTopic).append htmlCode
        console.log document.getElementById("allCards"+updatedTopic)
#        console.log document.getElementById('tweetDiv')
        #$('#tweetDiv').append html
        i++
    else
      document.getElementById("allCards"+newsearctopic).innerHTML = "";
      sentiment = ""
      $.get

        url: 'analysis/'+searchTopic
        success: (data) ->
          sentiment = data
          return
        async: false
      #document.getElementById(searchTopic).appendChild document.createTextNode('Search Results for  '+ searchTopic + ' '+sentiment)

#      a = document.createElement('a')
#      statsText = document.createTextNode(' Statistics')
#      a.appendChild statsText
#      a.title = 'Statistics'
#      a.href = 'statistics/'+searchTopic
#      br = document.createElement('br')
#      document.getElementById(searchTopic).appendChild br
#      document.getElementById(searchTopic).appendChild a
#      document.getElementById('tweetDiv').appendChild document.getElementById(searchTopic)
#
#      allCards = document.createElement('div')
#      allCards.id = "allCards"+searchTopic
#      allCards.className = "allCards"
#      document.getElementById(searchTopic).appendChild allCards

      while i<searchTermsLength
        screenName = jsonTweetObjects[i].screenName
        name = jsonTweetObjects[i].username
        tweet = jsonTweetObjects[i].tweet
        location = jsonTweetObjects[i].location
        latitude = jsonTweetObjects[i].latitude
        longtitude = jsonTweetObjects[i].longitude
        hashtags = jsonTweetObjects[i].hashtagEntities
        updatedSearchTopic= jsonTweetObjects[i].topic
        if ' ' in updatedSearchTopic
          updatedSearchTopic=updatedSearchTopic.replace(RegExp(' '),'_')
        htmlCode = '<div class="card">' +
          '<a id="linkToDisplay" href="displayTweets/' + screenName + '">' + screenName + '</a>' +
          '<h4 class="card-title" id="userName">' + name +
          '</h4>' + '<a href="location/' + latitude + '/' +
          longtitude + '/' + location + '">' + location +
          '</a>' + '<p class="card-text" id="tweet">' +
          tweet + '</p>'
        j = 0
        while j < hashtags.length
          hashtagString = '<a href="hashtag/' + hashtags[j].text + '">' + hashtags[j].text + '</a>'
          htmlCode += hashtagString
          j++
        htmlCode += '</div>'
        $('#allCards'+updatedSearchTopic).append htmlCode
        i++

  return


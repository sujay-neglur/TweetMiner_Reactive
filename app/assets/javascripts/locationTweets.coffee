$(document).ready ->
  $.get $('#locationTweets').val(), (data) ->
    console.log 'Inside locationTweets'
    jsonLocation=data.location
    locationData= JSON.parse(jsonLocation)
    console.log locationData
    tweets = locationData.tweets
    length = tweets.length
    console.log length
    i = 0
    while i < length
      $('<p>',
        text: tweets[i]).appendTo '#locationTweetDiv'
      i++
    return
  return
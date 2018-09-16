$(document).ready ->
  $.get $('#usertimeline').val(), (data) ->
    jsonuserData=data.screenName
    userData=JSON.parse(jsonuserData)
    username = userData.username
    screenName = userData.screenName
    location = userData.location
    followers = userData.followersCount
    description = userData.description
    tweets=userData.tweets;
    length = tweets.length
    userInfo = '<h1 align=\'center\'>' + username +
      '</h1>' + '<br><h5 align=\'center\'>Followers count: ' + followers +
      '</h5>' + '<h5 align=\'center\'>Location: ' + location + '</h5>' +
      '<h5 align=\'center\'>Description: ' + description + '</h5>'

    $('#userInfo').html userInfo
    i = 0
    while i < length
      if tweets[i] != null
        tweet = tweets[i]
        console.log tweets[i]
        $('<p>',
          class: 'list-group-item list-group-item-action waves-effect tweets'
          text: tweet).appendTo '#contentDiv'
      i++
    return
  return

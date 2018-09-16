$(document).ready ->
  $.get $('#statisticsTweets').val(), (data) ->
    console.log 'Inside statistics script'
    console.log data.stats
    console.log JSON.parse(data.stats)
    stat= JSON.parse(data.stats)
    for key,value of stat
      console.log key+' - '+value
      $('<p>',
        text: key+' - '+value).appendTo '#statistics'
  return
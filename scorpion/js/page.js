$(document).ready(function(){
  var lightPath = 'http://localhost:8080/light';
  var lightStatus = false;

  var booleanToOnOff = function(boolean) {
    return boolean ? 'on' : 'off';
  }

  var generateStatusText = function(boolean) {
    return 'Your house\'s light is currently ' + booleanToOnOff(boolean);
  }

  var generateControlText = function(boolean) {
      return 'Turn it ' + booleanToOnOff(!boolean);
  }

  var handleError = function(error) {
    if (error)
    {
      $('#light').text('Failed to connect to our server. Please contact supports');
      $('#control').toggleClass('none-visible', true);
    } else {
      $('#control').toggleClass('none-visible', false);
    }
  }

  var modifyLightAndControlText = function(data) {
    lightStatus = data.results.state;
    $('#light').text(generateStatusText(lightStatus));
    $('#control').text(generateControlText(lightStatus));
  }

  $.ajax({
    url: lightPath,
  })
  .done(function(data) {
    handleError(false);
    modifyLightAndControlText(data);
  })
  .fail(function() {
    handleError(true);
  });

  $('#control').click(function() {
    $.ajax({
      url: lightPath,
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(!lightStatus)
    })
    .done(function(data) {
      handleError(false);
      modifyLightAndControlText(data);
    })
    .fail(function() {
      handleError(true);
    })
  });
});

function getTime() {
  $.get(
    "http://senorpez.com:5001/time",
    function(data) {
      var year = data.year + " FY";
      var caste = "";

      if (data.festival_day && data.caste == 0) {
        caste = "Festival";
      } else if (data.festival_day) {
        caste = "Midfestival";
      } else {
        caste = data.caste + " Caste"
      }

      var day = data.day + " Day";
      var shift = data.shift + " Shift";

      document.getElementById("year").innerHTML = year + " " + caste + " " + day + " " + shift;
      document.getElementById("t0").innerHTML = new Date();
      document.getElementById("remainder").innerHTML = data.days;

      var startAngle = 0.25 * Math.PI;

      var decimals = (data.rawshift+"").split(".")[1].split("");

      var rawShiftAngle = (0.25 + 0.5 * data.rawshift) * Math.PI;
      var shiftAngle = (0.25 + 0.5 * Math.floor(data.shift)) * Math.PI;
      var nextShiftAngle = shiftAngle + 0.5 * Math.PI;

      var rawTitheAngle = (0.25 + 2 * (data.rawshift - Math.floor(data.rawshift))) * Math.PI;
      var titheAngle = (0.25 + 2 * decimals[0] / 10) * Math.PI;
      var nextTitheAngle = titheAngle + 0.2 * Math.PI;

      var rawSubtitheAngle = (0.25 + 2 * (data.rawshift * 10 - Math.floor(data.rawshift * 10))) * Math.PI;
      var subtitheAngle = (0.25 + 2 * decimals[1] / 10) * Math.PI;
      var nextSubtitheAngle = subtitheAngle + 0.2 * Math.PI;

      var rawSpinnerAngle = (0.25 + 2 * (data.rawshift * 100 - Math.floor(data.rawshift * 100))) * Math.PI;
      var spinnerAngle = (0.25 + 2 * decimals[2] / 10) * Math.PI;
      var nextSpinnerAngle = spinnerAngle + 0.2 * Math.PI;

      var c = document.getElementById("clockface");
      var ctx = c.getContext("2d");
      ctx.clearRect(0, 0, 300, 300);

      ctx.beginPath();
      ctx.arc(150, 150, 100, startAngle, nextShiftAngle);
      ctx.lineWidth = 8;
      ctx.strokeStyle = "#d3d3d3";
      ctx.stroke();

      ctx.beginPath();
      ctx.arc(150, 150, 100, startAngle, rawShiftAngle);
      ctx.lineWidth = 10;
      ctx.strokeStyle = "#aa0000";
      ctx.stroke();

      ctx.beginPath();
      ctx.arc(150, 150, 100, startAngle, shiftAngle);
      ctx.lineWidth = 12;
      ctx.strokeStyle = "#ff0000";
      ctx.stroke();

      ctx.beginPath();
      ctx.arc(150, 150, 75, startAngle, nextTitheAngle);
      ctx.lineWidth = 8;
      ctx.strokeStyle = "#d3d3d3";
      ctx.stroke();

      ctx.beginPath();
      ctx.arc(150, 150, 75, startAngle, rawTitheAngle);
      ctx.lineWidth = 10;
      ctx.strokeStyle = "#6a9f00";
      ctx.stroke();

      ctx.beginPath();
      ctx.arc(150, 150, 75, startAngle, titheAngle);
      ctx.lineWidth = 12;
      ctx.strokeStyle = "#00ff00";
      ctx.stroke();

      ctx.beginPath();
      ctx.arc(150, 150, 50, startAngle, nextSubtitheAngle);
      ctx.lineWidth = 8;
      ctx.strokeStyle = "#d3d3d3";
      ctx.stroke();

      ctx.beginPath();
      ctx.arc(150, 150, 50, startAngle, rawSubtitheAngle);
      ctx.lineWidth = 10;
      ctx.strokeStyle = "#006666";
      ctx.stroke();

      ctx.beginPath();
      ctx.arc(150, 150, 50, startAngle, subtitheAngle);
      ctx.lineWidth = 12;
      ctx.strokeStyle = "#0000ff";
      ctx.stroke();

      ctx.beginPath();
      ctx.arc(150, 150, 25, startAngle, nextSpinnerAngle);
      ctx.lineWidth = 8;
      ctx.strokeStyle = "#d3d3d3";
      ctx.stroke();

      ctx.beginPath();
      ctx.arc(150, 150, 25, startAngle, rawSpinnerAngle);
      ctx.lineWidth = 10;
      ctx.strokeStyle = "#a0a0a0";
      ctx.stroke();

      ctx.beginPath();
      ctx.arc(150, 150, 25, startAngle, spinnerAngle);
      ctx.lineWidth = 12;
      ctx.strokeStyle = "#000000";
      ctx.stroke();
    }
  )

  var t = setTimeout(getTime, 1000);
}

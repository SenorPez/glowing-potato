var scaleFactor = 1;
var apiResult;

function drawClockArc(ctx, radius, lineWidth, color, endAngle) {
  var startAngle = 0.25 * Math.PI;
  var center_x = 150;
  var center_y = 150;

  ctx.beginPath();
  ctx.arc(center_x, center_y, radius, startAngle, endAngle);
  ctx.lineWidth = lineWidth;
  ctx.strokeStyle = color;
  ctx.stroke();
}

function getTime() {
  apiResult = callAPI();
  apiResult.then(json => makeClock(json));
}

function callAPI() {
  return fetch('http://trident.senorpez.com/systems/1817514095/stars/1905216634/planets/-455609026/calendars/-1010689347/currentTime')
    .then(response => response.json());
}

function makeClock(json) {
  year = json.year;
  caste = json.caste;
  day = json.day;
  rawshift = json.shift + json.tithe;
  shift = Math.floor(rawshift * 100) / 100;

  if (day === 0) {
    festival_day = true;
  } else {
    festival_day = false;
  }

  var dispYear = year + " FY";
  var dispCaste = "";

  if (festival_day && caste === 0) {
    dispCaste = "Festival";
  } else if (festival_day) {
    dispCaste = "Midfestival";
  } else {
    dispCaste = caste + " Caste";
  }

  var dispDay = day + " Day";
  var dispShift = shift + " Shift";

  document.getElementById("year").innerHTML =
    dispYear + " " + dispCaste + " " + dispDay + " " + dispShift;
  document.getElementById("t0").innerHTML = new Date();

  var c = document.getElementById("clockface");
  var ctx = c.getContext("2d");
  ctx.clearRect(0, 0, 300, 300);

  var decimals = (rawshift.toString()).split(".")[1].split("");

  var rawShiftAngle = (0.25 + 0.5 * (rawshift - 1)) * Math.PI;
  var shiftAngle = (0.25 + 0.5 * (Math.floor(shift) - 1)) * Math.PI;
  var nextShiftAngle = shiftAngle + 0.5 * Math.PI;

  var ringNumber = 0;
  var ringRadius = -25 * ringNumber;

  drawClockArc(ctx, 100 * scaleFactor + ringRadius, 8, "#d3d3d3", nextShiftAngle);
  drawClockArc(ctx, 100 * scaleFactor + ringRadius, 10, "#aa0000", rawShiftAngle);
  drawClockArc(ctx, 100 * scaleFactor + ringRadius, 12, "#ff0000", shiftAngle);

  var rawTitheAngle = (0.25 + 2 * (rawshift - Math.floor(rawshift))) * Math.PI;
  var titheAngle = (0.25 + 2 * decimals[0] / 10) * Math.PI;
  var nextTitheAngle = titheAngle + 0.2 * Math.PI;

  var ringNumber = 1;
  var ringRadius = -25 * ringNumber;

  drawClockArc(ctx, 100 * scaleFactor + ringRadius, 8, "#d3d3d3", nextTitheAngle);
  drawClockArc(ctx, 100 * scaleFactor + ringRadius, 10, "#6a9f00", rawTitheAngle);
  drawClockArc(ctx, 100 * scaleFactor + ringRadius, 12, "#00ff00", titheAngle);

  var rawSubtitheAngle =
    (0.25 + 2 * (rawshift * 10 - Math.floor(rawshift * 10))) * Math.PI;
  var subtitheAngle = (0.25 + 2 * decimals[1] / 10) * Math.PI;
  var nextSubtitheAngle = subtitheAngle + 0.2 * Math.PI;

  var ringNumber = 2;
  var ringRadius = -25 * ringNumber;

  drawClockArc(ctx, 100 * scaleFactor + ringRadius, 8, "#d3d3d3", nextSubtitheAngle);
  drawClockArc(ctx, 100 * scaleFactor + ringRadius, 10, "#006666", rawSubtitheAngle);
  drawClockArc(ctx, 100 * scaleFactor + ringRadius, 12, "#0000ff", subtitheAngle);

  var ringNumber = 3;
  var ringRadius = -25 * ringNumber;

  while(100 * scaleFactor + ringRadius >= 25) {
    var power = ringNumber - 1;
    var rawSpinnerAngle =
      (0.25 + 2 * (rawshift * Math.pow(10, power) - Math.floor(rawshift * Math.pow(10, power)))) * Math.PI;
    var spinnerAngle = (0.25 + 2 * decimals[power] / 10) * Math.PI;
    var nextSpinnerAngle = spinnerAngle + 0.2 * Math.PI;

    drawClockArc(ctx, 100 * scaleFactor + ringRadius, 8, "#d3d3d3", nextSpinnerAngle);
    drawClockArc(ctx, 100 * scaleFactor + ringRadius, 10, "#a0a0a0", rawSpinnerAngle);
    drawClockArc(ctx, 100 * scaleFactor + ringRadius, 12, "#000000", spinnerAngle);

    ringNumber++;
    ringRadius = -25 * ringNumber;
  }

  var t = setTimeout(getTime, 50);
}

$(document).ready(function() {
  canvas = document.getElementById("clockface");
  canvas.addEventListener("mousewheel", handleMouseWheel, false);
  canvas.addEventListener("DOMMouseScroll", handleMouseWheel, false);
});

function handleMouseWheel(event) {
  scaleFactor = (event.wheelDelta > 0 || event.detail < 0) ? scaleFactor * 1.1 : scaleFactor * 0.9;
  scaleFactor = Math.max(1, scaleFactor);
  scaleFactor = Math.min(scaleFactor, 1.5);
}

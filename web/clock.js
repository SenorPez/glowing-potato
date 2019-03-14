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
  //var time_adj = -34.28646951536321; // TODO: API Call
  var time_adj = -72.27522481178462;
  var time_now = new Date(new Date() - time_adj * 86400000);
  var time_epoch = new Date("January 1, 2000 00:00:00 GMT+00:00");
  var time_delta = time_now - time_epoch;

  // Standard hours
  var hours = time_delta / 3600000;

  // Standard hours per local day
  var hours_per_local_day = 36.362486; // TODO: API Call

  // Local days
  var local_days = hours / hours_per_local_day;

  // Local days per loca year
  var local_days_per_local_year = 99.3142; // TODO: API Call

  // Local year, accounting for local calendar:
  // 2 years of 99 days followed by 1 year of 100 days // TODO: Leap year skips
  var local_days_countdown = local_days;
  var year = 1;

  while (true) {
    if (year % 3) {
      if (local_days_countdown > 99) {
        year += 1;
        local_days_countdown -= 99;
      } else {
        break;
      }
    } else {
      if (local_days_countdown > 100) {
        year += 1;
        local_days_countdown -= 100;
      } else {
        break;
      }
    }
  }

  var caste = 0;
  var festival_day = false;

  if (local_days_countdown < 1) {
    festival_day = true;
  } else if (local_days_countdown < 20) {
    caste = 1;
    local_days_countdown -= 1;
  } else if (local_days_countdown < 41) {
    caste = 2;
    local_days_countdown -= 20;
  } else if (year % 3 && local_days_countdown < 61) {
    caste = 3;
    local_days_countdown -= 41;
  } else if (local_days_countdown < 51) {
    caste = 3;
    local_days_countdown -= 41;
  } else if (local_days_countdown < 52) {
    caste = 3;
    festival_day = true;
    local_days_countdown -= 51;
  } else if (local_days_countdown < 62) {
    caste = 3;
    local_days_countdown -= 52;
  } else if (year % 3 === 0) {
    local_days_countdown -= 1;
  } else if (local_days_countdown < 80) {
    caste = 4;
    local_days_countdown -= 61;
  } else if (local_days_countdown < 99) {
    caste = 5;
    local_days_countdown -= 80;
  } else {
    caste = null;
    local_days_countdown = null;
  }

  var day = Math.ceil(local_days_countdown);
  local_days_countdown %= 1;
  var rawshift = (1 + local_days_countdown / 0.25);
  var shift = Math.floor(rawshift * 100) / 100;

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
  document.getElementById("remainder").innerHTML = local_days;

  var c = document.getElementById("clockface");
  var ctx = c.getContext("2d");
  ctx.clearRect(0, 0, 300, 300);

  var decimals = (rawshift.toString()).split(".")[1].split("");

  var rawShiftAngle = (0.25 + 0.5 * (rawshift - 1)) * Math.PI;
  var shiftAngle = (0.25 + 0.5 * (Math.floor(shift) - 1)) * Math.PI;
  var nextShiftAngle = shiftAngle + 0.5 * Math.PI;

  drawClockArc(ctx, 100, 8, "#d3d3d3", nextShiftAngle);
  drawClockArc(ctx, 100, 10, "#aa0000", rawShiftAngle);
  drawClockArc(ctx, 100, 12, "#ff0000", shiftAngle);

  var rawTitheAngle = (0.25 + 2 * (rawshift - Math.floor(rawshift))) * Math.PI;
  var titheAngle = (0.25 + 2 * decimals[0] / 10) * Math.PI;
  var nextTitheAngle = titheAngle + 0.2 * Math.PI;

  drawClockArc(ctx, 75, 8, "#d3d3d3", nextTitheAngle);
  drawClockArc(ctx, 75, 10, "#6a9f00", rawTitheAngle);
  drawClockArc(ctx, 75, 12, "#00ff00", titheAngle);

  var rawSubtitheAngle =
    (0.25 + 2 * (rawshift * 10 - Math.floor(rawshift * 10))) * Math.PI;
  var subtitheAngle = (0.25 + 2 * decimals[1] / 10) * Math.PI;
  var nextSubtitheAngle = subtitheAngle + 0.2 * Math.PI;

  drawClockArc(ctx, 50, 8, "#d3d3d3", nextSubtitheAngle);
  drawClockArc(ctx, 50, 10, "#006666", rawSubtitheAngle);
  drawClockArc(ctx, 50, 12, "#0000ff", subtitheAngle);

  var rawSpinnerAngle =
    (0.25 + 2 * (rawshift * 100 - Math.floor(rawshift * 100))) * Math.PI;
  var spinnerAngle = (0.25 + 2 * decimals[2] / 10) * Math.PI;
  var nextSpinnerAngle = spinnerAngle + 0.2 * Math.PI;

  drawClockArc(ctx, 25, 8, "#d3d3d3", nextSpinnerAngle);
  drawClockArc(ctx, 25, 10, "#a0a0a0", rawSpinnerAngle);
  drawClockArc(ctx, 25, 12, "#000000", spinnerAngle);

  var t = setTimeout(getTime, 500);
}

function getTime() {
  var time_adj = -34.28646951536321; // TODO: API Call
  var time_now = new Date(new Date() - time_adj * 86400000);
  var time_epoch = new Date('January 1, 2000 00:00:00 GMT+00:00');
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

  var caste = 0
  var festival_day = false;

  while (true) {
    if (local_days_countdown < 1) {
      festival_day = true;
      break;
    }
    if (local_days_countdown < 20) {
      caste = 1;
      local_days_countdown -= 1;
      break;
    }
    if (local_days_countdown < 41) {
      caste = 2;
      local_days_countdown -= 20;
      break;
    }
    if (year % 3) {
      if (local_days_countdown < 61) {
        caste = 3;
        local_days_countdown -= 41;
        break;
      }
    } else {
      if (local_days_countdown < 51) {
        caste = 3;
        local_days_countdown -= 41;
        break;
      }
      if (local_days_countdown < 52) {
        caste = 3;
        festival_day = true;
        local_days_countdown -= 51;
        break;
      }
      if (local_days_countdown < 62) {
        caste = 3;
        local_days_countdown -= 52;
        break;
      }
      local_days_countdown -= 1;
    }
    if (local_days_countdown < 80) {
      caste = 4
      local_days_countdown -= 61;
      break;
    }
    if (local_days_countdown < 99) {
      caste = 5
      local_days_countdown -= 80;
      break;
    }
    break;
  }

  var day = Math.ceil(local_days_countdown);
  local_days_countdown %= 1;

  var floored = Math.floor((1 + local_days_countdown / 0.25) * 100) / 100;
  var shift = floored;
  var rawshift = (1 + local_days_countdown / 0.25);

  var year = year + " FY";

  if (festival_day && caste == 0) {
    caste = "Festival";
  } else if (festival_day) {
    caste = "Midfestival";
  } else {
    caste = caste + " Caste"
  }

  var day = day + " Day";
  var shiftNum = Math.floor(shift) - 1;
  var shift = shift + " Shift";

  document.getElementById("year").innerHTML = year + " " + caste + " " + day + " " + shift;
  document.getElementById("t0").innerHTML = new Date();
  document.getElementById("remainder").innerHTML = local_days;

  var startAngle = 0.25 * Math.PI;

  var decimals = (rawshift+"").split(".")[1].split("");

  var rawShiftAngle = (0.25 + 0.5 * (rawshift - 1)) * Math.PI;
  var shiftAngle = (0.25 + 0.5 * shiftNum) * Math.PI;
  var nextShiftAngle = shiftAngle + 0.5 * Math.PI;

  var rawTitheAngle = (0.25 + 2 * (rawshift - Math.floor(rawshift))) * Math.PI;
  var titheAngle = (0.25 + 2 * decimals[0] / 10) * Math.PI;
  var nextTitheAngle = titheAngle + 0.2 * Math.PI;

  var rawSubtitheAngle = (0.25 + 2 * (rawshift * 10 - Math.floor(rawshift * 10))) * Math.PI;
  var subtitheAngle = (0.25 + 2 * decimals[1] / 10) * Math.PI;
  var nextSubtitheAngle = subtitheAngle + 0.2 * Math.PI;

  var rawSpinnerAngle = (0.25 + 2 * (rawshift * 100 - Math.floor(rawshift * 100))) * Math.PI;
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

  var t = setTimeout(getTime, 1000);
}


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

      var day = (data.day) + " Day";
      var shift = data.shift + " Shift";

      document.getElementById("year").innerHTML = year + " " + caste + " " + day + " " + shift;
      document.getElementById("t0").innerHTML = new Date();
      document.getElementById("remainder").innerHTML = data.days;
    }
  )

  var t = setTimeout(getTime, 1000);
}

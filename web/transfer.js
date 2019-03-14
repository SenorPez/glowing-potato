var porkchopPlot = document.getElementById('porkchop');

var defaultOrigin = -455609026;
var defaultTarget = 272811578;

$(document).ready(function() {
  var urlParams = new URLSearchParams(window.location.search);
  var origin = urlParams.has('origin') ? urlParams.get('origin') : defaultOrigin
  var target = urlParams.has('target') ? urlParams.get('target') : defaultTarget

  var posting = $.post(
    "http://senorpez.com:5001/transfer",
    {origin: origin, target: target});

  var spinnerDiv = document.getElementById('orbitCell');
  var spinner = new Spinner().spin(spinnerDiv);
  spinnerDiv.classList.add("opaque");

  posting.done(
    function(data) {
      var plotData = [{
        z: data.delta_v,
        type: 'contour',
        y0: 50,
        dy: 1,
        contours: {
          showlines: false,
          size: 5000,
          start: 10000,
          end: 95000
        },
        colorbar: {
          ticks: 'outside',
          tick0: 10000,
          dtick: 10000
        },
        colorscale: [
          [0, '#006837'],
          [0.1, '#1a9850'],
          [0.2, '#66bd63'],
          [0.3, '#a6d96a'],
          [0.4, '#d9ef8b'],
          [0.5, '#ffffbf'],
          [0.6, '#fee08b'],
          [0.7, '#fdae61'],
          [0.8, '#f46d43'],
          [0.9, '#d73027'],
          [1, '#a50026']
        ]
      }];

      var layout = {
        xaxis: {
          title: 'Launch Day'
        },
        yaxis: {
          title: 'Flight Time'
        }
      };
      Plotly.newPlot('porkchop', plotData, layout);

      spinnerDiv.classList.remove("opaque");
      spinner.stop();
      $("#orbit").attr('src', 'http://senorpez.com/orbit.png?' + $.now());
      $("#orbit-x").attr('src', 'http://senorpez.com/orbit-x.png?' + $.now());
      $("#orbit-y").attr('src', 'http://senorpez.com/orbit-y.png?' + $.now());
      $("#orbit-z").attr('src', 'http://senorpez.com/orbit-z.png?' + $.now());
      $("#launch_time").text(data.launch_time);
      $("#flight_time").text(data.flight_time);
      $("#delta_v").text(data.min_delta_v);

      porkchopPlot.on('plotly_click', function(data) {
        var x = data.points[0].x;
        var y = data.points[0].y;
        var delta_v = data.points[0].z;

        $("#launch_time").text(x);
        $("#flight_time").text(y);
        $("#delta_v").text(delta_v);
        var spinner = new Spinner().spin(spinnerDiv);
        spinnerDiv.classList.add("opaque");

        var thing = $.post(
          "http://senorpez.com:5001/transfer",
          {origin: origin, target: target, flight_time: y, launch_time: x, delta_v: delta_v});

        thing.done(
          function(data) {
            spinnerDiv.classList.remove("opaque");
            spinner.stop();
            $("#orbit").attr('src', 'http://senorpez.com/orbit.png?' + $.now());
            $("#orbit-x").attr('src', 'http://senorpez.com/orbit-x.png?' + $.now());
            $("#orbit-y").attr('src', 'http://senorpez.com/orbit-y.png?' + $.now());
            $("#orbit-z").attr('src', 'http://senorpez.com/orbit-z.png?' + $.now());
          }
        );
      });
    }
  );
});
var porkchopPlot = document.getElementById('porkchop')

var defaultOrigin = -455609026
var defaultTarget = 272811578

$(document).ready(function() {
  $.post(
    "http://senorpez.com:5001/transfer",
    {origin: defaultOrigin, target: defaultTarget},
    function(data) {
      var minDeltaV = Math.min(data.delta_v);
      var maxDeltaV = Math.max(data.delta_v);
      var scaleMin = Math.floor(minDeltaV / 5000) * 5000;
      var scaleMax = Math.ceil(maxDeltaV / 5000) * 5000;

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
      Plotly.newPlot('porkchop', plotData);

      $("#orbit").attr('src', 'http://senorpez.com/orbit.png?' + $.now());
      $("#orbit-x").attr('src', 'http://senorpez.com/orbit-x.png?' + $.now());
      $("#orbit-y").attr('src', 'http://senorpez.com/orbit-y.png?' + $.now());
      $("#orbit-z").attr('src', 'http://senorpez.com/orbit-z.png?' + $.now());
      $("#launch_time").text(data.launch_time);
      $("#flight_time").text(data.flight_time);
      $("#delta_v").text(data.min_delta_v);
    }
  );
});

var porkchopPlot = document.getElementById('porkchop');

$(document).ready(function() {
  var posting = $.post(
    "http://senorpez.com:5001/transfernew")
  .done(function(data) {
    var plotData = [{
      z: data.delta_v,
      type: 'contour',
      x0: 0,
      dx: 1,
      y0: 0,
      dy: 1,
      contours: {
        showlines: false,
        size: 5000,
        start: 10000,
        end: 95000
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
        title: 'Launch Day',
        range: [0, 200]
      },
      yaxis: {
        title: 'Arrival Day',
        range: [0, 500]
      }
    };
    Plotly.newPlot('porkchop', plotData, layout);

    $("#launch_time").text(data.launch_time);
    $("#flight_time").text(data.flight_time);
    $("#delta_v").text(data.min_delta_v);
  });
});

/*
$(document).ready(function() {
  var posting = $.post(
    "http://senorpez.com:5001/transfer",
    {
      flight_start: y_min,
      flight_end: y_max,
      launch_start: x_min,
      launch_end: x_max,
    })
  .done(function(data) {
    var plotData = [{
      z: data.delta_v,
      type: 'contour',
      x0: 0,
      dx: 1,
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
        title: 'Launch Day',
        range: [data.launch_start, data.launch_end]
      },
      yaxis: {
        title: 'Flight Time',
        range: [data.flight_start, data.flight_end]
      }
    };
    Plotly.newPlot('porkchop', plotData, layout);

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

      $.post(
        "http://senorpez.com:5001/plottransfer",
        {flight_time: y, launch_time: x})
      .done(function(data) {
        $("#orbit").attr('src', 'http://senorpez.com/orbit.png?' + $.now());
        $("#orbit-x").attr('src', 'http://senorpez.com/orbit-x.png?' + $.now());
        $("#orbit-y").attr('src', 'http://senorpez.com/orbit-y.png?' + $.now());
        $("#orbit-z").attr('src', 'http://senorpez.com/orbit-z.png?' + $.now());
      });
    });

    porkchopPlot.on('plotly_relayout', function(eventData) {
      if (typeof eventData['yaxis.range[0]'] !== 'undefined') {
        y_min = eventData['yaxis.range[0]']
        y_max = eventData['yaxis.range[1]']
      }

      if (typeof eventData['xaxis.range[0]'] !== 'undefined') {
        x_min = eventData['xaxis.range[0]']
        x_max = eventData['xaxis.range[1]']
      }

      var posting = $.post(
        "http://senorpez.com:5001/transfer",
        {
          flight_start: y_min,
          flight_end: y_max,
          launch_start: x_min,
          launch_end: x_max
        })
      .done(function(data) {
        var update = {
          z: [data.delta_v],
          x0: data.launch_offset,
          y0: data.flight_start
        }
        Plotly.restyle('porkchop', update);
      });
    });
  });
});
*/

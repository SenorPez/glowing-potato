$(document).ready(function() {
    $.get(
        "http://senorpez.com:5001/omegahydri",
        function(data) {
            var plotData = [];

            for (i = 0; i < data.x.length; i++) {
                var orbitPlot = {
                    type: 'scatter3d',
                    showlegend: false,
                    mode: 'lines',
                    x: data.x[i],
                    y: data.y[i],
                    z: data.z[i],
                    opacity: 0.5,
                    line: {
                        width: 6,
                        color: data.c[i]
                    },
                    hoverinfo: 'none'
                };

                var planetPlot = {
                    type: 'scatter3d',
                    name: data.n[i],
                    mode: 'markers',
                    x: [data.p[i][0]],
                    y: [data.p[i][1]],
                    z: [data.p[i][2]],
                    opacity: 1,
                    marker: {
                        color: data.c[i]
                    }
                };

                plotData.push(orbitPlot);
                plotData.push(planetPlot);
            }

            var winterSolsticePlot = {
                type: 'scatter3d',
                showlegend: false,
                mode: 'lines',
                x: [0, 0],
                y: [0, 1],
                z: [0, 0],
                opacity: 0.5,
                line: {
                    width: 3,
                    color: 'blue'
                },
                hoverinfo: 'none'
            };

            var summerSolsticePlot = {
                type: 'scatter3d',
                showlegend: false,
                mode: 'lines',
                x: [0, 0],
                y: [0, -1],
                z: [0, 0],
                opacity: 0.5,
                line: {
                    width: 3,
                    color: 'black'
                },
                hoverinfo: 'none'
            };

            var equinoxPlot = {
                type: 'scatter3d',
                showlegend: false,
                mode: 'lines',
                x: [-1, 1],
                y: [0, 0],
                z: [0, 0],
                opacity: 0.5,
                line: {
                    width: 3,
                    color: 'black'
                },
                hoverinfo: 'none'
            };

            plotData.push(winterSolsticePlot);
            plotData.push(summerSolsticePlot);
            plotData.push(equinoxPlot);

            var starPlot = {
                type: 'scatter3d',
                name: '1 Eta Veneris',
                showlegend: false,
                mode: 'markers',
                x: [0],
                y: [0],
                z: [0],
                opacity: 1,
                marker: {
                    color: 'orange'
                },
                hoverinfo: 'none'
            };

            plotData.push(starPlot);

            var layout = {
                margin: {
                    l: 0,
                    r: 0,
                    t: 0,
                    b: 0,
                    pad: 10
                },
                scene: {
                    xaxis: {
                        showspikes: false,
                        hoverformat: '.2f',
                        range: [-2, 2],
                        fixedrange: false
                    },
                    yaxis: {
                        showspikes: false,
                        hoverformat: '.2f',
                        range: [-2, 2],
                        fixedrange: false
                    },
                    zaxis: {
                        showspikes: false,
                        hoverformat: '.2f',
                        range: [-0.1, 0.1],
                        dtick: 0.02
                    }
                }
            };

            Plotly.newPlot('orbit', plotData, layout);
        }
    );
});


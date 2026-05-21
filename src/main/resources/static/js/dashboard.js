/**
 * Gráfico: emissões de CO₂ por hora (linha simples)
 */
document.addEventListener('DOMContentLoaded', function () {

    // ─── Gráfico de linha — CO₂ por hora ────────────────────────────────────
    const canvasCo2Hora = document.getElementById('co2HoraChart');
    if (canvasCo2Hora && typeof co2PorHora !== 'undefined') {
        const horas  = Array.from({length: 24}, (_, i) => i + 'h');
        const valores = Array.from({length: 24}, (_, i) => co2PorHora[i] ?? 0);

        // Cores dinâmicas: verde se OK, vermelho se acima do limite
        const LIMITE_CO2 = 1000; // ppm
        const barColors  = valores.map(v =>
            v > LIMITE_CO2 ? 'rgba(239,68,68,0.7)' : 'rgba(34,197,94,0.7)'
        );

        new Chart(canvasCo2Hora.getContext('2d'), {
            type: 'line',
            data: {
                labels: horas,
                datasets: [{
                    label: 'CO₂ (ppm) — total por hora',
                    data: valores,
                    borderColor: '#22c55e',
                    backgroundColor: 'rgba(34,197,94,0.08)',
                    pointBackgroundColor: barColors,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    tension: 0.4,
                    fill: true,
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                interaction: {mode: 'index', intersect: false},
                plugins: {
                    legend: {display: false},
                    tooltip: {
                        backgroundColor: '#111814',
                        borderColor: '#1e3027',
                        borderWidth: 1,
                        callbacks: {
                            label: ctx => ` ${ctx.parsed.y.toFixed(1)} ppm`,
                            afterLabel: ctx =>
                                ctx.parsed.y > LIMITE_CO2 ? ' ⚠ Acima do limite!' : ' ✓ Normal'
                        }
                    }
                },
                scales: {
                    x: {
                        grid: {color: 'rgba(30,48,39,0.6)'},
                        ticks: {color: '#9ca3af', font: {size: 11}}
                    },
                    y: {
                        grid: {color: 'rgba(30,48,39,0.6)'},
                        ticks: {color: '#9ca3af', font: {size: 11}},
                        title: {display: true, text: 'CO₂ (ppm)', color: '#22c55e'},
                        // Linha de limite visual
                        afterDataLimits: scale => {
                            scale.max = Math.max(scale.max, LIMITE_CO2 * 1.2);
                        }
                    }
                }
            }
        });

        // Linha de limite horizontal via anotação manual 
        // Desenha sobre o canvas depois que o Chart.js renderizou
        canvasCo2Hora.addEventListener('chartRendered', () => {
            const chart = Chart.getChart(canvasCo2Hora);
            if (!chart) return;
            const ctx   = canvasCo2Hora.getContext('2d');
            const yPos  = chart.scales.y.getPixelForValue(LIMITE_CO2);
            ctx.save();
            ctx.setLineDash([6, 4]);
            ctx.strokeStyle = 'rgba(239,68,68,0.5)';
            ctx.lineWidth   = 1;
            ctx.beginPath();
            ctx.moveTo(chart.chartArea.left,  yPos);
            ctx.lineTo(chart.chartArea.right, yPos);
            ctx.stroke();
            ctx.restore();
        });
    }

    // ─── Gráfico de barras — CO₂ por terminal ────────────────────────────────
    const canvasTerminal = document.getElementById('terminalChart');
    if (canvasTerminal && typeof terminalChartData !== 'undefined') {
        new Chart(canvasTerminal.getContext('2d'), {
            type: 'bar',
            data: {
                labels: terminalChartData.labels,
                datasets: [{
                    label: 'CO₂ médio 24h (ppm)',
                    data: terminalChartData.values,
                    backgroundColor: terminalChartData.values.map(v =>
                        v > 1000 ? 'rgba(239,68,68,0.7)' : 'rgba(34,197,94,0.7)'
                    ),
                    borderRadius: 4,
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {display: false},
                    tooltip: {
                        backgroundColor: '#111814',
                        borderColor: '#1e3027',
                        borderWidth: 1
                    }
                },
                scales: {
                    x: {grid: {color: 'rgba(30,48,39,0.6)'}, ticks: {color: '#9ca3af'}},
                    y: {grid: {color: 'rgba(30,48,39,0.6)'}, ticks: {color: '#9ca3af'},
                        title: {display: true, text: 'ppm', color: '#9ca3af'}}
                }
            }
        });
    }

    // ─── Badges dinâmicos para status de alerta ───────────────────────────────
    // Aplica a qualquer elemento com data-co2="valor"
    document.querySelectorAll('[data-co2]').forEach(el => {
        const val = parseFloat(el.dataset.co2);
        if (isNaN(val)) return;

        const badge = el.querySelector('.alert-badge') || el;
        badge.classList.remove('badge-ok', 'badge-warn', 'badge-danger');

        if (val > 1000) {
            badge.classList.add('badge-danger');
            badge.textContent = '⚠ Crítico';
        } else if (val > 800) {
            badge.classList.add('badge-warn');
            badge.textContent = '⚡ Atenção';
        } else {
            badge.classList.add('badge-ok');
            badge.textContent = '✓ Normal';
        }
    });

    // ─── Auto-refresh do dashboard a cada 60 segundos ────────────────────────
    const autoRefresh = document.getElementById('autoRefreshToggle');
    if (autoRefresh) {
        let timer = null;
        autoRefresh.addEventListener('change', function () {
            if (this.checked) {
                timer = setInterval(() => window.location.reload(), 60000);
            } else {
                clearInterval(timer);
            }
        });
    }
});
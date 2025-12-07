// ==============================
// CONFIG
// ==============================
const API_ALERTS = "http://localhost:8080/alerts";

// ==============================
// CHART INIT
// ==============================
const ctx = document.getElementById("latencyChart").getContext("2d");

const latencyChart = new Chart(ctx, {
    type: "line",
    data: {
        labels: [],
        datasets: [{
            label: "Avg Latency",
            borderColor: "rgb(1,200,100)",
            backgroundColor: "rgba(0,200,100,0.1)",
            data: []
        }]
    },
    options: {
        animation: false,
        scales: {
            y: { beginAtZero: true }
        }
    }
});

// ==============================
// MAIN LOOP
// ==============================
loadData();
setInterval(loadData, 2000);

async function loadData(){
    const alerts = await loadAlerts();
    updateKPIs(alerts);
    updateChart(alerts);
}

// ==============================
// API
// ==============================
async function loadAlerts(){
    try {
        const res = await fetch(API_ALERTS);
        if(!res.ok) throw new Error("API error " + res.status);
        return await res.json();
    } catch(e){
        console.error("Error alerts:", e);
        showOfflineState();
        return [];
    }
}

// ==============================
// KPIs
// ==============================
function updateKPIs(alerts){
    const values = alerts.map(a => a.value || a.latency || 0);
    const p95    = percentile(values, 95);
    const avg    = average(values);
    const count  = alerts.length;

    // -- KPIS --
    document.getElementById("kpi-p95").innerText = Math.round(p95) + " ms";
    document.getElementById("kpi-avg").innerText = Math.round(avg) + " ms";
    document.getElementById("kpi-alerts").innerText = count;

    // -- STATUS --
    const statusElem = document.getElementById("kpi-status");

    if(p95 > 1500){
        statusElem.innerText = "🔴 CRÍTICO";
        statusElem.className = "bad";
    } 
    else if(p95 > 800){
        statusElem.innerText = "🟡 ALERTA";
        statusElem.className = "warn";
    } 
    else {
        statusElem.innerText = "🟢 OK";
        statusElem.className = "good";
    }

    // -- LIST --
    const list = document.getElementById("alertList");
    list.innerHTML =
        count === 0
        ? "<li>No hay alertas 🔵</li>"
        : alerts.map(a =>
            `<li>⚠ ${a.metric || a.service} — ${Math.round(a.value)}ms (${a.severity || "?"})</li>`
        ).join("");
}

// ==============================
// CHART
// ==============================
function updateChart(alerts){
    const values = alerts.map(a => a.value || a.latency || 0);
    const avg = average(values);

    latencyChart.data.labels.push("");
    latencyChart.data.datasets[0].data.push(avg);

    // -- limit history --
    if(latencyChart.data.labels.length > 30){
        latencyChart.data.labels.shift();
        latencyChart.data.datasets[0].data.shift();
    }

    // -- color logic --
    const statusElem = document.getElementById("kpi-status").textContent;

    if(statusElem.includes("CRÍTICO")){
        latencyChart.data.datasets[0].borderColor = "#ff5555";
    } 
    else if(statusElem.includes("ALERTA")){
        latencyChart.data.datasets[0].borderColor = "#ffb300";
    } 
    else {
        latencyChart.data.datasets[0].borderColor = "#00e676";
    }

    latencyChart.update();
}

// ==============================
// FALLBACK (si no hay backend)
// ==============================
function showOfflineState(){
    document.getElementById("kpi-status").innerText = "⚪ offline";
    document.getElementById("kpi-status").className = "warn";

    document.getElementById("alertList").innerHTML =
        "<li>No se pudo conectar al backend</li>";
}


// ==============================
// UTIL
// ==============================
function average(arr){
    if(arr.length===0) return 0;
    return arr.reduce((a,b)=>a+b,0)/arr.length;
}

function percentile(arr, p){
    if(arr.length===0) return 0;
    arr = [...arr].sort((a,b) => a-b);
    const idx = Math.floor(arr.length * p / 100);
    return arr[idx];
}

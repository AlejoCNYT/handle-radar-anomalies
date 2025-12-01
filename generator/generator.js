const axios=require("axios");

setInterval(()=>{
    let normal = Math.random()*250;           // latencia normal
    let anomaly = Math.random()<0.10?800:normal;  // 10% anomalías

    axios.post("http://localhost:8081/events",{
        service:"payment-api",
        latency_ms: anomaly,
        status_code:200
    }).catch(()=>{});

    axios.get("http://localhost:8082/alerts/live")
},250);

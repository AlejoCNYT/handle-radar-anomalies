[![Demo del Proyecto](https://img.youtube.com/vi/bggTBPMWeE8/hqdefault.jpg)](https://youtu.be/bggTBPMWeE8)

# 📡 Radar de Latencia & Alertas — Sistema de Monitoreo Distribuido

Radar-Anomalías es un ecosistema de observabilidad que captura métricas de latencia, errores por servicio y dispara alertas en tiempo real utilizando **Prometheus + Alertmanager**, con visualización en un dashboard web en vivo.  

Incluye 3 microservicios principales:

| Servicio | Puerto | Función |
|---------|--------|----------|
| **ingestor-service** | `8081` | Recibe eventos y registra métricas Micrometer → Prometheus |
| **analyzer-service** | `8082` | Consulta Prometheus y expone alertas vía REST/WebSocket |
| **dashboard** | `8080` | UI que grafica P95, errores/min y alertas activas |

---

## 🛑 Arquitectura

```
CLIENTE  →  POST /events  →  INGESTOR
                 ↓ 
           /actuator/prometheus
                 ↓ scrape
           PROMETHEUS  ← reglas Alertmanager
                 ↓
ANALYZER  ← consulta API    →  /alerts   → Dashboard UI
```

---

## 📍 Tecnologías

| Componente | Tecnología |
|----------|------------|
| Backend | Spring Boot + WebSocket STOMP |
| Métricas | Micrometer + Prometheus Registry |
| Alertas | Prometheus Rules + Alertmanager |
| Visualización | HTML + JS + Chart.js |
| Infraestructura | Docker Compose |

---

## 🚀 Cómo Ejecutarlo

```bash
git clone <repo>
cd radar-anomalias
docker compose up -d --build
```

📌 Accesos:

| UI / Servicios | URL |
|---|---|
| Dashboard UI | http://localhost:8080 |
| API Analyzer JSON | http://localhost:8082/alerts |
| Prometheus Console | http://localhost:9090 |
| Alertmanager UI | http://localhost:9093 |

---

## 📤 Enviar tráfico de prueba

```powershell
for ($i=1; $i -le 120; $i++) {
    Invoke-RestMethod http://localhost:8081/events `
    -Method POST `
    -Headers @{ "Content-Type"="application/json" } `
    -Body '{ "service":"payment-api", "region":"us-east", "status_code":500, "latency_ms":4500 }'
}
```

---

## ⚠ Regla de Alerta P95

```yaml
groups:
  - name: latency-alerts
    rules:
      - alert: HighLatencyP95
        expr: histogram_quantile(
                0.95,
                rate(http_latency_ms_bucket{service="payment-api"}[5m])
              ) > 2000
        for: 1m
        labels:
          severity: warning
        annotations:
          description: "P95 supera 2000ms durante 1 minuto"
```

---

## 🖥 Dashboard Incluye

| Métrica / Funcionalidad | Estado |
|---|---|
| Latencia p95/p99 en tiempo real | 🟢 |
| Errores por minuto (HTTP 5xx) | 🟢 |
| Throughput (req/s) por servicio | 🟢 |
| Alertas activas (Z-score / IQR) | 🟢 |
| Visualización de alertas con severidad | 🟢 |
| Indicador de SLO (error budget) | 🟢 |
| Paneles en Grafana (opcional) | 🟢 Integrado |
| Exportación OpenTelemetry → Prometheus | 🟢 |
| WebSocket Live Feed | 🟡 Ajustable |
| Drill-down por servicio / endpoint | 🟡 Prototipo |
| Retención de métricas históricas | 🟡 7–30 días (configurable) |

### ✨ Notas adicionales

- El dashboard web básico usa **HTML/JS/Chart.js**  
- Grafana está habilitado como opción avanzada para visualización y análisis en tiempo real  
- Las métricas clave se basan en las consultas PromQL incluidas en el proyecto  
- Se agregaron indicadores de calidad del servicio (SLO) inspirados en Google SRE  
- Las alertas se generan a partir de detectores estadísticos configurables (Z-score, IQR)

### 🚀 Qué permite esta versión del proyecto

- Monitorear latencia y errores en tiempo real  
- Detectar anomalías basado en estadística interpretables  
- Visualizar alertas en un dashboard web o Grafana  
- Evaluar cumplimiento de objetivos de confiabilidad (SLO)  
- Extender rápidamente hacia métricas adicionales o ML  

import {useEffect, useMemo, useState} from "react"
import {Cell, Pie, PieChart, ResponsiveContainer, Tooltip} from "recharts"
import {apiUrl} from "../../api/apiConfig.js"
import "./StatisticsPage.css"

const SLICE_COLORS = ["#7c3aed", "#06b6d4", "#f59e0b", "#10b981", "#ec4899", "#ef4444"]
const OTHER_COLOR = "#94a3b8"
const MAX_SLICES = 6

const todayIso = () => new Date().toISOString().slice(0, 10)
const tomorrowIso = () => {
    const date = new Date()
    date.setDate(date.getDate() + 1)
    return date.toISOString().slice(0, 10)
}

const buildChartData = (statistikaPoBiciklu) => {
    if (!statistikaPoBiciklu || statistikaPoBiciklu.length === 0) {
        return []
    }
    const top = statistikaPoBiciklu.slice(0, MAX_SLICES)
    const rest = statistikaPoBiciklu.slice(MAX_SLICES)
    const chartData = top.map((stavka) => ({
        naziv: stavka.nazivBicikla,
        broj: stavka.brojIznajmljivanja,
        procenat: stavka.procenat
    }))
    if (rest.length > 0) {
        chartData.push({
            naziv: "Ostalo",
            broj: rest.reduce((sum, stavka) => sum + stavka.brojIznajmljivanja, 0),
            procenat: Math.round(rest.reduce((sum, stavka) => sum + (stavka.procenat ?? 0), 0) * 100) / 100
        })
    }
    return chartData
}

const renderSliceLabel = ({x, y, textAnchor, percent}) => (
    <text
        className="pie-slice-label"
        x={x}
        y={y}
        textAnchor={textAnchor}
        dominantBaseline="middle"
        fill="#1e1b4b"
        fontSize={12}
        fontWeight={700}
    >
        {`${Math.round(percent * 1000) / 10}%`}
    </text>
)

const StatisticsPage = ({loggedInUser}) => {
    const [datumOd, setDatumOd] = useState(todayIso)
    const [datumDo, setDatumDo] = useState(tomorrowIso)
    const [stats, setStats] = useState(null)
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(true)

    const rangeInvalid = datumOd && datumDo && datumDo <= datumOd

    useEffect(() => {
        if (rangeInvalid) {
            return
        }

        let cancelled = false

        const fetchStats = async () => {
            setLoading(true)
            setError("")
            try {
                const response = await fetch(
                    apiUrl(`/api/iznajmljivanja/statistika?datumOd=${datumOd}&datumDo=${datumDo}`),
                    {headers: {"Authorization": `Bearer ${loggedInUser.token}`}}
                )
                if (!response.ok) {
                    if (!cancelled) setError("Statistika ne može biti učitana")
                    return
                }
                const data = await response.json()
                if (!cancelled) setStats(data)
            } catch (error) {
                console.error("Error fetching statistika:", error.message)
                if (!cancelled) setError("Statistika ne može biti učitana")
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void fetchStats()
        return () => {
            cancelled = true
        }
    }, [datumOd, datumDo, rangeInvalid, loggedInUser.token])

    const chartData = useMemo(() => buildChartData(stats?.statistikaPoBiciklu), [stats])

    return (
        <main className="main-content">
            <h1 className="page-title">Statistika</h1>
            <p className="page-subtitle">Pregled poslovanja i zastupljenosti bicikala u iznajmljivanjima.</p>

            <section className="filter-bar" aria-label="Period za proveru dostupnosti">
                <div className="filter-field">
                    <label htmlFor="statDatumOd">Period od</label>
                    <input id="statDatumOd" type="date" value={datumOd} onChange={(event) => setDatumOd(event.target.value)}/>
                </div>
                <div className="filter-field">
                    <label htmlFor="statDatumDo">Period do</label>
                    <input id="statDatumDo" type="date" min={datumOd} value={datumDo} onChange={(event) => setDatumDo(event.target.value)}/>
                </div>
                {rangeInvalid && <p className="filter-error">Datum završetka mora biti posle datuma početka.</p>}
            </section>

            {error && <p className="error-banner">{error}</p>}

            {!error && !loading && stats && (
                <>
                    <section className="stats-grid" aria-label="Pregled ključnih brojeva">
                        <article className="stat-tile">
                            <span className="stat-tile-label">Ukupno iznajmljivanja</span>
                            <strong className="stat-tile-value">{stats.ukupanBrojIznajmljivanja}</strong>
                        </article>
                        <article className="stat-tile">
                            <span className="stat-tile-label">Ukupno klijenata</span>
                            <strong className="stat-tile-value">{stats.ukupanBrojKlijenata}</strong>
                        </article>
                        <article className="stat-tile">
                            <span className="stat-tile-label">Ukupno bicikala</span>
                            <strong className="stat-tile-value">{stats.ukupanBrojBicikala}</strong>
                        </article>
                        <article className="stat-tile stat-tile-accent">
                            <span className="stat-tile-label">Najiznajmljivanija bicikla</span>
                            <strong className="stat-tile-value stat-tile-value-text">{stats.najpopularnijiBicikl ?? "Nema podataka"}</strong>
                        </article>
                        <article className="stat-tile stat-tile-good">
                            <span className="stat-tile-label">Dostupno za izabrani period</span>
                            <strong className="stat-tile-value">{stats.brojDostupnihBicikala}</strong>
                        </article>
                        <article className="stat-tile stat-tile-bad">
                            <span className="stat-tile-label">Nedostupno</span>
                            <strong className="stat-tile-value">{stats.brojNedostupnihBicikala}</strong>
                        </article>
                    </section>

                    <section className="chart-card" aria-label="Zastupljenost bicikala u iznajmljivanjima">
                        <h2>Zastupljenost bicikala u iznajmljivanjima</h2>

                        {chartData.length === 0 ? (
                            <p className="empty-state">Još uvek nema iznajmljivanja za prikaz statistike.</p>
                        ) : (
                            <div className="chart-card-body">
                                <div className="chart-wrap">
                                    <ResponsiveContainer width="100%" height={340}>
                                        <PieChart>
                                            <Pie
                                                data={chartData}
                                                dataKey="broj"
                                                nameKey="naziv"
                                                cx="50%"
                                                cy="50%"
                                                outerRadius="72%"
                                                label={renderSliceLabel}
                                                labelLine={false}
                                            >
                                                {chartData.map((entry, index) => (
                                                    <Cell
                                                        key={entry.naziv}
                                                        fill={entry.naziv === "Ostalo" ? OTHER_COLOR : SLICE_COLORS[index % SLICE_COLORS.length]}
                                                        stroke="#ffffff"
                                                        strokeWidth={2}
                                                    />
                                                ))}
                                            </Pie>
                                            <Tooltip
                                                formatter={(value, name) => [`${value} iznajmljivanja`, name]}
                                                contentStyle={{
                                                    borderRadius: 12,
                                                    border: "1px solid rgba(30, 27, 75, 0.10)",
                                                    boxShadow: "0 10px 24px rgba(76, 29, 149, 0.16)"
                                                }}
                                            />
                                        </PieChart>
                                    </ResponsiveContainer>
                                </div>

                                <ul className="chart-legend-list">
                                    {stats.statistikaPoBiciklu.map((stavka, index) => (
                                        <li key={stavka.nazivBicikla + index}>
                                            <span
                                                className="chart-legend-dot"
                                                style={{background: index < MAX_SLICES ? SLICE_COLORS[index % SLICE_COLORS.length] : OTHER_COLOR}}
                                            />
                                            <span className="chart-legend-name">{stavka.nazivBicikla}</span>
                                            <span className="chart-legend-count">{stavka.brojIznajmljivanja}x · {stavka.procenat}%</span>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        )}
                    </section>
                </>
            )}
        </main>
    )
}

export default StatisticsPage

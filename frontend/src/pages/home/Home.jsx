import {useEffect, useMemo, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import {getBiciklImage} from "../../utils/biciklImages.js"
import BiciklModal from "./BiciklModal.jsx"
import EditBiciklModal from "../bicikl/EditBiciklModal.jsx"
import "./Home.css"
import "../bicikl/BiciklForm.css"

const todayIso = () => new Date().toISOString().slice(0, 10)
const tomorrowIso = () => {
    const date = new Date()
    date.setDate(date.getDate() + 1)
    return date.toISOString().slice(0, 10)
}

const SORT_OPTIONS = [
    {value: "dostupnost", label: "Dostupnost (dostupni prvo)"},
    {value: "proizvodjac", label: "Proizvođač (A-Ž)"},
    {value: "datum", label: "Datum nabavke (najnoviji prvo)"}
]

const Home = ({loggedInUser}) => {
    const [datumOd, setDatumOd] = useState(todayIso)
    const [datumDo, setDatumDo] = useState(tomorrowIso)
    const [bicikli, setBicikli] = useState([])
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(true)
    const [selectedBicikl, setSelectedBicikl] = useState(null)
    const [bookingMessage, setBookingMessage] = useState("")
    const [refreshToken, setRefreshToken] = useState(0)
    const [searchTerm, setSearchTerm] = useState("")
    const [editingBicikl, setEditingBicikl] = useState(null)
    const [sortBy, setSortBy] = useState("dostupnost")

    const isZaposleni = loggedInUser?.userType === "ZAPOSLENI"
    const rangeInvalid = datumOd && datumDo && datumDo <= datumOd

    useEffect(() => {
        if (rangeInvalid) {
            return
        }

        let cancelled = false

        const fetchBicikli = async () => {
            setLoading(true)
            setError("")
            try {
                const response = await fetch(apiUrl(`/api/bicikli?datumOd=${datumOd}&datumDo=${datumDo}`))
                if (!response.ok) {
                    if (!cancelled) setError("Bicikli ne mogu biti učitani")
                    return
                }
                const data = await response.json()
                if (!cancelled) setBicikli(data)
            } catch (error) {
                console.error("Error fetching bicikli:", error.message)
                if (!cancelled) setError("Bicikli ne mogu biti učitani")
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        void fetchBicikli()
        return () => {
            cancelled = true
        }
    }, [datumOd, datumDo, rangeInvalid, refreshToken])

    const handleDatumOdChange = (event) => {
        const value = event.target.value
        setDatumOd(value)
        if (datumDo && value && datumDo <= value) {
            const next = new Date(value)
            next.setDate(next.getDate() + 1)
            setDatumDo(next.toISOString().slice(0, 10))
        }
    }

    const brojDana = (() => {
        if (rangeInvalid || !datumOd || !datumDo) {
            return null
        }
        const diff = (new Date(datumDo) - new Date(datumOd)) / (1000 * 60 * 60 * 24)
        return Math.round(diff)
    })()

    const handleBookingComplete = () => {
        setSelectedBicikl(null)
        setBookingMessage("Iznajmljivanje je uspešno potvrđeno! Proverite email za PDF potvrdu.")
        setRefreshToken((token) => token + 1)
        window.setTimeout(() => setBookingMessage(""), 6000)
    }

    const filteredBicikli = useMemo(() => {
        const term = searchTerm.trim().toLowerCase()
        const filtered = term
            ? bicikli.filter((bicikl) => {
                const haystack = [
                    bicikl.proizvodjacNaziv,
                    bicikl.kategorijaNaziv,
                    bicikl.tipBicikle,
                    bicikl.velicinaRama,
                    bicikl.opis
                ].filter(Boolean).join(" ").toLowerCase()
                return haystack.includes(term)
            })
            : bicikli

        const sorted = [...filtered]

        switch (sortBy) {
            case "proizvodjac":
                sorted.sort((a, b) =>
                    (a.proizvodjacNaziv || "").localeCompare(b.proizvodjacNaziv || "", "sr")
                )
                break
            case "datum":
                sorted.sort((a, b) => {
                    const dateA = a.datumKupovine || ""
                    const dateB = b.datumKupovine || ""
                    return dateB.localeCompare(dateA)
                })
                break
            case "dostupnost":
            default:
                sorted.sort((a, b) => {
                    const aDostupan = a.dostupanZaPeriod ? 1 : 0
                    const bDostupan = b.dostupanZaPeriod ? 1 : 0
                    return bDostupan - aDostupan
                })
                break
        }

        return sorted
    }, [bicikli, searchTerm, sortBy])

    const handleEditSaved = () => {
        setEditingBicikl(null)
        setRefreshToken((token) => token + 1)
    }

    const handleDelete = async (event, bicikl) => {
        event.stopPropagation()
        const displayName = `${bicikl.proizvodjacNaziv ?? ""} ${bicikl.tipBicikle ?? ""}`.trim()
        if (!window.confirm(`Da li ste sigurni da želite da obrišete bicikl "${displayName}"?`)) {
            return
        }

        try {
            const response = await fetch(apiUrl(`/api/bicikli/${bicikl.id}`), {
                method: "DELETE",
                headers: {"Authorization": `Bearer ${loggedInUser.token}`}
            })
            if (!response.ok) {
                setError("Brisanje bicikle nije uspelo")
                return
            }
            setRefreshToken((token) => token + 1)
        } catch (error) {
            console.error("Error deleting bicikl:", error.message)
            setError("Brisanje bicikle nije uspelo")
        }
    }

    return (
        <main className="main-content">
            <h1 className="page-title">Ponuda bicikala</h1>
            <p className="page-subtitle">Pronađi i rezerviši bicikl za svoju sledeću vožnju.</p>

            <section className="filter-bar" aria-label="Filter perioda iznajmljivanja">
                <div className="filter-field">
                    <label htmlFor="datumOd">Datum početka</label>
                    <input
                        id="datumOd"
                        type="date"
                        value={datumOd}
                        onChange={handleDatumOdChange}
                    />
                </div>
                <div className="filter-field">
                    <label htmlFor="datumDo">Datum završetka</label>
                    <input
                        id="datumDo"
                        type="date"
                        min={datumOd}
                        value={datumDo}
                        onChange={(event) => setDatumDo(event.target.value)}
                    />
                </div>

                {rangeInvalid ? (
                    <p className="filter-error">Datum završetka mora biti posle datuma početka.</p>
                ) : (
                    brojDana !== null && (
                        <span className="filter-summary">
                            {brojDana} {brojDana === 1 ? "dan" : "dana"} iznajmljivanja
                        </span>
                    )
                )}
            </section>

            <section className="search-bar sort-bar" aria-label="Pretraga i sortiranje bicikala">
                <input
                    type="search"
                    className="search-input"
                    placeholder="Pretraži po nazivu, proizvođaču ili tipu..."
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                />

                <div className="sort-select-wrap">
                    <label htmlFor="sortBy">Sortiraj po</label>
                    <select id="sortBy" className="sort-select" value={sortBy} onChange={(event) => setSortBy(event.target.value)}>
                        {SORT_OPTIONS.map((option) => (
                            <option key={option.value} value={option.value}>{option.label}</option>
                        ))}
                    </select>
                </div>
            </section>

            {bookingMessage && <p className="verification-success">{bookingMessage}</p>}
            {error && <p className="error-banner">{error}</p>}

            {!error && !loading && filteredBicikli.length === 0 && (
                <p className="empty-state">
                    {bicikli.length === 0 ? "Trenutno nema bicikala u ponudi." : "Nema bicikala koji odgovaraju pretrazi."}
                </p>
            )}

            <section className="bicikl-grid" aria-label="Lista bicikala">
                {filteredBicikli.map((bicikl) => (
                    <article
                        className="bicikl-card"
                        key={bicikl.id}
                        onClick={() => setSelectedBicikl(bicikl)}
                    >
                        <div className="bicikl-card-art">
                            <img src={getBiciklImage(bicikl.kategorijaNaziv)} alt={bicikl.kategorijaNaziv || "Bicikl"}/>
                            {bicikl.kategorijaNaziv && (
                                <span className="bicikl-card-category">{bicikl.kategorijaNaziv}</span>
                            )}
                            <span className={`bicikl-card-badge ${bicikl.dostupanZaPeriod ? "available" : "unavailable"}`}>
                                {bicikl.dostupanZaPeriod ? "Dostupan" : "Nije dostupan"}
                            </span>
                        </div>

                        <div className="bicikl-card-body">
                            <p className="manufacturer">{bicikl.proizvodjacNaziv || "Nepoznat proizvođač"}</p>
                            <h2>{bicikl.tipBicikle ? `${bicikl.proizvodjacNaziv} · ${bicikl.tipBicikle}` : bicikl.proizvodjacNaziv}</h2>

                            {bicikl.opis && <p className="bicikl-card-desc">{bicikl.opis}</p>}

                            <div className="bicikl-spec-list">
                                {bicikl.velicinaRama && (
                                    <div>
                                        <span>Veličina rama</span>
                                        <strong>{bicikl.velicinaRama}</strong>
                                    </div>
                                )}
                                {bicikl.brojBrzina && (
                                    <div>
                                        <span>Broj brzina</span>
                                        <strong>{bicikl.brojBrzina}</strong>
                                    </div>
                                )}
                                {bicikl.tipKocnica && (
                                    <div>
                                        <span>Tip kočnica</span>
                                        <strong>{bicikl.tipKocnica}</strong>
                                    </div>
                                )}
                                {bicikl.precnikTocka && (
                                    <div>
                                        <span>Prečnik točka</span>
                                        <strong>{bicikl.precnikTocka}</strong>
                                    </div>
                                )}
                            </div>

                            <div className="bicikl-card-footer">
                                {bicikl.tezina ? (
                                    <span className="weight-chip">{bicikl.tezina}</span>
                                ) : <span/>}
                                {bicikl.napomena && <span className="note-text">{bicikl.napomena}</span>}
                            </div>

                            {isZaposleni && (
                                <div className="bicikl-card-manage">
                                    <button
                                        type="button"
                                        className="bicikl-card-edit-btn"
                                        onClick={(event) => {
                                            event.stopPropagation()
                                            setEditingBicikl(bicikl)
                                        }}
                                    >
                                        Izmeni
                                    </button>
                                    <button
                                        type="button"
                                        className="bicikl-card-delete-btn"
                                        onClick={(event) => handleDelete(event, bicikl)}
                                    >
                                        Obriši
                                    </button>
                                </div>
                            )}
                        </div>
                    </article>
                ))}
            </section>

            {selectedBicikl && (
                <BiciklModal
                    bicikl={selectedBicikl}
                    loggedInUser={loggedInUser}
                    onClose={() => setSelectedBicikl(null)}
                    onBookingComplete={handleBookingComplete}
                />
            )}

            {editingBicikl && (
                <EditBiciklModal
                    bicikl={editingBicikl}
                    loggedInUser={loggedInUser}
                    onClose={() => setEditingBicikl(null)}
                    onSaved={handleEditSaved}
                />
            )}
        </main>
    )
}

export default Home

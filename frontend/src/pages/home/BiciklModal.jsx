import {useEffect, useState} from "react"
import {useNavigate} from "react-router-dom"
import {apiUrl} from "../../api/apiConfig.js"
import {getBiciklImage} from "../../utils/biciklImages.js"

import OccupancyCalendar from "./OccupancyCalendar.jsx"
import PaymentModal from "./PaymentModal.jsx"

const BiciklModal = ({bicikl, loggedInUser, onClose, onBookingComplete}) => {
    const [zauzetost, setZauzetost] = useState([])
    const [selectedStart, setSelectedStart] = useState(null)
    const [selectedEnd, setSelectedEnd] = useState(null)
    const [showPayment, setShowPayment] = useState(false)

    const isKlijent = loggedInUser?.userType === "KLIJENT"
    const navigate = useNavigate()

    useEffect(() => {
        const fetchZauzetost = async () => {
            try {
                const response = await fetch(apiUrl(`/api/bicikli/${bicikl.id}/zauzetost`))
                if (!response.ok) {
                    return
                }
                setZauzetost(await response.json())
            } catch (error) {
                console.error("Error fetching zauzetost:", error.message)
            }
        }

        void fetchZauzetost()
    }, [bicikl.id])

    const handleSelect = (start, end) => {
        setSelectedStart(start)
        setSelectedEnd(end)
    }

    const handleBookingSuccess = (iznajmljivanje) => {
        setShowPayment(false)
        onBookingComplete(iznajmljivanje)
    }

    const canBook = isKlijent && selectedStart && selectedEnd

    return (
        <div className="modal-backdrop" role="presentation" onClick={onClose}>
            <section
                className="bicikl-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="bicikl-modal-title"
                onClick={(event) => event.stopPropagation()}
            >
                <button type="button" className="bicikl-modal-close" onClick={onClose} aria-label="Zatvori">×</button>

                <div className="bicikl-modal-art">
                    <img src={getBiciklImage(bicikl.kategorijaNaziv)} alt={bicikl.kategorijaNaziv || "bicikl"}/>
                    {bicikl.kategorijaNaziv && (
                        <span className="bicikl-card-category">{bicikl.kategorijaNaziv}</span>
                    )}
                    <span className={`bicikl-card-badge ${bicikl.dostupanZaPeriod ? "available" : "unavailable"}`}>
                        {bicikl.dostupanZaPeriod ? "Dostupan" : "Nije dostupan"}
                    </span>
                </div>

                <div className="bicikl-modal-body">
                    <div className="bicikl-modal-info">
                        <p className="manufacturer">{bicikl.proizvodjacNaziv || "Nepoznat proizvođač"}</p>
                        <h2 id="bicikl-modal-title">
                            {bicikl.tipBicikle ? `${bicikl.proizvodjacNaziv} · ${bicikl.tipBicikle}` : bicikl.proizvodjacNaziv}
                        </h2>

                        {bicikl.opis && <p className="description">{bicikl.opis}</p>}

                        <div className="bicikl-modal-specs">
                            {bicikl.tipBicikle && (
                                <div><span>Tip bicikle</span><strong>{bicikl.tipBicikle}</strong></div>
                            )}
                            {bicikl.velicinaRama && (
                                <div><span>Veličina rama</span><strong>{bicikl.velicinaRama}</strong></div>
                            )}
                            {bicikl.brojBrzina && (
                                <div><span>Broj brzina</span><strong>{bicikl.brojBrzina}</strong></div>
                            )}
                            {bicikl.tipKocnica && (
                                <div><span>Tip kočnica</span><strong>{bicikl.tipKocnica}</strong></div>
                            )}
                            {bicikl.precnikTocka && (
                                <div><span>Prečnik točka</span><strong>{bicikl.precnikTocka}</strong></div>
                            )}
                            {bicikl.tezina && (
                                <div><span>Težina</span><strong>{bicikl.tezina}</strong></div>
                            )}
                            {bicikl.datumKupovine && (
                                <div><span>Datum nabavke</span><strong>{new Date(bicikl.datumKupovine).toLocaleDateString("sr-Latn-RS")}</strong></div>
                            )}
                            <div><span>Status</span><strong>{bicikl.dostupan ? "U ponudi" : "Van upotrebe"}</strong></div>
                            {bicikl.napomena && (
                                <div><span>Napomena</span><strong>{bicikl.napomena}</strong></div>
                            )}
                        </div>

                        <button
                            type="button"
                            className="reviews-link-button"
                            onClick={() => navigate(`/bicikli/${bicikl.id}/recenzije`)}
                        >
                            Vidi recenzije
                        </button>
                    </div>

                    <div className="bicikl-modal-booking">
                        <h3>Kalendar dostupnosti</h3>
                        <OccupancyCalendar
                            zauzetost={zauzetost}
                            readOnly={!isKlijent}
                            selectedStart={selectedStart}
                            selectedEnd={selectedEnd}
                            onSelect={handleSelect}
                        />

                        {!loggedInUser && (
                            <p className="calendar-hint guest">
                                Prijavite se kao klijent da biste mogli da izaberete period i iznajmite bicikl.
                            </p>
                        )}
                        {loggedInUser && !isKlijent && (
                            <p className="calendar-hint guest">
                                Iznajmljivanje je dostupno samo prijavljenim klijentima.
                            </p>
                        )}
                        {isKlijent && (
                            <p className="calendar-hint">
                                {selectedStart && selectedEnd
                                    ? `Izabrani period: ${selectedStart} - ${selectedEnd}`
                                    : "Izaberite datum početka i datum završetka na kalendaru."}
                            </p>
                        )}

                        {isKlijent && (
                            <button type="button" className="book-button" disabled={!canBook || !bicikl.dostupan} onClick={() => setShowPayment(true)} > Iznajmi </button>

                        )}
                    </div>
                </div>
            </section>

            {showPayment && (
                <PaymentModal
                    bicikl={bicikl}
                    datumOd={selectedStart}
                    datumDo={selectedEnd}
                    loggedInUser={loggedInUser}
                    onClose={() => setShowPayment(false)}
                    onSuccess={handleBookingSuccess}
                />
            )}
        </div>
    )
}

export default BiciklModal

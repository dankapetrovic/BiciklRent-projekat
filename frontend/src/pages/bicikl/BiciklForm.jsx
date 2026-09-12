import {useEffect, useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"

const emptyForm = {
    proizvodjacNaziv: "",
    kategorijaId: "",
    datumKupovine: "",
    napomena: "",
    dostupan: true,
    tipBicikle: "",
    velicinaRama: "",
    brojBrzina: "",
    tipKocnica: "",
    precnikTocka: "",
    tezina: "",
    opis: ""
}

const toFormValues = (bicikl) => {
    if (!bicikl) {
        return emptyForm
    }
    return {
        proizvodjacNaziv: bicikl.proizvodjacNaziv ?? "",
        kategorijaId: bicikl.kategorijaId ?? "",
        datumKupovine: bicikl.datumKupovine ?? "",
        napomena: bicikl.napomena ?? "",
        dostupan: bicikl.dostupan ?? true,
        tipBicikle: bicikl.tipBicikle ?? "",
        velicinaRama: bicikl.velicinaRama ?? "",
        brojBrzina: bicikl.brojBrzina ?? "",
        tipKocnica: bicikl.tipKocnica ?? "",
        precnikTocka: bicikl.precnikTocka ?? "",
        tezina: bicikl.tezina ?? "",
        opis: bicikl.opis ?? ""
    }
}

const BiciklForm = ({bicikl, submitLabel, isSubmitting, fieldErrors, onSubmit, onCancel}) => {
    const [formData, setFormData] = useState(() => toFormValues(bicikl))
    const [kategorije, setKategorije] = useState([])

    useEffect(() => {
        const fetchKategorije = async () => {
            try {
                const response = await fetch(apiUrl("/api/kategorije"))
                if (!response.ok) {
                    return
                }
                setKategorije(await response.json())
            } catch (error) {
                console.error("Error fetching kategorije:", error.message)
            }
        }

        void fetchKategorije()
    }, [])

    const handleChange = (event) => {
        const {name, value, type, checked} = event.target
        setFormData({...formData, [name]: type === "checkbox" ? checked : value})
    }

    const handleSubmit = (event) => {
        event.preventDefault()
        onSubmit({
            ...formData,
            kategorijaId: formData.kategorijaId === "" ? null : Number(formData.kategorijaId),
            datumKupovine: formData.datumKupovine === "" ? null : formData.datumKupovine
        })
    }

    return (
        <form className="bicikl-form" onSubmit={handleSubmit}>
            <div className="bicikl-form-grid">
                <div className="auth-field">
                    <label htmlFor="proizvodjacNaziv">Proizvođač</label>
                    <input
                        id="proizvodjacNaziv"
                        name="proizvodjacNaziv"
                        type="text"
                        placeholder="npr. Trek"
                        value={formData.proizvodjacNaziv}
                        onChange={handleChange}
                    />
                    {fieldErrors?.proizvodjacNaziv && <p className="field-error">{fieldErrors.proizvodjacNaziv}</p>}
                </div>

                <div className="auth-field">
                    <label htmlFor="kategorijaId">Kategorija</label>
                    <select id="kategorijaId" name="kategorijaId" value={formData.kategorijaId} onChange={handleChange}>
                        <option value="">Izaberite kategoriju</option>
                        {kategorije.map((kategorija) => (
                            <option key={kategorija.id} value={kategorija.id}>{kategorija.naziv}</option>
                        ))}
                    </select>
                    {fieldErrors?.kategorijaId && <p className="field-error">{fieldErrors.kategorijaId}</p>}
                </div>

                <div className="auth-field">
                    <label htmlFor="tipBicikle">Tip bicikle</label>
                    <input id="tipBicikle" name="tipBicikle" type="text" placeholder="npr. Planinski (MTB)" value={formData.tipBicikle} onChange={handleChange}/>
                </div>

                <div className="auth-field">
                    <label htmlFor="velicinaRama">Veličina rama</label>
                    <input id="velicinaRama" name="velicinaRama" type="text" placeholder="npr. L / 50cm" value={formData.velicinaRama} onChange={handleChange}/>
                </div>

                <div className="auth-field">
                    <label htmlFor="brojBrzina">Broj brzina</label>
                    <input id="brojBrzina" name="brojBrzina" type="text" placeholder="npr. 21 brzina" value={formData.brojBrzina} onChange={handleChange}/>
                </div>

                <div className="auth-field">
                    <label htmlFor="tipKocnica">Tip kočnica</label>
                    <input id="tipKocnica" name="tipKocnica" type="text" placeholder="npr. Hidraulične disk kočnice" value={formData.tipKocnica} onChange={handleChange}/>
                </div>

                <div className="auth-field">
                    <label htmlFor="precnikTocka">Prečnik točka</label>
                    <input id="precnikTocka" name="precnikTocka" type="text" placeholder="npr. 29 inča" value={formData.precnikTocka} onChange={handleChange}/>
                </div>

                <div className="auth-field">
                    <label htmlFor="tezina">Težina</label>
                    <input id="tezina" name="tezina" type="text" placeholder="npr. 14.2 kg" value={formData.tezina} onChange={handleChange}/>
                </div>

                <div className="auth-field">
                    <label htmlFor="datumKupovine">Datum nabavke</label>
                    <input id="datumKupovine" name="datumKupovine" type="date" value={formData.datumKupovine} onChange={handleChange}/>
                </div>
            </div>

            <div className="auth-field">
                <label htmlFor="napomena">Napomena</label>
                <input id="napomena" name="napomena" type="text" placeholder="Interna napomena o stanju bicikle" value={formData.napomena} onChange={handleChange}/>
            </div>

            <div className="auth-field">
                <label htmlFor="opis">Specifikacije / opis</label>
                <textarea id="opis" name="opis" rows="4" placeholder="Detaljan opis bicikle i opreme" value={formData.opis} onChange={handleChange}/>
            </div>

            <div className="bicikl-form-toggles">
                <label className="bicikl-form-checkbox">
                    <input type="checkbox" name="dostupan" checked={formData.dostupan} onChange={handleChange}/>
                    Dostupan u ponudi
                </label>
            </div>

            {fieldErrors?.form && <p className="form-error">{fieldErrors.form}</p>}

            <div className="verification-actions">
                <button type="button" className="btn-secondary" onClick={onCancel} disabled={isSubmitting}>
                    Otkaži
                </button>
                <button type="submit" className="auth-submit" disabled={isSubmitting} style={{width: "auto", padding: "0 20px"}}>
                    {isSubmitting ? "Čuvanje..." : submitLabel}
                </button>
            </div>
        </form>
    )
}

export default BiciklForm
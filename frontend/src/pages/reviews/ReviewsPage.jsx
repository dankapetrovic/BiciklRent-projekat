import{ useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { apiUrl } from '../../api/apiConfig.js';
import './ReviewsPage.css';

function StarDisplay({ ocena }) {
    return (
        <span className="star-display">
      {[1, 2, 3, 4, 5].map((n) => (
          <span key={n} className={n <= ocena ? 'star filled' : 'star'}>★</span>
      ))}
    </span>
    );
}

function StarInput({ value, onChange }) {
    return (
        <span className="star-input">
      {[1, 2, 3, 4, 5].map((n) => (
          <span
              key={n}
              className={n <= value ? 'star filled' : 'star'}
              onClick={() => onChange(n)}
          >
          ★
        </span>
      ))}
    </span>
    );
}

export default function ReviewsPage({ loggedInUser }) {
    const { id: biciklId } = useParams();
    const [recenzije, setRecenzije] = useState([]);
    const [prosek, setProsek] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [ocena, setOcena] = useState(0);
    const [komentar, setKomentar] = useState('');
    const [submitting, setSubmitting] = useState(false);

    const token = loggedInUser?.token;
    const userType = loggedInUser?.userType; // 'KLIJENT' ili 'ZAPOSLENI'

    useEffect(() => {
        fetchRecenzije();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [biciklId]);

    async function safeJson(res) {
        const text = await res.text();
        return text ? JSON.parse(text) : null;
    }

    async function fetchRecenzije() {
        setLoading(true);
        setError('');
        try {
            const recRes = await fetch(apiUrl(`/api/bicikli/${biciklId}/recenzije`));
            if (!recRes.ok) {
                throw new Error(`Server je vratio grešku ${recRes.status} na /recenzije`);
            }
            setRecenzije((await safeJson(recRes)) || []);

            const prosekRes = await fetch(apiUrl(`/api/bicikli/${biciklId}/recenzije/prosek`));
            if (!prosekRes.ok) {
                throw new Error(`Server je vratio grešku ${prosekRes.status} na /recenzije/prosek`);
            }
            setProsek(await safeJson(prosekRes));
        } catch (err) {
            if (err instanceof TypeError) {
                setError(
                    'Ne mogu da se povežem sa serverom. Proveri da li je backend pokrenut i da li je adresa u apiConfig.js tačna.'
                );
            } else {
                setError(err.message);
            }
        } finally {
            setLoading(false);
        }
    }

    async function handleSubmit(e) {
        e.preventDefault();
        if (ocena === 0) {
            setError('Izaberite ocenu od 1 do 5.');
            return;
        }
        setSubmitting(true);
        setError('');
        try {
            const res = await fetch(apiUrl(`/api/bicikli/${biciklId}/recenzije`), {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ ocena, komentar }),
            });
            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || `Server je vratio grešku ${res.status}`);
            }      setOcena(0);
            setKomentar('');
            fetchRecenzije();
        } catch (err) {
            setError(err.message || 'Slanje recenzije nije uspelo. Pokušajte ponovo.');
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div className="reviews-page">
            <h2>Recenzije bicikla</h2>

            {prosek != null && (
                <div className="prosek-ocena">
                    <StarDisplay ocena={Math.round(prosek)} />
                    <span className="prosek-broj">{prosek.toFixed(1)} / 5</span>
                    <span className="broj-recenzija">({recenzije.length} recenzija)</span>
                </div>
            )}

            {userType === 'KLIJENT' && (
                <form className="review-form" onSubmit={handleSubmit}>
                    <label>Vaša ocena:</label>
                    <StarInput value={ocena} onChange={setOcena} />
                    <textarea
                        placeholder="Napišite komentar (opciono)..."
                        value={komentar}
                        onChange={(e) => setKomentar(e.target.value)}
                        maxLength={1000}
                    />
                    <button type="submit" disabled={submitting}>
                        {submitting ? 'Slanje...' : 'Pošalji recenziju'}
                    </button>
                </form>
            )}

            {error && <p className="review-error">{error}</p>}

            {loading ? (
                <p>Učitavanje...</p>
            ) : recenzije.length === 0 ? (
                <p className="no-reviews">Još uvek nema recenzija za ovaj bicikl.</p>
            ) : (
                <ul className="reviews-list">
                    {recenzije.map((r) => (
                        <li key={r.id} className="review-item">
                            <div className="review-header">
                                <StarDisplay ocena={r.ocena} />
                                <span className="review-author">
                  {r.klijentIme} {r.klijentPrezime}
                </span>
                                <span className="review-date">{r.datumKreiranja}</span>
                            </div>
                            {r.komentar && <p className="review-comment">{r.komentar}</p>}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}
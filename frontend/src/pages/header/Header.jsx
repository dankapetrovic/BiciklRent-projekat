import {Container, Navbar} from "react-bootstrap"
import {Link, useNavigate} from "react-router-dom"
import "./Header.css"

const Header = ({loggedInUser, onLogout}) => {
    const navigate = useNavigate()
    const displayName = loggedInUser ? `${loggedInUser.ime ?? ""} ${loggedInUser.prezime ?? ""}`.trim() : ""
    const roleLabel = loggedInUser?.userType === "ZAPOSLENI" ? "Zaposleni" : "Klijent"

    const handleLogout = () => {
        onLogout()
        navigate("/")
    }

    return (
        <Navbar expand="lg" className="fr-navbar">
            <Container>
                <Navbar.Brand as={Link} to="/" className="fr-brand">
                    <span className="fr-brand-icon" aria-hidden="true">
    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="6" cy="17" r="3" stroke="currentColor" strokeWidth="1.6"/>
        <circle cx="18" cy="17" r="3" stroke="currentColor" strokeWidth="1.6"/>
        <path d="M6 17L10 8H14L12 12M18 17L14 8M10 8H8M12 12H16.5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
</span>
                    BiciklRent
                </Navbar.Brand>

                <div className="ms-auto fr-nav-links">
                    {!loggedInUser && (
                        <Link className="fr-login-btn" to="/login">
                            Prijava
                        </Link>
                    )}

                    {loggedInUser?.userType === "KLIJENT" && (
                        <Link className="fr-login-btn" to="/moja-iznajmljivanja">
                            Moja iznajmljivanja
                        </Link>
                    )}

                    {loggedInUser?.userType === "ZAPOSLENI" && (
                        <Link className="fr-login-btn" to="/dodaj-aparat">
                            Dodaj bicikl
                        </Link>
                    )}

                    {loggedInUser?.userType === "ZAPOSLENI" && (
                        <Link className="fr-login-btn" to="/klijenti">
                            Prikaz svih klijenata
                        </Link>
                    )}

                    {loggedInUser?.userType === "ZAPOSLENI" && (
                        <Link className="fr-login-btn" to="/statistika">
                            Statistika
                        </Link>
                    )}

                    {loggedInUser && (
                        <div className="fr-user-summary">
                            <span className="fr-user-avatar" aria-hidden="true">
                                {displayName.charAt(0) || loggedInUser.username?.charAt(0) || "K"}
                            </span>
                            <span className="fr-user-meta">
                                <span className="fr-user-name">{displayName || loggedInUser.username}</span>
                                <span className="fr-user-role">{roleLabel}</span>
                            </span>
                            <button type="button" className="fr-logout-btn" onClick={handleLogout}>
                                Odjava
                            </button>
                        </div>
                    )}
                </div>
            </Container>
        </Navbar>
    )
}

export default Header

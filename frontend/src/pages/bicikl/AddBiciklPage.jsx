import {useState} from "react"
import {useNavigate} from "react-router-dom"
import {apiUrl} from "../../api/apiConfig.js"
import BiciklForm from "./BiciklForm.jsx"
import "./BiciklForm.css"

const AddBiciklPage = ({loggedInUser}) => {
    const navigate = useNavigate()
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [fieldErrors, setFieldErrors] = useState({})

    const handleSubmit = async (payload) => {
        setIsSubmitting(true)
        setFieldErrors({})

        try {
            const response = await fetch(apiUrl("/api/bicikli"), {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify(payload)
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Dodavanje bicikla nije uspelo"})
                return
            }

            navigate("/")
        } catch (error) {
            console.error("Error creating bicikl:", error.message)
            setFieldErrors({form: "Dodavanje bicikla nije uspelo"})
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <main className="main-content bicikl-form-page">
            <h1 className="page-title">Dodaj bicikl</h1>
            <p className="page-subtitle">Unesite podatke o novom biciklu koji ulazi u ponudu.</p>

            <div className="auth-card bicikl-form-card">
                <BiciklForm
                    bicikl={null}
                    submitLabel="Dodaj bicikl"
                    isSubmitting={isSubmitting}
                    fieldErrors={fieldErrors}
                    onSubmit={handleSubmit}
                    onCancel={() => navigate("/")}
                />
            </div>
        </main>
    )
}

export default AddBiciklPage

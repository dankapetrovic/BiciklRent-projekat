import {useState} from "react"
import {apiUrl} from "../../api/apiConfig.js"
import BiciklForm from "./BiciklForm.jsx"
import "./BiciklForm.css"

const EditBiciklModal = ({bicikl, loggedInUser, onClose, onSaved}) => {
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [fieldErrors, setFieldErrors] = useState({})

    const handleSubmit = async (payload) => {
        setIsSubmitting(true)
        setFieldErrors({})

        try {
            const response = await fetch(apiUrl(`/api/bicikli/${bicikl.id}`), {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${loggedInUser.token}`
                },
                body: JSON.stringify(payload)
            })

            if (!response.ok) {
                const errorData = await response.json().catch(() => null)
                setFieldErrors(errorData?.fieldErrors ?? {form: "Izmena bicikla nije uspela"})
                return
            }

            onSaved()
        } catch (error) {
            console.error("Error updating bicikl:", error.message)
            setFieldErrors({form: "Izmena bicikla nije uspela"})
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <div className="modal-backdrop payment-backdrop" role="presentation" onClick={onClose}>
            <section
                className="bicikl-form-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="edit-bicikl-title"
                onClick={(event) => event.stopPropagation()}
            >
                <h2 id="edit-bicikl-title">Izmeni bicikl</h2>
                <BiciklForm
                    bicikl={bicikl}
                    submitLabel="Sačuvaj izmene"
                    isSubmitting={isSubmitting}
                    fieldErrors={fieldErrors}
                    onSubmit={handleSubmit}
                    onCancel={onClose}
                />
            </section>
        </div>
    )
}

export default EditBiciklModal

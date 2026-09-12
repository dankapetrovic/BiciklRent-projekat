import drumski from "../assets/bicikl/drumski.svg"
import planinski from "../assets/bicikl/planinski.svg"
import gradski from "../assets/bicikl/gradski.svg"
import elektricni from "../assets/bicikl/elektricni.svg"
import gravel from "../assets/bicikl/gravel.svg"
import deciji from "../assets/bicikl/deciji.svg"

const CATEGORY_IMAGES = {
    "Drumski": drumski,
    "Planinski": planinski,
    "Gradski": gradski,
    "Električni": elektricni,
    "Dečiji": deciji,
    "Gravel": gravel,
}

export const getBiciklImage = (kategorijaNaziv) => CATEGORY_IMAGES[kategorijaNaziv] || deciji

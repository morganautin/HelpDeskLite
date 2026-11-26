package fr.estiam.helpdesklite.model

object TicketValidation {
    // Exercice 1 — Un titre est valide s'il contient au moins un caractère qui n'est pas un espace.
    // isNotBlank() renvoie false pour "", " " ou " \t\n" (espaces, tabulations, retours à la ligne).
    fun isTitleValid(title: String): Boolean = title.isNotBlank()

    // Exercice 1 — La description doit faire au moins 10 caractères une fois les espaces
    // du début et de la fin retirés (trim). Les espaces au milieu du texte comptent.
    fun isDescriptionValid(description: String): Boolean = description.trim().length >= MIN_DESCRIPTION_LENGTH

    // Exercice 1 — Fabrique un ticket à partir de la saisie du formulaire.
    fun create(
        id: String,
        title: String,
        description: String,
        priority: Priority,
        createdAt: Long,
    ): SupportTicket? {
        // 1. Saisie invalide → pas de ticket (null).
        if (!isTitleValid(title) || !isDescriptionValid(description)) return null
        // 2. Saisie valide → textes nettoyés (trim), statut de départ OPEN,
        //    et l'id, la priorité et la date reçus sont repris tels quels.
        return SupportTicket(
            id = id,
            title = title.trim(),
            description = description.trim(),
            priority = priority,
            status = Status.OPEN,
            createdAt = createdAt,
        )
    }

    private const val MIN_DESCRIPTION_LENGTH = 10
}

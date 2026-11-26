package fr.estiam.helpdesklite.model

object TicketRules {
    // Exercice 2 — Seules deux transitions sont autorisées, toujours vers l'avant :
    //   OPEN → IN_PROGRESS   et   IN_PROGRESS → RESOLVED.
    // Tout le reste est refusé : retour en arrière, saut d'étape (OPEN → RESOLVED)
    // et "transition" vers le même statut.
    fun canTransition(from: Status, to: Status): Boolean = when (from) {
        Status.OPEN -> to == Status.IN_PROGRESS
        Status.IN_PROGRESS -> to == Status.RESOLVED
        Status.RESOLVED -> false
    }

    // Exercice 2 — Trie le catalogue : HIGH d'abord, puis MEDIUM, puis LOW.
    // - sortedBy() renvoie une NOUVELLE liste : la liste reçue n'est pas modifiée.
    // - sortedBy() est un tri STABLE : deux tickets de même priorité gardent leur ordre d'origine.
    // - Aucun ticket n'est perdu ni ajouté.
    fun sortByPriority(tickets: List<SupportTicket>): List<SupportTicket> =
        tickets.sortedBy { rank(it.priority) }

    // Rang de chaque priorité (0 = en premier). On l'écrit explicitement plutôt que d'utiliser
    // l'ordre de déclaration de l'enum (LOW, MEDIUM, HIGH), pour ne pas dépendre de cet ordre.
    private fun rank(priority: Priority): Int = when (priority) {
        Priority.HIGH -> 0
        Priority.MEDIUM -> 1
        Priority.LOW -> 2
    }
}

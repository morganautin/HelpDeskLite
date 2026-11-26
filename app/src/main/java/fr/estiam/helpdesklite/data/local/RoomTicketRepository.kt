package fr.estiam.helpdesklite.data.local

import fr.estiam.helpdesklite.data.TicketRepository
import fr.estiam.helpdesklite.model.Status
import fr.estiam.helpdesklite.model.SupportTicket
import fr.estiam.helpdesklite.model.TicketRules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTicketRepository(
    private val dao: TicketDao,
    private val canTransition: (Status, Status) -> Boolean = TicketRules::canTransition,
) : TicketRepository {
    override suspend fun add(ticket: SupportTicket) = dao.insert(ticket.toRow())

    // Exercice 3 — Cherche la ligne en base puis la convertit en ticket du modèle.
    // Si aucune ligne n'a cet id, le DAO renvoie null et "?." renvoie null aussi.
    override suspend fun findById(id: String): SupportTicket? = dao.findById(id)?.toTicket()

    // Exercice 3 — Change le statut d'un ticket. Renvoie true seulement si la base a bien été modifiée.
    override suspend fun updateStatus(id: String, status: Status): Boolean {
        // 1. Lire le ticket actuel : id inconnu → false.
        val current = dao.findById(id) ?: return false
        // 2. Vérifier la règle reçue dans le constructeur (canTransition) : transition interdite → false.
        if (!canTransition(current.status, status)) return false
        // 3. Mise à jour en base. La requête ne modifie QUE la colonne status, et seulement si le
        //    statut n'a pas changé entre-temps (WHERE status = previous). Elle renvoie le nombre de
        //    lignes modifiées : 0 → échec (false), 1 → succès (true). Les autres champs et les
        //    autres tickets ne sont pas touchés.
        return dao.updateStatus(id, previous = current.status, next = status) > 0
    }

    override fun observeAll() = dao.observeAll().map { rows -> rows.map(TicketRow::toTicket) }

    // Exercice 3 — Flow Room filtré par statut : il émet de nouveau à chaque changement en base.
    // L'ordre vient de la requête du DAO (plus récent d'abord, puis id croissant).
    // Si aucun ticket ne correspond, Room émet une liste vide.
    // map : chaque ligne Room (TicketRow) est convertie en ticket du modèle (SupportTicket).
    override fun observeByStatus(status: Status): Flow<List<SupportTicket>> =
        dao.observeByStatus(status).map { rows -> rows.map(TicketRow::toTicket) }
}

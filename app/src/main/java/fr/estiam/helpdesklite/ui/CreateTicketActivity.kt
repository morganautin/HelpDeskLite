package fr.estiam.helpdesklite.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import fr.estiam.helpdesklite.HelpDeskApplication
import fr.estiam.helpdesklite.R
import fr.estiam.helpdesklite.databinding.ActivityCreateTicketBinding
import fr.estiam.helpdesklite.model.Priority
import fr.estiam.helpdesklite.model.TicketValidation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.UUID

class CreateTicketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTicketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemPadding()
        binding.priorityInput.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, Priority.entries.map { it.name },
        )
        if (savedInstanceState == null) binding.priorityInput.setSelection(Priority.MEDIUM.ordinal)
        binding.saveTicket.setOnClickListener { saveTicket() }
    }

    private fun saveTicket() {
        val title = binding.titleInput.text.toString()
        val description = binding.descriptionInput.text.toString()
        val priority = Priority.entries[binding.priorityInput.selectedItemPosition]
        binding.titleInput.error = if (TicketValidation.isTitleValid(title)) null
        else getString(R.string.title_error)
        binding.descriptionInput.error = if (TicketValidation.isDescriptionValid(description)) null
        else getString(R.string.description_error)
        binding.saveError.text = ""
        val ticket = TicketValidation.create(
            UUID.randomUUID().toString(), title, description, priority, System.currentTimeMillis(),
        ) ?: return

        binding.saveTicket.isEnabled = false
        lifecycleScope.launch {
            try {
                // Exercice 4 — Enregistrer le ticket via le repository de l'application
                // (Room en temps normal, base en mémoire pendant les tests).
                // add() est une fonction suspend : la ligne suivante ne s'exécute
                // qu'une fois l'insertion terminée avec succès.
                (application as HelpDeskApplication).tickets.add(ticket)
                // Insertion réussie → on ferme le formulaire et on revient au catalogue.
                // Le bouton reste désactivé : impossible d'enregistrer deux fois le même ticket.
                finish()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                binding.saveError.setText(R.string.save_error)
                binding.saveTicket.isEnabled = true
            }
        }
    }
}

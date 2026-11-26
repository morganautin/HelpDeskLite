package fr.estiam.helpdesklite

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TicketJourneyTest {
    // La fixture fournit une base Room vide en mémoire et lance le catalogue (MainActivity).
    @get:Rule val fixture = TicketUiFixture()

    // Exercice 5 — Parcours complet : créer un ticket de priorité LOW et le retrouver dans le catalogue.
    @Test fun createsLowPriorityTicket() {
        val title = "Keyboard in lab B"
        val description = "Several keys stop responding after a few minutes."

        // 1. Ouvrir le formulaire depuis le catalogue.
        onView(withId(R.id.addTicket)).perform(click())

        // 2. Saisir le titre et une description valide (au moins 10 caractères).
        onView(withId(R.id.titleInput)).perform(replaceText(title), closeSoftKeyboard())
        onView(withId(R.id.descriptionInput)).perform(replaceText(description), closeSoftKeyboard())

        // 3. Choisir explicitement LOW dans la liste déroulante (le formulaire propose MEDIUM par défaut).
        onView(withId(R.id.priorityInput)).perform(scrollTo(), click())
        onData(equalTo<Any>("LOW")).perform(click())

        // 4. Enregistrer.
        onView(withId(R.id.saveTicket)).perform(scrollTo(), click())

        // 5. Attendre que le catalogue affiche la ligne (pas de Thread.sleep :
        //    la fixture attend la mise à jour de la RecyclerView, 10 s maximum).
        fixture.awaitCatalogueRows()

        // 6. Retrouver LA ligne qui contient ce titre (recherche par contenu, pas par position),
        //    puis vérifier que, dans cette même ligne, la priorité affichée est LOW.
        val row = allOf(withId(R.id.ticketRow), hasDescendant(withText(title)))
        onView(withId(R.id.ticketList)).perform(
            RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(row),
        )
        onView(row).check(matches(allOf(
            isDisplayed(),
            hasDescendant(allOf(withId(R.id.ticketTitle), withText(title))),
            hasDescendant(allOf(withId(R.id.ticketPriority), withText("LOW"))),
        )))
    }
}

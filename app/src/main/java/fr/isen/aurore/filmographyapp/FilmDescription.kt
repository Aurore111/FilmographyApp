package fr.isen.aurore.filmographyapp

import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.snapshots.SnapshotStateList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmDescription(modifier: Modifier, showBackButton: Boolean = false)
{
    val context = LocalContext.current
    val film = mapOf(
        "title" to "Star Wars: A New Hope",
        "universe" to "Star Wars",
        "category" to "Skywalker Saga",
        "release_date" to "25 mai 1977",
        "description" to "Luke Skywalker, un jeune fermier de la planète Tatooine, se retrouve mêlé à une guerre galactique après avoir découvert un message caché dans un droïde. Accompagné du mystérieux Obi-Wan Kenobi, du contrebandier Han Solo et de la Princesse Leia, il devra affronter l'Empire et l'implacable Dark Vador.",
        "poster" to "mettre lien image"
    )

    // Statuts possibles
    val statuses = listOf("Vu", "À voir", "Possède en DVD/Blu-Ray", "Veut s'en débarrasser")
    var selectedStatuses = remember { mutableStateListOf<String?>() }

    //utilisateur qui possedent le fil, donné brute
    val owners = listOf("Alice", "Bob", "Charlie")
    val wantToSell = listOf("Bob")

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = film["title"] ?: "",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3E2723)
                    )
                },
                navigationIcon = {   //fleche retour à la page précédente
                   if(showBackButton) {
                       IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                           Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                       }
                   }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFFCCFA4))
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Poster image du film
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                )
            }
            item {
                Card (
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(film["title"] ?: "", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF3E2723))
                        Text("Univers : ${film["universe"]}", fontSize = 16.sp, color = Color(0xFF5D4037))
                        Text("Catégorie : ${film["category"]}", fontSize = 16.sp, color = Color(0xFF5D4037))
                        Text("Sortie : ${film["release_date"]}", fontSize = 16.sp, color = Color(0xFF5D4037))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(film["description"] ?: "", fontSize = 16.sp, color = Color(0xFF4E342E))
                    }
                }
            }

            // statut utilisateur
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Column (modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Mon statut", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF3E2723))
                        statuses.forEach { status ->
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = selectedStatuses.contains(status),
                                    onCheckedChange = {  //on ne peut pas coché a "vu" et "a voir" en meme temps
                                        if (it) {
                                            if (status == "Vu") selectedStatuses.remove("À voir")
                                            if (status == "À voir") selectedStatuses.remove("Vu")
                                            selectedStatuses.add(status)
                                        }
                                        else selectedStatuses.remove(status)
                                    }
                                )
                                Text(text = status, fontSize = 16.sp, color = Color(0xFF5D4037))
                            }
                        }
                    }
                }
            }
            //partie utilisateur possede et veut vendre son film
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Column (modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Utilisateurs", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF3E2723))

                        Text("Possèdent ce film :", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF5D4037))
                        owners.forEach { user ->
                            Text("• $user", fontSize = 16.sp, color = Color(0xFF4E342E))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("Veulent s'en débarrasser :", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF5D4037))
                        wantToSell.forEach { user ->
                            Text("• $user", fontSize = 16.sp, color = Color(0xFF4E342E))
                        }
                    }
                }
            }
        }
    }
}
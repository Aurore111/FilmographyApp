package fr.isen.aurore.filmographyapp

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmDescription(modifier: Modifier, filmTitle: String?, showBackButton: Boolean = true) {

    val context = LocalContext.current

    var description by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var annee by remember { mutableStateOf("") }

    val database = FirebaseDatabase.getInstance(
        "https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app"
    )

    val ref = database.getReference("categories")

    LaunchedEffect(filmTitle) {

        ref.get().addOnSuccessListener { snapshot ->

            snapshot.children.forEach { category ->

                category.child("franchises").children.forEach { franchise ->

                    franchise.child("films").children.forEach { film ->

                        val titre = film.child("titre").value.toString()

                        if (filmTitle == null || titre.contains(filmTitle)) {

                            description = film.child("description").value.toString()
                            genre = film.child("genre").value.toString()
                            annee = film.child("annee").value.toString()

                        }

                    }

                }

            }

        }

    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = filmTitle ?: "",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3E2723)
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = {
                            (context as? ComponentActivity)?.finish()
                        }) {
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

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            filmTitle ?: "",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        )

                        Text("Année : $annee", fontSize = 16.sp)

                        Text("Genre : $genre", fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(description, fontSize = 16.sp)

                    }

                }

            }

        }

    }

}
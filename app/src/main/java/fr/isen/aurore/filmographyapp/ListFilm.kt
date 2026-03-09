package fr.isen.aurore.filmographyapp

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListFilm(modifier: Modifier, categoryName: String?) {

    val context = LocalContext.current
    val films = remember { mutableStateListOf<String>() }

    val database = FirebaseDatabase.getInstance("https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app")
    val ref = database.getReference("categories")

    LaunchedEffect(categoryName) {

        ref.get().addOnSuccessListener { snapshot ->

            films.clear()

            snapshot.children.forEach { category ->

                val nomCategorie = category.child("category").value.toString()

                if (nomCategorie == categoryName) {

                    category.child("franchises").children.forEach { franchise ->

                        // films avec sous sagas
                        franchise.child("sous_sagas").children.forEach { saga ->

                            saga.child("films").children.forEach { film ->

                                val titre = film.child("titre").value.toString()
                                films.add(titre)

                            }

                        }

                        // films sans sous saga
                        franchise.child("films").children.forEach { film ->

                            val titre = film.child("titre").value.toString()
                            films.add(titre)

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
                        text = categoryName ?: "Films",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3E2723)
                    )
                },

                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }

            )
        }

    ) { innerPadding ->

        LazyVerticalGrid(

            columns = GridCells.Fixed(2),

            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFFCCFA4))
                .padding(innerPadding)
                .padding(16.dp),

            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {

            items(films) { film ->

                Button(

                    onClick = {
                        val intent = Intent(context, FilmDescriptionActivity::class.java)
                        intent.putExtra("Film", film)
                        context.startActivity(intent)
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f),

                    shape = RoundedCornerShape(20.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.8f),
                        contentColor = Color(0xFF5D4037)
                    )

                ) {

                    Text(
                        text = film,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                }

            }

        }

    }

}
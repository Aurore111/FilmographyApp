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
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import fr.isen.aurore.filmographyapp.api.OmdbApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Shape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListFilm(modifier: Modifier, categoryName: String?) {

    val context = LocalContext.current
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api = retrofit.create(OmdbApi::class.java)

    val apiKey = "2f17e6ee"

    val films = remember { mutableStateListOf<String>() }

    val database = FirebaseDatabase.getInstance("https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app")
    val ref = database.getReference("categories")

    LaunchedEffect(categoryName) {

        val database = FirebaseDatabase.getInstance("https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app")
        val ref = database.getReference("categories")

        ref.get().addOnSuccessListener { snapshot ->

            films.clear()

            snapshot.children.forEach { category ->

                val nomCategorie = category.child("categorie").value.toString()

                if (nomCategorie == categoryName) {

                    category.child("franchises").children.forEach { franchise ->

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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFE50914)),
                title = {
                    Text(
                        text = categoryName ?: "Films",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                },

                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                }

            )
        }

    ) { innerPadding ->

        LazyVerticalGrid(

            columns = GridCells.Fixed(2),

            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF050505))
                .padding(innerPadding)
                .padding(16.dp),

            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {

            items(films) { film ->
                var posterUrl by remember { mutableStateOf("") }

                LaunchedEffect(film) {
                    try {
                        val movie = api.getMovie(film, apiKey)
                        posterUrl = movie.Poster.toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7f)
                        .border(
                            width = 1.dp,
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            val intent = Intent(context, FilmDescriptionActivity::class.java)
                            intent.putExtra("Film", film)
                            context.startActivity(intent)
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box {

                        AsyncImage(
                            model = posterUrl,
                            contentDescription = film,
                            modifier = Modifier.fillMaxSize()
                        )

                        Text(
                            text = film,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(6.dp)
                        )
                    }
                }

            }

        }

    }

}
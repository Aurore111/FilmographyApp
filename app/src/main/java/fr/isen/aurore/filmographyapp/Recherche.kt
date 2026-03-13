package fr.isen.aurore.filmographyapp

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.google.firebase.database.FirebaseDatabase
import fr.isen.aurore.filmographyapp.api.OmdbApi
import coil.compose.AsyncImage  //Ne Pas Oublier dans buil.gradle.kts ( le 2e) de synch et de mettre les 3 implementation !!!
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Recherche(modifier: Modifier) { //page d'acceuil et de recherche de films
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val allFilms = remember { mutableStateListOf<Map<String, String>>() }
    val database = FirebaseDatabase.getInstance(
        "https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app"
    )
    LaunchedEffect(Unit) {
        database.getReference("categories").get().addOnSuccessListener { snapshot ->
            allFilms.clear()
            snapshot.children.forEach { category ->
                category.child("franchises").children.forEach { franchise ->
                    franchise.child("films").children.forEach { film ->
                        allFilms.add(
                            mapOf(
                                "title" to (film.child("titre").value?.toString() ?: ""),
                                "universe" to (category.child("categorie").value?.toString() ?: ""),                                "genre" to (film.child("genre").value?.toString() ?: ""),
                                "year" to (film.child("annee").value?.toString() ?: "")
                            )
                        )
                    }
                }
            }
        }
    }

    val filteredFilms = allFilms.filter {   //cherche un film par TITRE
        it["title"]?.contains(searchQuery, ignoreCase = true) == true
    }

    val byUniverse = filteredFilms.groupBy { it["universe"] ?: "" }
    val byGenre = filteredFilms.groupBy { it["genre"] ?: "" }
    val byYear = filteredFilms.sortedByDescending { it["year"] }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFE50914)),
                title = {
                    Text(
                        text = "Home",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF050505))
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // barre de recherche
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Rechercher un film...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF3E2723))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3E2723),
                        unfocusedBorderColor = Color(0xFFBCAAA4),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }
            item {
                Box( //ligne rouge delimitation de mes 3 parties
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE50914), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)

                ) {
                    Text(
                        "Univers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
            byUniverse.forEach { (universe, films) ->
                item {
                    Text(universe, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
                    FilmCarousel(films, context)
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE50914), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Genre",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
            byGenre.forEach { (genre, films) ->
                item {
                    Text(genre, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
                    FilmCarousel(films, context)
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE50914), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Date",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
                FilmCarousel(byYear, context)
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}


@Composable   //Carousel de ma page d'acceuil
fun FilmCarousel(films: List<Map<String, String>>, context: android.content.Context) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(films) { film ->
            var posterUrl by remember { mutableStateOf("") }//pour les images de mes films

            LaunchedEffect(film["title"]) {
                try {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://www.omdbapi.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    val api = retrofit.create(OmdbApi::class.java)
                    val movie = api.getMovie(film["title"] ?: "", "2f17e6ee")
                    posterUrl = movie.Poster.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Card(
                modifier = Modifier
                    .width(130.dp)
                    .height(220.dp)
                    .clickable {
                        val intent = Intent(context, FilmDescriptionActivity::class.java)
                        intent.putExtra("Film", film["title"])
                        context.startActivity(intent)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    if (posterUrl.isNotEmpty()) {
                        AsyncImage(
                            model = posterUrl,
                            contentDescription = film["title"],
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .background(Color(0xFFD7CCC8)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF3E2723),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = film["title"] ?: "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF3E2723),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                            )
                        Text(
                            text = film["year"] ?: "",
                            fontSize = 11.sp,
                            color = Color(0xFF8D6E63)
                            )
                    }
                }
            }
        }
    }
}
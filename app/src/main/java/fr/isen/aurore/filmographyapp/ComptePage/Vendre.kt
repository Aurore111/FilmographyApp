package fr.isen.aurore.filmographyapp.ComptePage

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import fr.isen.aurore.filmographyapp.FilmDescriptionActivity
import fr.isen.aurore.filmographyapp.api.OmdbApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//page de films que les autres utilisateur veulent vendre
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Vendre(modifier: Modifier) {
    val database = FirebaseDatabase.getInstance(
        "https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app"
    )

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current

    var filmsToSell by remember { mutableStateOf(listOf<Map<String, String>>()) }

    LaunchedEffect(Unit) {
        database.getReference("userFilms").get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<Map<String, String>>()
            var pending = 0

            snapshot.children.forEach { user ->
                val userId = user.key ?: ""
                if (userId == currentUserId) return@forEach //ne pas montrer les films du user.
                user.children.forEach { film ->
                    val filmName = film.key ?: ""
                    val sell = film.child("sell").value?.toString()

                    if (sell == "Veut s'en débarrasser") {
                        pending++
                        database.getReference("users").child(userId).child("username")
                            .get().addOnSuccessListener { nameSnap ->
                                val username = nameSnap.value?.toString() ?: userId
                                list.add(mapOf("film" to filmName, "user" to username))
                                pending--
                                if (pending == 0) filmsToSell = list.toList()
                            }
                    }
                }
                if (pending == 0) filmsToSell = list.toList()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFE50914)
                ),
                title = {
                    Text(
                        text = "Films en ventes",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? ComponentActivity)?.finish()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF050505))
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filmsToSell.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun film en vente pour le moment.",
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(filmsToSell) { entry ->
                    var posterUrl by remember { mutableStateOf("") }

                    LaunchedEffect(entry["film"]) {
                        try {
                            val retrofit = Retrofit.Builder()
                                .baseUrl("https://www.omdbapi.com/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                            val api = retrofit.create(OmdbApi::class.java)
                            val movie = api.getMovie(entry["film"] ?: "", "2f17e6ee")
                            posterUrl = movie.Poster ?: ""
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(context, FilmDescriptionActivity::class.java)
                                intent.putExtra("Film", entry["film"])
                                context.startActivity(intent)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (posterUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = posterUrl,
                                    contentDescription = entry["film"],
                                    modifier = Modifier.width(70.dp).height(90.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier.width(70.dp).height(90.dp)
                                        .background(Color(0xFFD7CCC8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF3E2723),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = entry["film"] ?: "",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF3E2723),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Vendu par : ${entry["user"]}",
                                        fontSize = 13.sp,
                                        color = Color(0xFF5D4037)
                                    )
                                }
                                Text(
                                    text = "À vendre",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

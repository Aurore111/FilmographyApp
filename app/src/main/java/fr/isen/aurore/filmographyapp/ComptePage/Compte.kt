package fr.isen.aurore.filmographyapp

import fr.isen.aurore.filmographyapp.ComptePage.FilmAVendreActivity
import fr.isen.aurore.filmographyapp.ComptePage.FilmAVoirActivity
import fr.isen.aurore.filmographyapp.ComptePage.FilmVuActivity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.foundation.lazy.items //souvent pas mis et cause error
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import fr.isen.aurore.filmographyapp.api.OmdbApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import fr.isen.aurore.filmographyapp.inscription.ConnexionActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Compte(modifier: Modifier) {
    val database = FirebaseDatabase.getInstance(
        "https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app"
    )
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid ?: ""
    val ownedFilms = remember { mutableStateListOf<String>() }

    LaunchedEffect(userId) {
        database.getReference("userFilms").child(userId)
            .get().addOnSuccessListener { snapshot ->
                ownedFilms.clear()
                snapshot.children.forEach { filmSnap ->
                    val own = filmSnap.child("own").value?.toString()
                    if (own == "Possède en DVD Blu-Ray") {
                        ownedFilms.add(filmSnap.key ?: "")
                    }
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
                    Image(
                        painter = painterResource(R.drawable.logoflix),
                        contentDescription = "logo",
                        modifier = Modifier.height(200.dp)
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profil",
                    modifier = Modifier.size(100.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = user?.displayName ?: user?.email ?: "Non connecté",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clickable {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            FilmVuActivity::class.java
                                        )
                                    )
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A7D))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Films vus",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clickable {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            FilmOwnActivity::class.java
                                        )
                                    )
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A5C1A))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Films à acheter",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clickable {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            FilmAVoirActivity::class.java
                                        )
                                    )
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF7D6B00))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Films à voir",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clickable {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            FilmAVendreActivity::class.java
                                        )
                                    )
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF5C1A1A))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Mes films à vendre",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        context.startActivity(
                            Intent(
                                context,
                                ConnexionActivity::class.java
                            )
                        )
                        (context as? ComponentActivity)?.finish()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914))
                ) {
                    Text(text = "Se déconnecter", color = Color.White, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Mes films possédés",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            items(ownedFilms) { film ->
                var posterUrl by remember { mutableStateOf("") }

                LaunchedEffect(film) {
                    try {
                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://www.omdbapi.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                        val api = retrofit.create(OmdbApi::class.java)
                        val movie = api.getMovie(film, "2f17e6ee")
                        posterUrl = movie.Poster ?: ""
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        // .padding(vertical = 4.dp)
                        .clickable { //permet d'afficher la descrip film quand on clique dessu
                            val intent = Intent(context, FilmDescriptionActivity::class.java)
                            intent.putExtra("Film", film)
                            context.startActivity(intent)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (posterUrl.isNotEmpty()) {
                            AsyncImage(
                                model = posterUrl,
                                contentDescription = film,
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(90.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(90.dp)
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
                                .weight(1f)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = film,
                                fontSize = 16.sp,
                                color = Color(0xFF3E2723),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(onClick = {
                                database.getReference("userFilms").child(userId).child(film)
                                    .removeValue()
                                ownedFilms.remove(film)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Supprimer",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }
                if (ownedFilms.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tu ne posséde auncun film pour le moment.",
                                color = Color.White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
}


package fr.isen.aurore.filmographyapp

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmOwn(modifier: Modifier) {
    val context = LocalContext.current

    val database = FirebaseDatabase.getInstance(
        "https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app"
    )

    val ref = database.getReference("userFilms")
    var filmsOwned by remember { mutableStateOf(listOf<Map<String, String>>()) }

    LaunchedEffect(Unit) {
        ref.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<Map<String,String>>()
            val users = snapshot.children.toList()
            var pending = 0

            snapshot.children.forEach { user ->
                val userId = user.key ?: ""

                user.children.forEach { film ->
                    val filmName = film.key ?: ""
                    val own = film.child("own").value?.toString()
                    val sell = film.child("sell").value?.toString()

                    if (own == "Possède en DVD Blu-Ray" || sell == "Veut s'en débarrasser")
                    {
                        pending++
                        database.getReference("users")
                            .child(userId)
                            .child("username")
                            .get()
                            .addOnSuccessListener { nameSnap ->
                                val username = nameSnap.value?.toString() ?: userId
                                list.add(
                                    mapOf(
                                        "user" to username,
                                        "film" to filmName,
                                        "wantToSell" to (sell == "Veut s'en débarrasser").toString()
                                    )
                                )
                                pending--
                                if (pending ==0) filmsOwned = list.toList()
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(filmsOwned) { entry ->

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
                        containerColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {

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
                                text = "Possédé par : ${entry["user"]}",
                                fontSize = 14.sp,
                                color = Color(0xFF5D4037)
                            )
                        }

                        if (entry["wantToSell"] == "true") {
                            Text(
                                text = "Veut vendre",
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .background(
                                        Color(0xFFE50914),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            if (filmsOwned.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun film possédé pour le moment.",
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
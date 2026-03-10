package fr.isen.aurore.filmographyapp

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextOverflow

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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
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
        }


        item {
            Text(
                text = user?.displayName ?: user?.email ?: "Non connecté",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))
        }


        item {
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    context.startActivity(
                        Intent(
                            context,
                            fr.isen.aurore.filmographyapp.inscription.ConnexionActivity::class.java
                        )
                    )
                    (context as? ComponentActivity)?.finish()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914))
            ) {
                Text(text = "Se déconnecter", color = Color.White)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        item {
            Text(
                text = "Mes films possédés",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(ownedFilms) { film ->
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { //permet d'afficher la descrip film quand on clique dessu
                val intent = Intent(context, FilmDescriptionActivity::class.java)
                intent.putExtra("Film", film)
                context.startActivity(intent)
            },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = film, fontSize = 16.sp, color = Color(0xFF3E2723), maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))

                    IconButton(onClick = {
                        database.getReference("userFilms").child(userId).child(film).removeValue()
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
}
package fr.isen.aurore.filmographyapp

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmDescription(
    modifier: Modifier,
    filmTitle: String?,
    showBackButton: Boolean = true
) {

    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var annee by remember { mutableStateOf("") }

    val database = FirebaseDatabase.getInstance(
        "https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app"
    )

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: return
    val userRef = database.getReference("userFilms").child(userId)
    val ref = database.getReference("categories")

    val statuses = listOf(
        "Vu",
        "À voir",
        "Possède en DVD/Blu-Ray",
        "Veut s'en débarrasser"
    )

    val selectedStatuses = remember { mutableStateListOf<String>() }
    val owners = listOf("Alice", "Bob", "Charlie")
    val wantToSell = listOf("Bob")



    LaunchedEffect(filmTitle) {
        ref.get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach { category ->
                category.child("franchises").children.forEach { franchise ->
                    franchise.child("films").children.forEach { film ->
                        val titre = film.child("titre").value.toString()
                        if (titre == filmTitle) {
                            title = titre
                            description = film.child("description").value.toString()
                            genre = film.child("genre").value.toString()
                            annee = film.child("annee").value.toString()
                        }
                    }
                }
            }
        }
    }



    LaunchedEffect(title) {
        val filmKey = title.replace(".", "")
        userRef.child(filmKey).get().addOnSuccessListener { snapshot ->
            val savedStatus = snapshot.value?.toString()
            if (savedStatus != null) {
                selectedStatuses.clear()
                selectedStatuses.add(savedStatus)
            }
        }
    }



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3E2723)
                    )
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = {
                            (context as? ComponentActivity)?.finish()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour"
                            )
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Poster du film")
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color(0xFF3E2723)
                        )
                        Text(
                            text = "Genre : $genre",
                            fontSize = 16.sp,
                            color = Color(0xFF5D4037)
                        )
                        Text(
                            text = "Année : $annee",
                            fontSize = 16.sp,
                            color = Color(0xFF5D4037)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            fontSize = 16.sp,
                            color = Color(0xFF4E342E)
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Mon statut",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF3E2723)
                        )
                        statuses.forEach { status ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = selectedStatuses.contains(status),
                                    onCheckedChange = { checked ->
                                        val filmKey = title.replace(".", "")
                                        if (checked) {
                                            selectedStatuses.clear()
                                            selectedStatuses.add(status)
                                            userRef.child(filmKey).setValue(status)
                                        } else {
                                            selectedStatuses.remove(status)
                                            userRef.child(filmKey).removeValue()
                                        }
                                    }
                                )
                                Text(
                                    text = status,
                                    fontSize = 16.sp,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Utilisateurs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF3E2723)
                        )
                        Text("Possèdent ce film :")
                        owners.forEach {
                            Text("• $it")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Veulent s'en débarrasser :")
                        wantToSell.forEach {
                            Text("• $it")
                        }
                    }
                }
            }
        }
    }
}
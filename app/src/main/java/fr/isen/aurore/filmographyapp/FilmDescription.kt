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
import coil.compose.AsyncImage

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
    var posterUrl by remember { mutableStateOf("") }

    val database = FirebaseDatabase.getInstance(
        "https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app"
    )

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    val userRef = if (userId != null) {
        database.getReference("userFilms").child(userId)
    } else null

    val ref = database.getReference("categories")

    val watchStatuses = listOf("Vu", "À voir")
    val ownStatuses = listOf("Possède en DVD Blu-Ray", "Veut s'en débarrasser")

    val selectedStatuses = remember { mutableStateListOf<String>() }
    val owners = remember { mutableStateListOf<String>() }
    val wantToSell = remember { mutableStateListOf<String>() }

    val apiKey = "2f17e6ee"

    /*
    --------------------------------
    Récupération infos film Firebase
    --------------------------------
    */

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

    /*
    --------------------------------
    Chargement statuts utilisateurs
    --------------------------------
    */

    LaunchedEffect(title) {

        if (title.isEmpty()) return@LaunchedEffect

        val filmKey = title.replace(".", "")

        userRef?.child(filmKey)?.get()?.addOnSuccessListener { snapshot ->

            selectedStatuses.clear()

            snapshot.child("watch").value?.toString()?.let {
                selectedStatuses.add(it)
            }

            snapshot.child("own").value?.toString()?.let {
                selectedStatuses.add(it)
            }

            snapshot.child("sell").value?.toString()?.let {
                selectedStatuses.add(it)
            }
        }

        owners.clear()
        wantToSell.clear()

        database.getReference("userFilms").get().addOnSuccessListener { snapshot ->

            snapshot.children.forEach { userSnap ->

                val uid = userSnap.key ?: ""
                val film = userSnap.child(filmKey)

                val own = film.child("own").value?.toString()
                val sell = film.child("sell").value?.toString()

                if (own == "Possède en DVD Blu-Ray" || sell == "Veut s'en débarrasser") {

                    database.getReference("users")
                        .child(uid)
                        .child("username")
                        .get()
                        .addOnSuccessListener { nameSnap ->

                            val username = nameSnap.value?.toString() ?: uid

                            if (own == "Possède en DVD Blu-Ray") owners.add(username)

                            if (sell == "Veut s'en débarrasser") wantToSell.add(username)
                        }
                }
            }
        }

        posterUrl = "https://img.omdbapi.com/?t=$title&apikey=$apiKey"
    }

    /*
    --------------------------------
    Interface
    --------------------------------
    */

    Scaffold(

        topBar = {
            CenterAlignedTopAppBar(

                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFE50914)
                ),

                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
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
                .background(Color(0xFF050505))
                .padding(innerPadding)
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {

            /*
            Poster
             */

            item {

                AsyncImage(
                    model = posterUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

            }

            /*
            Description
             */

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.85f)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )

                        Text("Genre : $genre")

                        Text("Année : $annee")

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(description)

                    }
                }
            }

            /*
            Statuts
             */

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            "Mon statut",
                            fontWeight = FontWeight.Bold
                        )

                        watchStatuses.forEach { status ->

                            Row(verticalAlignment = Alignment.CenterVertically) {

                                RadioButton(

                                    selected = selectedStatuses.contains(status),

                                    onClick = {

                                        val filmKey = title.replace(".", "")

                                        watchStatuses.forEach {
                                            selectedStatuses.remove(it)
                                        }

                                        selectedStatuses.add(status)

                                        userRef?.child(filmKey)
                                            ?.child("watch")
                                            ?.setValue(status)
                                    }
                                )

                                Text(status)
                            }
                        }

                    }
                }
            }

            /*
            Utilisateurs
             */

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            "Utilisateurs",
                            fontWeight = FontWeight.Bold
                        )

                        Text("Possèdent ce film :")

                        owners.forEach {
                            Text("• $it")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

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
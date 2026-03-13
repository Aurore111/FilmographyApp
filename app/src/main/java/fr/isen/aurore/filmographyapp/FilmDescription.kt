package fr.isen.aurore.filmographyapp

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import fr.isen.aurore.filmographyapp.api.OmdbApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    val userId = auth.currentUser?.uid ?: return
    val userRef = database.getReference("userFilms").child(userId)

    val ref = database.getReference("categories")

    val statuses = listOf(
        "Vu",
        "À voir",
        "Possède en DVD/Blu-Ray",
        "Veut s'en débarrasser"
    )

    val watchStatuses = listOf("Vu", "À voir")
    val ownStatuses = listOf("Possède en DVD Blu-Ray", "Veut s'en débarrasser")

    val selectedStatuses = remember { mutableStateListOf<String>() }

    val owners = remember { mutableStateListOf<String>() }
    val wantToSell = remember { mutableStateListOf<String>() }

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api = retrofit.create(OmdbApi::class.java)
    val apiKey = "2f17e6ee"

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

        //partie Mon status dans page description film

        if (title.isEmpty()) return@LaunchedEffect

        val filmKey = title.replace(".", "")

        userRef.child(filmKey).get().addOnSuccessListener { snapshot ->

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
                    database.getReference("users").child(uid).child("username")
                        .get().addOnSuccessListener { nameSnap ->
                            val username = nameSnap.value?.toString() ?: uid
                            if (own == "Possède en DVD Blu-Ray") owners.add(username)
                            if (sell == "Veut s'en débarrasser") wantToSell.add(username)
                        }
                }
            }
        }

        try {
            val movie = api.getMovie(title, apiKey)
            posterUrl = movie.Poster ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
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
                },

                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = { (context as? ComponentActivity)?.finish() }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = Color.White

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

            item {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
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

                        Text(
                            "Statut de visionnage",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF3E2723)
                        )

                        watchStatuses.forEach { status ->

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {

                                RadioButton(
                                    selected = selectedStatuses.contains(status),
                                    onClick = {
                                        val filmKey = title.replace(".", "")
                                        watchStatuses.forEach {
                                            selectedStatuses.remove(it)
                                        }
                                        selectedStatuses.add(status)
                                        userRef.child(filmKey)
                                            .child("watch")
                                            .setValue(status)
                                    }
                                )

                                Text(
                                    text = status,
                                    fontSize = 16.sp,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Possession",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF3E2723)
                        )

                        ownStatuses.forEach { status ->

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
                                            // "Veut s'en débarrasser" coche automatiquement "Possède en DVD Blu-Ray"
                                            if (
                                                status == "Veut s'en débarrasser"
                                                && !selectedStatuses.contains("Possède en DVD Blu-Ray")
                                            ) {
                                                selectedStatuses.add("Possède en DVD Blu-Ray")
                                                userRef.child(filmKey)
                                                    .child("own")
                                                    .setValue("Possède en DVD Blu-Ray")
                                            }

                                            selectedStatuses.add(status)
                                            userRef.child(filmKey)
                                                .child(
                                                    if (status == "Possède en DVD Blu-Ray")
                                                        "own"
                                                    else
                                                        "sell"
                                                )
                                                .setValue(status)

                                        } else {
                                            selectedStatuses.remove(status)
                                            if (status == "Possède en DVD Blu-Ray") {
                                                userRef.child(filmKey)
                                                    .child("own")
                                                    .removeValue()
                                            }

                                            if (status == "Veut s'en débarrasser") {

                                                userRef.child(filmKey)
                                                    .child("sell")
                                                    .removeValue()
                                            }
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
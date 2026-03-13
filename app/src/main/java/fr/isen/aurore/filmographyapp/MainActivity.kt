package fr.isen.aurore.filmographyapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import fr.isen.aurore.filmographyapp.ui.theme.FilmographyAppTheme
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.LaunchedEffect

enum class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
){
    CheckCircle(title = "Description", Icons.Default.CheckCircle, route = "CheckCircle"),
    Home(title = "Catégories", Icons.Default.List, route = "Home"),
    Search(title = "Home", Icons.Default.Home, route = "Search"), //icone home car c'est la page d'acceuil mais le fichier s appelle rechercher
    List(title = "Possédés", Icons.Default.Create, route = "List"),
    Account(title = "Compte", Icons.Default.AccountCircle, route = "Account")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FIREBASE_TEST", "APP STARTED")

        val database = FirebaseDatabase.getInstance(
            "https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app"
        )
        val ref = database.getReference("categories")

        ref.get().addOnSuccessListener { snapshot ->
            Log.d("FIREBASE_TEST", "Categories count: " + snapshot.childrenCount)
        }.addOnFailureListener {
            Log.d("FIREBASE_TEST", "DATABASE ERROR")
        }
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, fr.isen.aurore.filmographyapp.inscription.ConnexionActivity::class.java))
            finish()
            return
        }
        enableEdgeToEdge()
        setContent {
            FilmographyAppTheme {

                val currentItem: MutableState<NavigationItem> =
                    remember { mutableStateOf(NavigationItem.Search) }
                val randomFilm = remember { mutableStateOf<String?>(null) }

                LaunchedEffect(currentItem.value) {
                    val films = mutableListOf<String>()

                    ref.get().addOnSuccessListener { snapshot ->
                        snapshot.children.forEach { category ->
                            category.child("franchises").children.forEach { franchise ->
                                franchise.child("films").children.forEach { film ->
                                    films.add(film.child("titre").value.toString())
                                }
                            }
                        }

                        if (films.isNotEmpty()) {
                            randomFilm.value = films.random()
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar (
                                containerColor = Color(0xFFE50914)
                        )
                        {
                            NavigationItem.entries.forEach { navigationItem ->
                                NavigationBarItem(
                                    selected = currentItem.value == navigationItem,
                                    onClick = {
                                        currentItem.value = navigationItem
                                    },
                                    label = { Text(text = navigationItem.title) },
                                    icon = {
                                        Icon(
                                            imageVector = navigationItem.icon,
                                            contentDescription = "",
                                            tint = Color.White
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedTextColor = Color.White,
                                        unselectedTextColor = Color.White,
                                        indicatorColor = Color.Black
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    when (currentItem.value) {
                        NavigationItem.Home -> CategoriesScreen(Modifier.padding(innerPadding))
                        NavigationItem.CheckCircle -> FilmDescription(
                            Modifier.padding(innerPadding),
                            filmTitle = randomFilm.value,
                            showBackButton = false
                        )
                        NavigationItem.Search -> Recherche(Modifier.padding(innerPadding))
                        NavigationItem.Account -> Compte(Modifier.padding(innerPadding))
                        NavigationItem.List -> FilmOwn(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

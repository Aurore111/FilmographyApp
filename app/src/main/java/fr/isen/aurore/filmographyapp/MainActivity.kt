package fr.isen.aurore.filmographyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.aurore.filmographyapp.ui.theme.FilmographyAppTheme
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
){
    Home(title = "Catégories Films", Icons.Default.Home, route = "Home"),
    List(title = "List Films", Icons.Default.Menu, route = "List"),
//    Fav(title = "Favoris", Icons.Default.Favorite, route = "Fav"),
    Search(title = "Descritions Films", Icons.Default.Search, route = "Search")
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
        enableEdgeToEdge()
        setContent {
            FilmographyAppTheme {

                val currentItem: MutableState<NavigationItem> =
                    remember { mutableStateOf(NavigationItem.Home) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
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
                                            contentDescription = ""
                                        )
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    when (currentItem.value) {
                        NavigationItem.Home -> CategoriesScreen(Modifier.padding(innerPadding))
                        NavigationItem.List -> ListFilm(Modifier.padding(innerPadding))
                        NavigationItem.Search -> FilmDescription(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

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
               CategoriesScreen(modifier = Modifier)
            }
        }
    }
}
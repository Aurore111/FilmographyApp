package fr.isen.aurore.filmographyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import fr.isen.aurore.filmographyapp.ui.theme.FilmographyAppTheme

class FilmOwnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilmographyAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FilmOwn(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

package fr.isen.aurore.filmographyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import fr.isen.aurore.filmographyapp.ui.theme.FilmographyAppTheme

class CategoriesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryName = intent.getStringExtra("category")

        setContent {
            FilmographyAppTheme {

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    ListFilm(
                        modifier = Modifier.padding(innerPadding),
                        categoryName = categoryName
                    )

                }

            }
        }
    }
}
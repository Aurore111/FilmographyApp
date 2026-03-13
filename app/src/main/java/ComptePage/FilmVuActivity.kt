package ComptePage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import fr.isen.aurore.filmographyapp.ui.theme.FilmographyAppTheme

class FilmVuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FilmographyAppTheme {
                FilmographyAppTheme {
                    FilmVu(Modifier.fillMaxSize())
                }
            }
        }
    }
}


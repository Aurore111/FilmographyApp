package fr.isen.aurore.filmographyapp

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Compte(modifier: Modifier)
{
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFCCFA4))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profil",
            modifier = Modifier.size(100.dp),
            tint = Color(0xFF3E2723)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user?.email ?: "Non connecté",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3E2723)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button (
            onClick = {
                FirebaseAuth.getInstance().signOut()
                context.startActivity(Intent(context, fr.isen.aurore.filmographyapp.inscription.ConnexionActivity::class.java))
                (context as? ComponentActivity)?.finish()
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723))
        ) {
            Text(text = "Se déconnecter", color = Color.White)
        }

    }
}

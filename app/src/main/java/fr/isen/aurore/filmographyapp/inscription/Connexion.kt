package fr.isen.aurore.filmographyapp.inscription

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import fr.isen.aurore.filmographyapp.MainActivity

@Composable
fun Connexion(modifier: Modifier) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) } // true = connexion, false = inscription

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFCCFA4))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLogin) "Connexion" else "Inscription",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3E2723)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!isLogin) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nom d'utilisateur") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe (6 caractères)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button (
            onClick = {
                val auth = FirebaseAuth.getInstance()
                if (isLogin) {
                    // Connexion
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            Log.d("AUTH", "Connexion réussie")
                            context.startActivity(Intent(context, MainActivity::class.java)) //connecté ou inscrit ca ouvre la page indiqué dans mainActivity
                            (context as? ComponentActivity)?.finish()
                        }
                        .addOnFailureListener {
                            Log.d("AUTH", "Erreur connexion : ${it.message}")
                            Toast.makeText(context, "Erreur : ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                } else
                {
                    // inscription
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val profileUpdate = UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build()
                            it.user?.updateProfile(profileUpdate)

                            Log.d("AUTH", "Inscription réussie")
                            context.startActivity(Intent(context, MainActivity::class.java))
                            (context as? ComponentActivity)?.finish()
                        }
                        .addOnFailureListener {
                            Log.d("AUTH", "Erreur inscription : ${it.message}")
                            Toast.makeText(context, "Erreur : ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723))
        ) {
            Text(
                text = if (isLogin) "Se connecter" else "S'inscrire",
                fontSize = 16.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        TextButton (onClick = { isLogin = isLogin.not() }) {
            Text(
                text = if (isLogin) "Pas de compte ? S'inscrire" else "Déjà un compte ? Se connecter",
                color = Color(0xFF5D4037)
            )
        }
    }
}
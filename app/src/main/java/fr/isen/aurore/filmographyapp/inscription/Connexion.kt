package fr.isen.aurore.filmographyapp.inscription

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import fr.isen.aurore.filmographyapp.MainActivity

@Composable
fun Connexion(modifier: Modifier) {

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
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
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nom d'utilisateur") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe (6 caractères)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                val auth = FirebaseAuth.getInstance()

                if (isLogin) {

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {

                            Log.d("AUTH", "Connexion réussie")

                            context.startActivity(Intent(context, MainActivity::class.java))
                            (context as? ComponentActivity)?.finish()

                        }
                        .addOnFailureListener {

                            Log.d("AUTH", "Erreur connexion : ${it.message}")
                            Toast.makeText(context, "Erreur : ${it.message}", Toast.LENGTH_SHORT).show()

                        }

                } else {

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { result ->

                            val user = result.user
                            val uid = user?.uid

                            val profileUpdate = UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build()

                            user?.updateProfile(profileUpdate)

                            val database = FirebaseDatabase.getInstance(
                                "https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app"
                            )

                            uid?.let { userId ->

                                Log.d("DB_TEST", "UID = $userId  username = $username")

                                database.getReference("users")
                                    .child(userId)
                                    .child("username")
                                    .setValue(username)
                                    .addOnSuccessListener {
                                        Log.d("DB_TEST", "Pseudo enregistré dans Firebase")
                                    }
                                    .addOnFailureListener {
                                        Log.d("DB_TEST", "Erreur Firebase : ${it.message}")
                                    }
                            }
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

        TextButton(onClick = { isLogin = !isLogin }) {

            Text(
                text = if (isLogin) "Pas de compte ? S'inscrire" else "Déjà un compte ? Se connecter",
                color = Color(0xFF5D4037)
            )

        }

    }

}
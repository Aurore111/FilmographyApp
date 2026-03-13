package fr.isen.aurore.filmographyapp

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.draw.blur


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(modifier: Modifier) {

    val context = LocalContext.current


    var franchises by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        val database = FirebaseDatabase.getInstance(
            "https://filmographyapp-8fb1e-default-rtdb.europe-west1.firebasedatabase.app"
        )

        val ref = database.getReference("categories")

        ref.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<String>()

            snapshot.children.forEach { category ->
                val name = category.child("categorie").value.toString()
                list.add(name)
            }

            franchises = list
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
                }
            )
        }
    ) { innerPadding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF050505))
                .padding(innerPadding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(franchises) { univers ->

                val context = LocalContext.current

                Button(
                    onClick = {
                        val intent = Intent(context, CategoriesActivity::class.java)
                        intent.putExtra("category", univers)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.8f),
                        contentColor = Color(0xFF5D4037)
                    )
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        val logo = when (univers) {
                            "Marvel" -> R.drawable.marvel
                            "Star Wars" -> R.drawable.starwars
                            "Pixar" -> R.drawable.pixar
                            "Disney" -> R.drawable.disney
                            "Avatar" -> R.drawable.avatar
                            "Harry Potter" -> R.drawable.harrypotter
                            else -> null
                        }

                        logo?.let {
                            Image(
                                painter = painterResource(it),
                                contentDescription = univers,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }

                        Text(
                            text = univers,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
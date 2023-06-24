package com.example.pruebatecnicapokemon

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.random.Random
import com.bumptech.glide.Glide


class PokemonInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_pokemon)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val textView = findViewById<TextView>(R.id.textView)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        getRandomPokemonData { name, imageUrl, exception -> //Aqui se mandan los parametros ara la funcion los cuales buscara en el json para traer su valor
            if (name != null && imageUrl != null) { //en caso de que si devuelva imagen y nombre se procede a lo siguiente por eso pide que no sean nulos
                runOnUiThread {
                    // Modificar ImageView con la imagen del Pokémon
                    Glide.with(this) //usando Glide se cargan imagenes directamente de una URL
                        .load(imageUrl) //esta url se genera en la función
                        .into(imageView) //aca se modifica la image view

                    // Modificar TextView con el nombre del Pokémon
                    textView.text = name
                    //Ajuste del tamaño de la imagen obtenida de la api
                    imageView.layoutParams.height = 1200
                    imageView.layoutParams.width = 1200
                }
            } else {
                exception?.printStackTrace()
            }
        }


    }
    private fun getRandomPokemonData(callback: (String?, String?, Exception?) -> Unit) {
        val client = OkHttpClient()
        val randomPokemonId = Random.nextInt(1, 899)
        val url = "https://pokeapi.co/api/v2/pokemon/$randomPokemonId"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val pokemon = Gson().fromJson(responseBody, Pokemon::class.java)
                    val name = pokemon.name
                    val imageUrl = pokemon.sprites.front_default
                    callback(name, imageUrl, null)
                } else {
                    callback(null, null, IOException("Error: ${response.code} ${response.message}"))
                }
            }
        })
    }

    data class Pokemon(
        val name: String,
        val sprites: Sprites
    )

    data class Sprites(
        val front_default: String
    )


}
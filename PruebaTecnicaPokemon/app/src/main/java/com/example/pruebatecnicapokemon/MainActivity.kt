package com.example.pruebatecnicapokemon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val backButton = findViewById<Button>(R.id.button)
        backButton.setOnClickListener {
            val intent = Intent(this, PokemonInfo::class.java)
            startActivity(intent)
        }

    }
}
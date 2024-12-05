package com.example.conquistadores

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnAltas).setOnClickListener {
            startActivity(Intent(this, AltasActivity::class.java))
        }
        findViewById<Button>(R.id.btnModificacion).setOnClickListener {
            startActivity(Intent(this, ModificacionActivity::class.java))
        }
        findViewById<Button>(R.id.btnConsulta).setOnClickListener {
            startActivity(Intent(this, ConsultaActivity::class.java))
        }
    }
}
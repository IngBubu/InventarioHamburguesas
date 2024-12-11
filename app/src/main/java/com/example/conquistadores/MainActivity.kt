package com.example.conquistadores

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.conquistadores.menu.AltaMenuActivity
import com.example.conquistadores.menu.AltasActivity
import com.example.conquistadores.menu.ConsultaActivity
import com.example.conquistadores.menu.ModificacionActivity
import com.example.conquistadores.menu.VentasActivity

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
        findViewById<Button>(R.id.btnVentas).setOnClickListener{
            startActivity(Intent(this, VentasActivity::class.java))
        }
        findViewById<Button>(R.id.btnMenu).setOnClickListener{
            startActivity(Intent(this, AltaMenuActivity::class.java))
        }
    }
}
package com.example.conquistadores.menu

import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.conquistadores.R
import com.example.conquistadores.data.BaseDeDatos

class AltasActivity : AppCompatActivity() {

    private lateinit var dbHelper: BaseDeDatos
    private lateinit var etNombre: EditText
    private lateinit var etPrecioCosto: EditText
    private lateinit var etPrecioPublico: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_altas)

        // Inicializa la base de datos
        dbHelper = BaseDeDatos(this)

        // Encuentra las vistas
        etNombre = findViewById(R.id.etNombre)
        etPrecioCosto = findViewById(R.id.etPrecio)
        etPrecioPublico = findViewById(R.id.etPrecioPublico)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Configura el botón Guardar
        btnGuardar.setOnClickListener {
            guardarProducto()
        }
    }

    private fun guardarProducto() {
        val db = dbHelper.writableDatabase
        val values = ContentValues()

        // Obtiene los valores de los campos
        val nombre = etNombre.text.toString()
        val precioCosto = etPrecioCosto.text.toString().toDoubleOrNull()
        val precioPublico = etPrecioPublico.text.toString().toDoubleOrNull()

        // Valida los datos
        if (nombre.isNotEmpty() && precioCosto != null && precioPublico != null) {
            values.put("nombre", nombre)
            values.put("precio", precioCosto)
            values.put("precio_publico", precioPublico)

            // Inserta el producto con cantidad inicial en 0 (configurado por defecto en la base de datos)
            val resultado = db.insert("Productos", null, values)

            if (resultado != -1L) {
                mostrarExito("Producto guardado correctamente")
            } else {
                mostrarError("Error al guardar el producto")
            }
        } else {
            mostrarError("Por favor completa todos los campos correctamente")
        }

        db.close()
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarExito(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

        // Limpia los campos después de guardar
        etNombre.text.clear()
        etPrecioCosto.text.clear()
        etPrecioPublico.text.clear()
    }
}

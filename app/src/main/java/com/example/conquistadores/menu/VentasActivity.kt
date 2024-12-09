package com.example.conquistadores.menu

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.conquistadores.R
import com.example.conquistadores.data.BaseDeDatos
import java.text.SimpleDateFormat
import java.util.*

class VentasActivity : AppCompatActivity() {

    private lateinit var dbHelper: BaseDeDatos
    private lateinit var etFecha: EditText
    private lateinit var etTotal: EditText
    private lateinit var radioGroupFormaPago: RadioGroup
    private lateinit var btnGuardarVenta: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ventas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar la base de datos
        dbHelper = BaseDeDatos(this)

        // Enlazar vistas
        etFecha = findViewById(R.id.etFecha)
        etTotal = findViewById(R.id.etTotal)
        radioGroupFormaPago = findViewById(R.id.radioGroupFormaPago)
        btnGuardarVenta = findViewById(R.id.btnGuardarVenta)

        // Establecer la fecha actual
        val fechaActual = obtenerFechaActual()
        etFecha.setText(fechaActual)

        // Configurar botón de guardar
        btnGuardarVenta.setOnClickListener {
            guardarVenta()
        }
    }

    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun guardarVenta() {
        val fecha = etFecha.text.toString().trim()
        val total = etTotal.text.toString().toDoubleOrNull()

        // Validación de entrada
        if (fecha.isEmpty() || total == null) {
            Toast.makeText(this, "Por favor, completa todos los campos correctamente.", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener forma de pago seleccionada
        val formaPago = when (radioGroupFormaPago.checkedRadioButtonId) {
            R.id.rbEfectivo -> "Efectivo"
            R.id.rbTarjeta -> "Tarjeta"
            else -> {
                Toast.makeText(this, "Por favor, selecciona una forma de pago.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("fecha", fecha)
            put("total", total)
            put("forma_pago", formaPago) // Agrega una columna "forma_pago" en la tabla Ventas
        }

        // Insertar nueva venta
        val newRowId = db.insert("Ventas", null, values)
        if (newRowId != -1L) {
            Toast.makeText(this, "Venta registrada correctamente.", Toast.LENGTH_SHORT).show()
            limpiarCampos()
        } else {
            Toast.makeText(this, "Error al registrar la venta.", Toast.LENGTH_SHORT).show()
        }

        db.close()
    }

    private fun limpiarCampos() {
        etTotal.text.clear()
        radioGroupFormaPago.clearCheck()
    }
}

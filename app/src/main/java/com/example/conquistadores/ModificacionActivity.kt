package com.example.conquistadores

import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.conquistadores.data.BaseDeDatos

class ModificacionActivity : AppCompatActivity() {

    private lateinit var dbHelper: BaseDeDatos
    private lateinit var spTablas: Spinner
    private lateinit var etId: EditText
    private lateinit var etCampo1: EditText
    private lateinit var etCampo2: EditText
    private lateinit var etCampo3: EditText
    private lateinit var etCampo4: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnGuardar: Button
    private lateinit var layoutCampos: CardView

    private var tablaSeleccionada: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificacion)

        dbHelper = BaseDeDatos(this)

        spTablas = findViewById(R.id.spTablas)
        etId = findViewById(R.id.etId)
        etCampo1 = findViewById(R.id.etCampo1)
        etCampo2 = findViewById(R.id.etCampo2)
        etCampo3 = findViewById(R.id.etCampo3)
        etCampo4 = findViewById(R.id.etCampo4)
        btnBuscar = findViewById(R.id.btnBuscar)
        btnGuardar = findViewById(R.id.btnGuardar)
        layoutCampos = findViewById(R.id.layoutCampos)

        // Configuración del Spinner
        val tablas = arrayOf("Selecciona una tabla", "Productos", "Ventas" )
        spTablas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tablas)

        spTablas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                tablaSeleccionada = tablas[position]
                configurarCampos(tablaSeleccionada)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnBuscar.setOnClickListener {
            if (etId.text.isNotEmpty()) {
                cargarDatos(tablaSeleccionada, etId.text.toString().toInt())
            } else {
                Toast.makeText(this, "Por favor ingresa un ID válido", Toast.LENGTH_SHORT).show()
            }
        }

        btnGuardar.setOnClickListener {
            if (etId.text.isNotEmpty()) {
                guardarCambios(tablaSeleccionada, etId.text.toString().toInt())
            } else {
                Toast.makeText(this, "Por favor ingresa un ID válido", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun configurarCampos(tabla: String) {
        layoutCampos.visibility = if (tabla != "Selecciona una tabla") LinearLayout.VISIBLE else LinearLayout.GONE
        etCampo1.visibility = EditText.VISIBLE
        etCampo2.visibility = EditText.VISIBLE
        etCampo3.visibility = EditText.VISIBLE
        etCampo4.visibility = EditText.VISIBLE

        when (tabla) {
            "Productos" -> {
                etCampo1.hint = "Nombre"
                etCampo2.hint = "Cantidad"
                etCampo3.hint = "Precio"
                etCampo4.visibility = EditText.GONE
            }
            "Ventas" -> {
                etCampo1.hint = "Fecha (YYYY-MM-DD)"
                etCampo2.hint = "Total"
                etCampo3.visibility = EditText.GONE
                etCampo4.visibility = EditText.GONE
            }
        }
    }

    private fun cargarDatos(tabla: String, id: Int) {
        val db = dbHelper.readableDatabase
        val query = when (tabla) {
            "Productos" -> "SELECT * FROM Productos WHERE id_producto = ?"
            "Ventas" -> "SELECT * FROM Ventas WHERE id_venta = ?"
            else -> ""
        }

        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            when (tabla) {
                "Productos" -> {
                    etCampo1.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre")))
                    etCampo2.setText(cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")).toString())
                    etCampo3.setText(cursor.getDouble(cursor.getColumnIndexOrThrow("precio")).toString())
                }
                "Ventas" -> {
                    etCampo1.setText(cursor.getString(cursor.getColumnIndexOrThrow("fecha")))
                    etCampo2.setText(cursor.getDouble(cursor.getColumnIndexOrThrow("total")).toString())
                }
            }
        } else {
            Toast.makeText(this, "El ID $id no existe en la tabla $tabla", Toast.LENGTH_SHORT).show()

        }
        cursor.close()
        db.close()
    }

    private fun guardarCambios(tabla: String, id: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues()

        when (tabla) {
            "Productos" -> {
                values.put("nombre", etCampo1.text.toString())
                values.put("cantidad", etCampo2.text.toString().toIntOrNull())
                values.put("precio", etCampo3.text.toString().toDoubleOrNull())
                db.update("Productos", values, "id_producto = ?", arrayOf(id.toString()))
            }
            "Ventas" -> {
                values.put("fecha", etCampo1.text.toString())
                values.put("total", etCampo2.text.toString().toDoubleOrNull())
                db.update("Ventas", values, "id_venta = ?", arrayOf(id.toString()))
            }
        }

        Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show()
        db.close()
    }
}

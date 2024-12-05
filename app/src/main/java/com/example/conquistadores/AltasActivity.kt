package com.example.conquistadores

import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AltasActivity : AppCompatActivity() {

    private lateinit var dbHelper: BaseDeDatos
    private lateinit var layoutCampos: CardView // Cambiar a CardView
    private lateinit var etCampo1: EditText
    private lateinit var etCampo2: EditText
    private lateinit var etCampo3: EditText
    private lateinit var etCampo4: EditText
    private lateinit var btnGuardar: Button
    private lateinit var tablaSeleccionada: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_altas)

        dbHelper = BaseDeDatos(this)
        // Encuentra las vistas
        layoutCampos = findViewById(R.id.layoutCampos) // Esto ahora es un CardView
        etCampo1 = findViewById(R.id.etCampo1)
        etCampo2 = findViewById(R.id.etCampo2)
        etCampo3 = findViewById(R.id.etCampo3)
        etCampo4 = findViewById(R.id.etCampo4)
        btnGuardar = findViewById(R.id.btnGuardar)

        findViewById<Button>(R.id.btnProductos).setOnClickListener {
            configurarCampos("Productos")
        }


        findViewById<Button>(R.id.btnVentas).setOnClickListener {
            configurarCampos("Ventas")
        }



        btnGuardar.setOnClickListener {
            if (::tablaSeleccionada.isInitialized) {
                guardarDatos(tablaSeleccionada)
            } else {
                Toast.makeText(this, "Por favor selecciona una tabla", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarCampos(tabla: String) {
        tablaSeleccionada = tabla
        layoutCampos.visibility = CardView.VISIBLE

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
            "Proveedores" -> {
                etCampo1.hint = "Nombre"
                etCampo2.hint = "Contacto"
                etCampo3.visibility = EditText.GONE
                etCampo4.visibility = EditText.GONE
            }
            "Ventas" -> {
                etCampo1.hint = "Fecha (YYYY-MM-DD)"
                etCampo2.hint = "Total"
                etCampo3.visibility = EditText.GONE
                etCampo4.visibility = EditText.GONE
            }
            "Categorías" -> {
                etCampo1.hint = "Nombre de la Categoría"
                etCampo2.visibility = EditText.GONE
                etCampo3.visibility = EditText.GONE
                etCampo4.visibility = EditText.GONE
            }
        }
    }

    private fun guardarDatos(tabla: String) {
            val db = dbHelper.writableDatabase
            val values = ContentValues()
            var mensajeExito = ""

            when (tabla) {
                "Productos" -> {
                    val nombre = etCampo1.text.toString()
                    val cantidad = etCampo2.text.toString().toIntOrNull()
                    val precio = etCampo3.text.toString().toDoubleOrNull()
                    if (nombre.isNotEmpty() && cantidad != null && precio != null) {
                        values.put("nombre", nombre)
                        values.put("cantidad", cantidad)
                        values.put("precio", precio)
                        db.insert("Productos", null, values)
                        mensajeExito = "Producto guardado correctamente"
                    } else {
                        mostrarError()
                        return
                    }
                }
                "Proveedores" -> {
                    val nombre = etCampo1.text.toString()
                    val contacto = etCampo2.text.toString()
                    if (nombre.isNotEmpty() && contacto.isNotEmpty()) {
                        values.put("nombre", nombre)
                        values.put("contacto", contacto)
                        db.insert("Proveedores", null, values)
                        mensajeExito = "Proveedor guardado correctamente"
                    } else {
                        mostrarError()
                        return
                    }
                }
                "Ventas" -> {
                    val fecha = etCampo1.text.toString()
                    val total = etCampo2.text.toString().toDoubleOrNull()
                    if (fecha.isNotEmpty() && total != null) {
                        values.put("fecha", fecha)
                        values.put("total", total)
                        db.insert("Ventas", null, values)
                        mensajeExito = "Venta registrada correctamente"
                    } else {
                        mostrarError()
                        return
                    }
                }
                "Categorías" -> {
                    val nombreCategoria = etCampo1.text.toString()
                    if (nombreCategoria.isNotEmpty()) {
                        values.put("nombre_categoria", nombreCategoria)
                        db.insert("Categorias", null, values)
                        mensajeExito = "Categoría guardada correctamente"
                    } else {
                        mostrarError()
                        return
                    }
                }
            }
            db.close()

            // Mostrar el mensaje de éxito
            mostrarExito(mensajeExito)
        }
    private fun mostrarError() {
        Toast.makeText(this, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarExito(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

        // Limpia los campos después de guardar
        etCampo1.text.clear()
        etCampo2.text.clear()
        etCampo3.text.clear()
        etCampo4.text.clear()

        layoutCampos.visibility = CardView.GONE // Oculta los campos
    }


}


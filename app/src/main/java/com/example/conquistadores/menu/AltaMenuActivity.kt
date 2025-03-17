package com.example.conquistadores.menu

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conquistadores.R
import com.example.conquistadores.adapter.IngredienteAdapter
import com.example.conquistadores.data.BaseDeDatos
import com.example.conquistadores.data.Producto

class AltaMenuActivity : AppCompatActivity() {

    private lateinit var dbHelper: BaseDeDatos
    private lateinit var etNombrePlato: EditText
    private lateinit var etPrecioPlato: EditText
    private lateinit var btnGuardarMenu: Button
    private lateinit var rvIngredientes: RecyclerView
    private lateinit var adapter: IngredienteAdapter
    private var listaProductos = mutableListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alta_menu)

        dbHelper = BaseDeDatos(this)
        etNombrePlato = findViewById(R.id.etNombrePlato)
        etPrecioPlato = findViewById(R.id.etPrecioPlato)
        btnGuardarMenu = findViewById(R.id.btnGuardarMenu)
        rvIngredientes = findViewById(R.id.rvIngredientes)

        // Inicializar el adapter antes de cargar los productos
        adapter = IngredienteAdapter(listaProductos)
        rvIngredientes.layoutManager = LinearLayoutManager(this)
        rvIngredientes.adapter = adapter

        cargarProductos()

        btnGuardarMenu.setOnClickListener {
            guardarMenu()
        }
    }

    private fun cargarProductos() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Productos", null)

        listaProductos.clear()
        if (cursor.moveToFirst()) {
            do {
                val producto = Producto(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cantidad = 0, // Inicializamos la cantidad en 0 para el menú
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                )
                listaProductos.add(producto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        adapter.notifyDataSetChanged()
    }

    private fun guardarMenu() {
        val nombrePlato = etNombrePlato.text.toString().trim()
        val precioPlato = etPrecioPlato.text.toString().toDoubleOrNull()

        if (nombrePlato.isEmpty() || precioPlato == null || listaProductos.none { it.cantidad > 0 }) {
            Toast.makeText(this, "Completa el nombre, precio y selecciona al menos un ingrediente.", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        db.beginTransaction() // Inicia una transacción para garantizar la atomicidad de la operación

        try {
            // Insertar el plato en la tabla Menus
            val menuValues = ContentValues().apply {
                put("nombre", nombrePlato)
                put("precio", precioPlato)
            }
            val menuId = db.insert("Menus", null, menuValues)

            if (menuId != -1L) {
                // Insertar los ingredientes en la tabla IngredientesMenus
                val ingredientes = listaProductos.filter { it.cantidad > 0 }
                ingredientes.forEach { ingrediente ->
                    val ingredienteValues = ContentValues().apply {
                        put("id_menu", menuId)
                        put("id_producto", ingrediente.id)
                        put("cantidad_usada", ingrediente.cantidad)
                    }
                    db.insert("IngredientesMenus", null, ingredienteValues)
                }

                // Log del nombre del platillo y los ingredientes
                Log.d("AltaMenuActivity", "Platillo agregado: $nombrePlato")
                ingredientes.forEach { ingrediente ->
                    Log.d("AltaMenuActivity", "Ingrediente: ${ingrediente.nombre}, Cantidad: ${ingrediente.cantidad}")
                }

                db.setTransactionSuccessful() // Marca la transacción como exitosa
                Toast.makeText(this, "Menú guardado correctamente.", Toast.LENGTH_SHORT).show()
                limpiarCampos()
            } else {
                Toast.makeText(this, "Error al guardar el menú.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("AltaMenuActivity", "Error al guardar el menú: ${e.message}")
            Toast.makeText(this, "Error al guardar el menú.", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction() // Finaliza la transacción
            db.close()
        }
    }

    private fun limpiarCampos() {
        etNombrePlato.text.clear()
        etPrecioPlato.text.clear()
        listaProductos.forEach { it.cantidad = 0 }
        adapter.notifyDataSetChanged()
    }
}
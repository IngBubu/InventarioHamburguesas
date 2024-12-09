package com.example.conquistadores.menu

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conquistadores.R
import com.example.conquistadores.adapter.ProductoAdapter
import com.example.conquistadores.data.BaseDeDatos
import com.example.conquistadores.data.Producto

class ModificacionActivity : AppCompatActivity() {

    private lateinit var dbHelper: BaseDeDatos
    private lateinit var rvProductos: RecyclerView
    private lateinit var btnInventariar: Button
    private lateinit var adaptador: ProductoAdapter
    private var listaProductos = mutableListOf<Producto>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificacion)

        dbHelper = BaseDeDatos(this)
        rvProductos = findViewById(R.id.rvProductos)
        btnInventariar = findViewById(R.id.btnInventariar)

        cargarProductos()

        adaptador = ProductoAdapter(listaProductos)
        rvProductos.layoutManager = LinearLayoutManager(this)
        rvProductos.adapter = adaptador

        btnInventariar.setOnClickListener {
            guardarInventario()
            limpiarCantidades() // Limpia las cantidades ingresadas después de guardar
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
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                )
                listaProductos.add(producto)
             } while (cursor.moveToNext())
        } else {
        }

        cursor.close()
        db.close()
    }

    private fun guardarInventario() {
        val db = dbHelper.writableDatabase
        var productosActualizados = 0

        for (producto in listaProductos) {
            if (producto.cantidadNueva > 0) { // Solo actualiza si se ingresó una cantidad nueva
                val nuevaCantidad = producto.cantidad + producto.cantidadNueva

                val values = ContentValues()
                values.put("cantidad", nuevaCantidad)

                val rowsAffected = db.update("Productos", values, "id_producto = ?", arrayOf(producto.id.toString()))
                if (rowsAffected > 0) {
                     producto.cantidad = nuevaCantidad // Actualiza la cantidad en memoria
                    productosActualizados++
                } else {
                }
            }
        }

        Toast.makeText(this, "Inventario actualizado correctamente. Productos actualizados: $productosActualizados", Toast.LENGTH_SHORT).show()
        db.close()
    }

    private fun limpiarCantidades() {
        listaProductos.forEach { it.cantidadNueva = 0 } // Reinicia la cantidad ingresada
        adaptador.notifyDataSetChanged() // Actualiza la vista
    }
}

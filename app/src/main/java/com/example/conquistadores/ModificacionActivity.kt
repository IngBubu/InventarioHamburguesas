package com.example.conquistadores

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conquistadores.adapter.ProductoAdapter
import com.example.conquistadores.data.BaseDeDatos
import com.example.conquistadores.data.Producto

class ModificacionActivity : AppCompatActivity() {

    private lateinit var dbHelper: BaseDeDatos
    private lateinit var rvProductos: RecyclerView
    private lateinit var btnInventariar: Button
    private lateinit var adaptador: ProductoAdapter
    private var listaProductos = mutableListOf<Producto>()

    companion object {
        private const val TAG = "ModificacionActivity"
    }

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
        Log.d(TAG, "Cargando productos desde la base de datos...")
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
                Log.d(TAG, "Producto cargado: ID=${producto.id}, Nombre=${producto.nombre}, Cantidad=${producto.cantidad}, Precio=${producto.precio}")
            } while (cursor.moveToNext())
        } else {
            Log.d(TAG, "No se encontraron productos en la base de datos.")
        }

        cursor.close()
        db.close()
        Log.d(TAG, "Productos cargados: ${listaProductos.size}")
    }

    private fun guardarInventario() {
        Log.d(TAG, "Guardando inventario...")
        val db = dbHelper.writableDatabase
        var productosActualizados = 0

        for (producto in listaProductos) {
            if (producto.cantidadNueva > 0) { // Solo actualiza si se ingresó una cantidad nueva
                val nuevaCantidad = producto.cantidad + producto.cantidadNueva

                val values = ContentValues()
                values.put("cantidad", nuevaCantidad)

                val rowsAffected = db.update("Productos", values, "id_producto = ?", arrayOf(producto.id.toString()))
                if (rowsAffected > 0) {
                    Log.d(TAG, "Producto actualizado correctamente: ID=${producto.id}, Cantidad anterior=${producto.cantidad}, Nueva cantidad=$nuevaCantidad")
                    producto.cantidad = nuevaCantidad // Actualiza la cantidad en memoria
                    productosActualizados++
                } else {
                    Log.e(TAG, "Error al actualizar el producto: ID=${producto.id}")
                }
            }
        }

        Toast.makeText(this, "Inventario actualizado correctamente. Productos actualizados: $productosActualizados", Toast.LENGTH_SHORT).show()
        db.close()
        Log.d(TAG, "Inventario guardado. Total productos actualizados: $productosActualizados")
    }

    private fun limpiarCantidades() {
        Log.d(TAG, "Limpiando cantidades ingresadas...")
        listaProductos.forEach { it.cantidadNueva = 0 } // Reinicia la cantidad ingresada
        adaptador.notifyDataSetChanged() // Actualiza la vista
    }
}

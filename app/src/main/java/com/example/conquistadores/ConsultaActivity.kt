package com.example.conquistadores

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.conquistadores.data.BaseDeDatos

class ConsultaActivity : AppCompatActivity() {

    private lateinit var dbHelper: BaseDeDatos
    private lateinit var lvResultados: ListView
    private val resultados = ArrayList<Pair<String, Int>>() // Lista para guardar registros y sus IDs

    companion object {
        private const val TAG = "ConsultaActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta)

        dbHelper = BaseDeDatos(this)
        lvResultados = findViewById(R.id.lvResultados)

        val btnProductos = findViewById<Button>(R.id.btnProductos)
        val btnVentas = findViewById<Button>(R.id.btnVentas)

        // Botón para consultar la tabla Productos
        btnProductos.setOnClickListener { consultarProductos() }

        // Botón para consultar la tabla Ventas
        btnVentas.setOnClickListener { consultarVentas() }
    }

    private fun consultarProductos() {
        Log.d(TAG, "Iniciando consulta de la tabla Productos...")
        val db = dbHelper.readableDatabase
        resultados.clear()

        // Consulta para obtener todos los productos
        val query = """
            SELECT id_producto, nombre, cantidad, precio
            FROM Productos
        """.trimIndent()

        Log.d(TAG, "Ejecutando consulta: $query")
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            Log.d(TAG, "Registros encontrados en la tabla Productos.")
            do {
                val idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("id_producto"))
                val nombreProducto = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val cantidadProducto = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
                val precioProducto = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))

                Log.d(TAG, "Registro: ID Producto: $idProducto, Nombre: $nombreProducto, Cantidad: $cantidadProducto, Precio: $precioProducto")

                resultados.add(
                    Pair(
                        "ID Producto: $idProducto\nNombre: $nombreProducto\nCantidad: $cantidadProducto\nPrecio: $precioProducto",
                        idProducto
                    )
                )
            } while (cursor.moveToNext())
        } else {
            Log.d(TAG, "No se encontraron registros en la tabla Productos.")
            resultados.add(Pair("No hay productos registrados en la tabla Productos.", -1))
        }

        cursor.close()
        db.close()

        actualizarListView("Productos")
        Toast.makeText(this, "Consulta de Productos realizada", Toast.LENGTH_SHORT).show()
    }

    private fun consultarVentas() {
        Log.d(TAG, "Iniciando consulta de la tabla Ventas...")
        val db = dbHelper.readableDatabase
        resultados.clear()

        // Consulta para obtener todas las ventas con forma de pago
        val query = """
            SELECT id_venta, fecha, total, forma_pago
            FROM Ventas
        """.trimIndent()

        Log.d(TAG, "Ejecutando consulta: $query")
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            Log.d(TAG, "Registros encontrados en la tabla Ventas.")
            do {
                val idVenta = cursor.getInt(cursor.getColumnIndexOrThrow("id_venta"))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                val formaPago = cursor.getString(cursor.getColumnIndexOrThrow("forma_pago"))

                Log.d(TAG, "Registro: ID Venta: $idVenta, Fecha: $fecha, Total: $total, Forma de Pago: $formaPago")

                resultados.add(
                    Pair(
                        "ID Venta: $idVenta\nFecha: $fecha\nTotal: $total\nForma de Pago: $formaPago",
                        idVenta
                    )
                )
            } while (cursor.moveToNext())
        } else {
            Log.d(TAG, "No se encontraron registros en la tabla Ventas.")
            resultados.add(Pair("No hay ventas registradas en la tabla Ventas.", -1))
        }

        cursor.close()
        db.close()

        actualizarListView("Ventas")
        Toast.makeText(this, "Consulta de Ventas realizada", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarListView(tabla: String) {
        val adapter = object : ArrayAdapter<Pair<String, Int>>(this, R.layout.list_item_consulta, resultados) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = layoutInflater.inflate(R.layout.list_item_consulta, parent, false)
                val tvRegistro = view.findViewById<TextView>(R.id.tvRegistro)
                val btnEliminar = view.findViewById<ImageButton>(R.id.btnEliminar)

                val registro = resultados[position]
                tvRegistro.text = registro.first

                btnEliminar.setOnClickListener {
                    if (registro.second != -1) {
                        eliminarRegistro(tabla, registro.second)
                        resultados.removeAt(position)
                        notifyDataSetChanged()
                    }
                }
                return view
            }
        }

        lvResultados.adapter = adapter
    }

    private fun eliminarRegistro(tabla: String, id: Int) {
        val db = dbHelper.writableDatabase
        val whereClause = when (tabla) {
            "Ventas" -> "id_venta=?"
            "Productos" -> "id_producto=?" // Eliminar de la tabla Productos
            else -> ""
        }
        db.delete(tabla, whereClause, arrayOf(id.toString()))
        db.close()
        Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show()
    }
}

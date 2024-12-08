package com.example.conquistadores

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.conquistadores.data.BaseDeDatos

class ConsultaActivity : AppCompatActivity() {

    private lateinit var dbHelper: BaseDeDatos
    private lateinit var lvResultados: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val resultados = ArrayList<Pair<String, Int>>() // Lista para guardar registros y sus IDs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta)

        dbHelper = BaseDeDatos(this)
        lvResultados = findViewById(R.id.lvResultados)

        val btnProductos = findViewById<Button>(R.id.btnProductos)
        val btnVentas = findViewById<Button>(R.id.btnVentas)

        btnProductos.setOnClickListener { consultarTabla("Productos") }
        btnVentas.setOnClickListener { consultarTabla("Ventas") }

    }

    private fun consultarTabla(tabla: String) {
        val db = dbHelper.readableDatabase
        resultados.clear()

        val query = when (tabla) {
            "Productos" -> "SELECT * FROM Productos"
            "Ventas" -> "SELECT * FROM Ventas"
            else -> ""
        }

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val registro = when (tabla) {
                    "Productos" -> {
                        val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_producto"))
                        val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                        val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
                        val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                        resultados.add(Pair("ID: $id\nNombre: $nombre\nCantidad: $cantidad\nPrecio: $precio", id))
                    }
                    "Ventas" -> {
                        val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_venta"))
                        val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                        val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                        resultados.add(Pair("ID: $id\nFecha: $fecha\nTotal: $total", id))
                    }

                    else -> ""
                }
            } while (cursor.moveToNext())
        } else {
            resultados.add(Pair("No hay registros en la tabla $tabla.", -1))
        }
        cursor.close()
        db.close()

        actualizarListView(tabla)
        Toast.makeText(this, "Consulta de $tabla realizada", Toast.LENGTH_SHORT).show()
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
            "Productos" -> "id_producto=?"
            "Ventas" -> "id_venta=?"
            else -> ""
        }
        db.delete(tabla, whereClause, arrayOf(id.toString()))
        db.close()
        Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show()
    }
}

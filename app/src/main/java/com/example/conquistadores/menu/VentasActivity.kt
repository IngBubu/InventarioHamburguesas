package com.example.conquistadores.menu

import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conquistadores.R
import com.example.conquistadores.adapter.MenuProductoAdapter
import com.example.conquistadores.data.BaseDeDatos
import com.example.conquistadores.data.Producto
import java.text.SimpleDateFormat
import java.util.*

class VentasActivity : AppCompatActivity() {

    private lateinit var dbHelper: BaseDeDatos
    private lateinit var etFecha: EditText
    private lateinit var tvTotalVenta: TextView
    private lateinit var radioGroupFormaPago: RadioGroup
    private lateinit var btnMenu: Button
    private lateinit var btnProductos: Button
    private lateinit var btnGuardarVenta: Button
    private lateinit var rvItems: RecyclerView
    private lateinit var adapter: MenuProductoAdapter

    private val listaProductos = mutableListOf<Producto>()
    private var totalVenta = 0.0

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
        tvTotalVenta = findViewById(R.id.tvTotalVenta)
        radioGroupFormaPago = findViewById(R.id.radioGroupFormaPago)
        btnMenu = findViewById(R.id.btnMenu)
        btnProductos = findViewById(R.id.btnProductos)
        btnGuardarVenta = findViewById(R.id.btnGuardarVenta)
        rvItems = findViewById(R.id.rvSeleccion)

        // Configurar RecyclerView
        adapter = MenuProductoAdapter(listaProductos) { producto, cantidad ->
            if (cantidad > 0) {
                producto.cantidad += cantidad
                actualizarTotal()
            } else {
                Toast.makeText(this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show()
            }

        }
        rvItems.layoutManager = LinearLayoutManager(this)
        rvItems.adapter = adapter

        // Establecer la fecha actual
        etFecha.setText(obtenerFechaActual())

        // Configurar botones
        btnMenu.setOnClickListener { cargarMenu() }
        btnProductos.setOnClickListener { cargarProductos() }
        btnGuardarVenta.setOnClickListener { guardarVenta() }
    }

    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun cargarMenu() {
        listaProductos.clear()
        val db = dbHelper.readableDatabase
        val query = "SELECT id_menu AS id_producto, nombre, precio FROM Menus"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("id_producto"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                listaProductos.add(Producto(idProducto, nombre, 0, precio))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        adapter.notifyDataSetChanged()
    }

    private fun cargarProductos() {
        listaProductos.clear()
        val db = dbHelper.readableDatabase
        val query = "SELECT id_producto, nombre, precio_publico FROM Productos"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("id_producto"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_publico"))
                listaProductos.add(Producto(idProducto, nombre, 0, precio))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        adapter.notifyDataSetChanged()
    }

    private fun actualizarTotal() {
        totalVenta = listaProductos.sumOf { it.precio * it.cantidad }
        tvTotalVenta.text = "Total: $$totalVenta"
    }


    private fun guardarVenta() {
        val fecha = etFecha.text.toString().trim()
        if (fecha.isEmpty() || totalVenta <= 0) {
            Toast.makeText(this, "Por favor, completa todos los campos correctamente.", Toast.LENGTH_SHORT).show()
            return
        }

        val formaPago = when (radioGroupFormaPago.checkedRadioButtonId) {
            R.id.rbEfectivo -> "Efectivo"
            R.id.rbTarjeta -> "Tarjeta"
            else -> {
                Toast.makeText(this, "Por favor, selecciona una forma de pago.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val db = dbHelper.writableDatabase

        // Insertar la venta
        val values = ContentValues().apply {
            put("fecha", fecha)
            put("total", totalVenta)
            put("forma_pago", formaPago)
        }

        val ventaId = db.insert("Ventas", null, values)
        if (ventaId != -1L) {
            // Actualizar inventario según los productos vendidos
            listaProductos.forEach { producto ->
                if (producto.cantidad > 0) {
                    descontarProducto(producto.id, producto.cantidad)
                }
            }

            Toast.makeText(this, "Venta registrada correctamente.", Toast.LENGTH_SHORT).show()
            limpiarCampos()
        } else {
            Toast.makeText(this, "Error al registrar la venta.", Toast.LENGTH_SHORT).show()
        }

        db.close()
    }

    private fun descontarProducto(idProducto: Int, cantidad: Int) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE Productos SET cantidad = cantidad - ? WHERE id_producto = ?",
            arrayOf(cantidad.toString(), idProducto.toString())
        )
        db.close()
    }

    private fun limpiarCampos() {
        etFecha.setText(obtenerFechaActual())
        radioGroupFormaPago.clearCheck()
        listaProductos.clear()
        adapter.notifyDataSetChanged()
        tvTotalVenta.text = "Total: $0.0"
    }
}

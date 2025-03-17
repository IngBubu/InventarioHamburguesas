package com.example.conquistadores.menu

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
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
        val query = """
        SELECT id_menu, nombre, precio FROM Menus
    """
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val idMenu = cursor.getInt(cursor.getColumnIndexOrThrow("id_menu"))
                val nombreMenu = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val precioMenu = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                listaProductos.add(Producto(idMenu, nombreMenu, 0, precioMenu, isMenu = true))
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
            // Actualizar inventario y registrar detalles de venta
            listaProductos.forEach { producto ->
                if (producto.cantidad > 0) {
                    if (producto.isMenu) {
                        registrarDetalleVentaMenu(db, ventaId, producto)
                    } else {
                        registrarDetalleVentaProducto(db, ventaId, producto)
                    }
                }
            }

            Toast.makeText(this, "Venta registrada correctamente.", Toast.LENGTH_SHORT).show()
            limpiarCampos()
        } else {
            Toast.makeText(this, "Error al registrar la venta.", Toast.LENGTH_SHORT).show()
        }

        db.close()
    }


    private fun registrarDetalleVentaMenu(db: SQLiteDatabase, ventaId: Long, producto: Producto) {
        // Registrar el menú como detalle de venta
        val values = ContentValues().apply {
            put("id_venta", ventaId)
            put("id_menu", producto.id)
            put("cantidad", producto.cantidad)
            put("subtotal", producto.precio * producto.cantidad)
        }
        db.insert("VentasDetalle", null, values)

        // Descontar ingredientes del menú
        val query = "SELECT id_producto, cantidad_usada FROM IngredientesMenus WHERE id_menu = ?"
        val cursor = db.rawQuery(query, arrayOf(producto.id.toString()))

        if (cursor.moveToFirst()) {
            do {
                val idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("id_producto"))
                val cantidadUsada = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_usada"))
                descontarProducto(db, idProducto, producto.cantidad * cantidadUsada)
            } while (cursor.moveToNext())
        }

        cursor.close()
    }

    private fun registrarDetalleVentaProducto(db: SQLiteDatabase, ventaId: Long, producto: Producto) {
        // Registrar el producto como detalle de venta
        val values = ContentValues().apply {
            put("id_venta", ventaId)
            put("id_producto", producto.id)
            put("cantidad", producto.cantidad)
            put("subtotal", producto.precio * producto.cantidad)
        }
        db.insert("VentasDetalle", null, values)

        // Descontar el producto del inventario
        descontarProducto(db, producto.id, producto.cantidad)
    }

    private fun descontarProducto(db: SQLiteDatabase, idProducto: Int, cantidad: Int) {
        Log.d("VentasActivity", "Descontando $cantidad unidades del producto con ID $idProducto")
        db.execSQL(
            "UPDATE Productos SET cantidad = cantidad - ? WHERE id_producto = ?",
            arrayOf(cantidad.toString(), idProducto.toString())
        )
    }

    private fun limpiarCampos() {
        etFecha.setText(obtenerFechaActual())
        radioGroupFormaPago.clearCheck()
        listaProductos.clear()
        adapter.notifyDataSetChanged()
        tvTotalVenta.text = "Total: $0.0"
    }
}
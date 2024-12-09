package com.example.conquistadores.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.conquistadores.R
import com.example.conquistadores.data.Producto

class ProductoAdapter(private val productos: List<Producto>) :
    RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto)
    }

    override fun getItemCount(): Int = productos.size

    class ProductoViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        private val tvNombre: TextView = vista.findViewById(R.id.tvNombreProducto)
        private val etCantidad: EditText = vista.findViewById(R.id.etCantidad)
        private var textWatcher: android.text.TextWatcher? = null

        fun bind(producto: Producto) {
            tvNombre.text = producto.nombre

            // Remueve cualquier TextWatcher previo
            textWatcher?.let {
                etCantidad.removeTextChangedListener(it)
            }

            // Configura el valor de cantidadNueva
            etCantidad.setText(producto.cantidadNueva.toString())

            // Crea un nuevo TextWatcher
            textWatcher = object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: android.text.Editable?) {
                    val nuevaCantidad = s.toString().toIntOrNull() ?: 0
                    producto.cantidadNueva = nuevaCantidad
                }
            }

            // Agrega el nuevo TextWatcher
            etCantidad.addTextChangedListener(textWatcher)
        }
    }
}


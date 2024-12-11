package com.example.conquistadores.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.conquistadores.R
import com.example.conquistadores.data.Producto

class IngredienteAdapter(private val productos: MutableList<Producto>) :
    RecyclerView.Adapter<IngredienteAdapter.IngredienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingrediente, parent, false)
        return IngredienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredienteViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto)
    }

    override fun getItemCount(): Int = productos.size

    inner class IngredienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombreIngrediente: TextView = itemView.findViewById(R.id.tvNombreIngrediente)
        private val etCantidadUsada: EditText = itemView.findViewById(R.id.etCantidadUsada)
        private val btnIncrementar: Button = itemView.findViewById(R.id.btnIncrementar)
        private val btnDecrementar: Button = itemView.findViewById(R.id.btnDecrementar)

        fun bind(producto: Producto) {
            tvNombreIngrediente.text = producto.nombre
            etCantidadUsada.setText(producto.cantidad.toString())

            btnIncrementar.setOnClickListener {
                val cantidadActual = producto.cantidad
                producto.cantidad = cantidadActual + 1
                etCantidadUsada.setText(producto.cantidad.toString())
            }

            btnDecrementar.setOnClickListener {
                val cantidadActual = producto.cantidad
                if (cantidadActual > 0) {
                    producto.cantidad = cantidadActual - 1
                    etCantidadUsada.setText(producto.cantidad.toString())
                }
            }

            // Actualización manual del EditText (opcional para casos de entrada directa)
            etCantidadUsada.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val nuevaCantidad = etCantidadUsada.text.toString().toIntOrNull() ?: 0
                    producto.cantidad = if (nuevaCantidad >= 0) nuevaCantidad else 0
                    etCantidadUsada.setText(producto.cantidad.toString())
                }
            }
        }
    }
}

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

class MenuProductoAdapter(
    private val productos: List<Producto>,
    private val onAgregarClick: (Producto, Int) -> Unit
) : RecyclerView.Adapter<MenuProductoAdapter.MenuProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_producto, parent, false)
        return MenuProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto, onAgregarClick)
    }

    override fun getItemCount(): Int = productos.size

    class MenuProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombreProducto: TextView = itemView.findViewById(R.id.tvNombreProducto)
        private val tvPrecioProducto: TextView = itemView.findViewById(R.id.tvPrecioProducto)
        private val btnAgregar: Button = itemView.findViewById(R.id.btnAgregar)
        private val etCantidadProducto: EditText = itemView.findViewById(R.id.etCantidadProducto)

        fun bind(producto: Producto, onAgregarClick: (Producto, Int) -> Unit) {
            tvNombreProducto.text = producto.nombre
            tvPrecioProducto.text = "$%.2f".format(producto.precio)

            btnAgregar.setOnClickListener {
                val cantidad = etCantidadProducto.text.toString().toIntOrNull() ?: 0
                if (cantidad > 0) {
                    onAgregarClick(producto, cantidad)
                } else {
                    etCantidadProducto.error = "Ingresa una cantidad válida"
                }
            }
        }
    }
}

package com.example.conquistadores.data

data class Producto(
    val id: Int,
    val nombre: String,
    var cantidad: Int,
    val precio: Double,
    var cantidadNueva: Int = 0,
    val cantidadUsada: Int = 1,
    val isMenu: Boolean = false
)

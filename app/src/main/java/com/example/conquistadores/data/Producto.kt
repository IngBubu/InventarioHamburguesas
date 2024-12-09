package com.example.conquistadores.data

data class Producto(
    val id: Int,
    val nombre: String,
    var cantidad: Int,
    val precio: Double,
    var cantidadNueva: Int = 0
)

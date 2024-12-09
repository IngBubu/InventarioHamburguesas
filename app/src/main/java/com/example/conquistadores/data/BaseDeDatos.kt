package com.example.conquistadores.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDeDatos(context: Context) : SQLiteOpenHelper(context, "ConquistadoresDB", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla Productos
        db.execSQL(
            """
            CREATE TABLE Productos (
                id_producto INTEGER PRIMARY KEY,
                nombre TEXT,
                cantidad INTEGER DEFAULT 0,
                precio REAL
            )
            """.trimIndent()
        )

        // Crear tabla Ventas con forma de pago
        db.execSQL(
            """
            CREATE TABLE Ventas (
                id_venta INTEGER PRIMARY KEY,
                fecha TEXT,
                total REAL,
                forma_pago TEXT
            )
            """.trimIndent()
        )

        // Crear tabla Altas
        db.execSQL(
            """
            CREATE TABLE Altas (
                id_alta INTEGER PRIMARY KEY AUTOINCREMENT,
                id_producto INTEGER,
                inventariado INTEGER DEFAULT 0,
                fecha_alta TEXT,
                FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Manejar actualizaciones de la base de datos
        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS Altas (
                    id_alta INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_producto INTEGER,
                    inventariado INTEGER DEFAULT 0,
                    fecha_alta TEXT,
                    FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
                )
                """.trimIndent()
            )
        }

        if (oldVersion < 3) {
            // Agregar columna forma_pago en la tabla Ventas
            db.execSQL("ALTER TABLE Ventas ADD COLUMN forma_pago TEXT")
        }
    }

    /**
     * Método para borrar la base de datos y recrearla (solo para desarrollo).
     */
    fun reiniciarBaseDeDatos() {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS Productos")
        db.execSQL("DROP TABLE IF EXISTS Ventas")
        db.execSQL("DROP TABLE IF EXISTS Altas")
        onCreate(db)
    }
}

package com.example.conquistadores.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDeDatos(context: Context) : SQLiteOpenHelper(context, "ConquistadoresDB", null, 5) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla Productos
        db.execSQL(
            """
            CREATE TABLE Productos (
                id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                cantidad INTEGER DEFAULT 0,
                precio REAL NOT NULL
            )
            """.trimIndent()
        )

        // Crear tabla Ventas
        db.execSQL(
            """
            CREATE TABLE Ventas (
                id_venta INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha TEXT NOT NULL,
                total REAL NOT NULL,
                forma_pago TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Crear tabla Altas (para inventarios)
        db.execSQL(
            """
            CREATE TABLE Altas (
                id_alta INTEGER PRIMARY KEY AUTOINCREMENT,
                id_producto INTEGER NOT NULL,
                inventariado INTEGER DEFAULT 0,
                fecha_alta TEXT NOT NULL,
                FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
            )
            """.trimIndent()
        )

        // Crear tabla Menús
        db.execSQL(
            """
            CREATE TABLE Menus (
                id_menu INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                precio REAL NOT NULL
            )
            """.trimIndent()
        )

        // Crear tabla Ingredientes (relación entre Menús y Productos)
        db.execSQL(
            """
            CREATE TABLE IngredientesMenus (
                id_ingrediente INTEGER PRIMARY KEY AUTOINCREMENT,
                id_menu INTEGER NOT NULL,
                id_producto INTEGER NOT NULL,
                cantidad_usada INTEGER NOT NULL,
                FOREIGN KEY (id_menu) REFERENCES Menus (id_menu),
                FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Crear tabla Altas si no existe
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS Altas (
                    id_alta INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_producto INTEGER NOT NULL,
                    inventariado INTEGER DEFAULT 0,
                    fecha_alta TEXT NOT NULL,
                    FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
                )
                """.trimIndent()
            )
        }

        if (oldVersion < 4) {
            // Crear tabla Menús
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS Menus (
                    id_menu INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    precio REAL NOT NULL
                )
                """.trimIndent()
            )

            // Crear tabla IngredientesMenus
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS IngredientesMenus (
                    id_ingrediente INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_menu INTEGER NOT NULL,
                    id_producto INTEGER NOT NULL,
                    cantidad_usada INTEGER NOT NULL,
                    FOREIGN KEY (id_menu) REFERENCES Menus (id_menu),
                    FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
                )
                """.trimIndent()
            )
        }

        if (oldVersion < 5) {
            // Asegurar que las columnas "precio" y "nombre" estén correctamente definidas en Menus
            db.execSQL("DROP TABLE IF EXISTS Menus")
            db.execSQL(
                """
                CREATE TABLE Menus (
                    id_menu INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    precio REAL NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    /**
     * Método para reiniciar la base de datos (solo para desarrollo).
     */
    fun reiniciarBaseDeDatos() {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS Productos")
        db.execSQL("DROP TABLE IF EXISTS Ventas")
        db.execSQL("DROP TABLE IF EXISTS Altas")
        db.execSQL("DROP TABLE IF EXISTS Menus")
        db.execSQL("DROP TABLE IF EXISTS IngredientesMenus")
        onCreate(db)
    }
}
package com.example.conquistadores.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDeDatos(context: Context) : SQLiteOpenHelper(context, "ConquistadoresDB", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear todas las tablas necesarias
        db.execSQL(
            """
            CREATE TABLE Productos (
                id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                cantidad INTEGER DEFAULT 0,
                precio REAL NOT NULL,
                precio_publico REAL NOT NULL
            )
            """.trimIndent()
        )

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

        db.execSQL(
            """
            CREATE TABLE Menus (
                id_menu INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                precio REAL NOT NULL
            )
            """.trimIndent()
        )

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

        db.execSQL(
            """
            CREATE TABLE VentasDetalle (
                id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
                id_venta INTEGER NOT NULL,
                id_menu INTEGER DEFAULT NULL,
                id_producto INTEGER DEFAULT NULL,
                cantidad INTEGER NOT NULL,
                subtotal REAL NOT NULL,
                FOREIGN KEY (id_venta) REFERENCES Ventas (id_venta),
                FOREIGN KEY (id_menu) REFERENCES Menus (id_menu),
                FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Verificar si la tabla IngredientesMenus ya existe antes de crearla
            val cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='IngredientesMenus'",
                null
            )
            if (cursor.count == 0) {
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
            cursor.close()
        }
    }
}
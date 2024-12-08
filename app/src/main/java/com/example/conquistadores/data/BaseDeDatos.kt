package com.example.conquistadores.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class BaseDeDatos (context: Context) : SQLiteOpenHelper(context, "ConquistadoresDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE Productos (" +
                "id_producto INTEGER PRIMARY KEY," +
                " nombre TEXT, " +
                "cantidad INTEGER," +
                " precio REAL)")
        db.execSQL("CREATE TABLE Ventas (" +
                "id_venta INTEGER PRIMARY KEY," +
                " fecha TEXT, " +
                "total REAL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Productos")
        db.execSQL("DROP TABLE IF EXISTS Ventas")
        onCreate(db)
    }
}
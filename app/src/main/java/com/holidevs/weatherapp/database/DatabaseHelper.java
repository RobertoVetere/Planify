package com.holidevs.weatherapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "planify_plans.db";

    private static final int DATABASE_VERSION = 12;

    private static final String TABLE_PLANS = "plans";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PLAN = "plan_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crea la tabla de planes
        String createTableQuery = "CREATE TABLE " + DatabaseContract.PlanEntry.TABLE_NAME +
                "(" + DatabaseContract.PlanEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.PlanEntry.COLUMN_PLAN + " TEXT)";
        db.execSQL(createTableQuery);

        // Agrega un mensaje de log para verificar si onCreate() se ejecuta correctamente
        Log.d("DatabaseHelper", "onCreate() ejecutado. Tabla 'plans' creada.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si necesitas actualizar la estructura de la base de datos, puedes implementar aquí la lógica
        // para modificar la tabla existente o crear nuevas tablas.
        // Ten en cuenta que esto se ejecutará cuando cambies el número de versión de la base de datos.
        // En este ejemplo, simplemente eliminaremos la tabla anterior y la crearemos nuevamente.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANS);
        onCreate(db);

        // Agrega un mensaje de log para verificar si onUpgrade() se ejecuta correctamente
        Log.d("DatabaseHelper", "onUpgrade() ejecutado. Tabla 'plans' eliminada y creada nuevamente.");
    }
}

package com.eswar.employeemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [DepartmentEntity::class, EmployeeEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun employeeDao(): EmployeeDao
    abstract fun departmentDao(): DepartmentDao

    companion object {
        private const val NAME = "employee_manager.db"
        @Volatile private var instance: AppDatabase? = null

        /** Thread-safe singleton (double-checked locking). */
        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, NAME)
                .addCallback(SeedCallback)
                .build().also { instance = it }
        }

        /** Test hook: closes and forgets the singleton so each test starts from a fresh file. */
        @androidx.annotation.VisibleForTesting
        fun resetInstance() = synchronized(this) { instance?.close(); instance = null }

        /** In-memory database for unit tests (no seed data). */
        fun inMemory(context: Context): AppDatabase =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
    }

    /** Pre-populates departments + sample employees inside the CREATE transaction (works fully offline). */
    private object SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            SeedData.departments.forEach { d ->
                db.execSQL("INSERT INTO departments(code, name, cost_centre) VALUES (?, ?, ?)", arrayOf(d.code, d.name, d.costCentre))
            }
            SeedData.employees.forEach { e ->
                db.execSQL(
                    "INSERT INTO employees(name, designation, department_code, email, phone, salary, joining_date, is_favorite) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    arrayOf(e.name, e.designation, e.departmentCode, e.email, e.phone, e.salary, e.joiningDate.toEpochDay(), if (e.isFavorite) 1 else 0),
                )
            }
        }
    }
}

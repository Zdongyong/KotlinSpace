package com.zdy.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * 创建日期：8/3/21 on 12:48 AM
 * 描述：
 * 作者：zhudongyong
 */
// java  entities = {}
// kt entities = []
@Database(entities = [Student::class], version = 1)
abstract class StudentDataBase : RoomDatabase() {
    // 最终给用户的就是 DAO
    abstract fun getStudentDao(): StudentDao

    companion object {

        private var INSTANCE: StudentDataBase? = null

        // Application
        fun getDatabase(context: Context): StudentDataBase? {

            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    StudentDataBase::class.java,
                    "student_database.db"
                )
                    .allowMainThreadQueries() // 允许在主线程运行
                    .build()
            }

            return INSTANCE

        }

        fun getDatabase(): StudentDataBase? = INSTANCE

    }

}
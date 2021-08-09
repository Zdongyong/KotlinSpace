package com.zdy.dataBase

import androidx.room.*

/**
 * 创建日期：8/3/21 on 12:43 AM
 * 描述：
 * 作者：zhudongyong
 */
@Dao //student 操作类
interface StudentDao {

    // vararg 可变参数
    // void a(Student... stus)   vs   vararg students: Student
    @Insert
    fun insert(vararg students: Student)

    @Delete
    fun delete(vararg students: Student)

    // 删除全部
    @Query("DELETE FROM student")
    fun deleteAllStudents()

    @Update
    fun update(vararg students: Student)

    @Query("SELECT * FROM student ORDER BY ID DESC")
    fun query(): List<Student>
}
package com.zdy.dataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 创建日期：8/2/21 on 10:42 PM
 * 描述：
 * 作者：zhudongyong
 */
@Entity
class Student() {

    @PrimaryKey(autoGenerate = true)//自增
    var id: Long = 0

    @ColumnInfo(name = "_name")//可以加别名，优先级更高， 如果加了 就使用别名的名称
    lateinit var name: String

    @ColumnInfo(name = "_age")
    var age: Int = 0

    constructor(name: String, age: Int) : this() {
        this.name = name
        this.age = age
    }
}
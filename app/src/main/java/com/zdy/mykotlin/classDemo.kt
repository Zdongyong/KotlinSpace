package com.zdy.mykotlin

/**
 * 创建日期：7/31/21 on 12:08 AM
 * 描述：
 * 作者：zhudongyong
 */
//默认是public
class classDemo(id: Int)//主构造
{

    //次构造 必须调用主构造
    constructor(id: Int, name: String, age: Int) : this(id) {
    }

}
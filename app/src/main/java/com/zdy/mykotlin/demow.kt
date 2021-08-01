package com.zdy.mykotlin

import com.zdy.clazz.Instance
import java.time.Instant

/**
 * 创建日期：7/30/21 on 11:49 PM
 * 描述：
 * 作者：zhudongyong
 */
fun main() {

    var numbers = arrayOf<Int>(2);
    var numbers2 = arrayOf(1, 2, 3);
    numbers[0] = 1;
    println(numbers2.size)

//    var numbers3 = Array(20, { value: Int -> (value + 100) });
//    for (value: Int in numbers3) {
//        println(value)
//    }

    var number: Int = 5;
    var number2: Int = 5;
    val nn: String = "1"
    var reslut: String = when (number) {
        1 -> "1"
        else -> "其他"
    }

    println(reslut)

    when (number) {
        number2 -> println("包含")
        else -> println("不包含")
    }

    var classa = classDemo(1);
    var classa2 = classDemo(1, "demo", 3);

    val instance = Instance.getInstance()
    instance?.show("11")
}
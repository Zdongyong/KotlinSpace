package com.zdy.clazz

/**
 * 创建日期：7/31/21 on 12:34 AM
 * 描述：
 * 作者：zhudongyong
 */
class Instance {

    companion object {
        private var INSTANCE: Instance? = null

        fun getInstance(): Instance? {
            if (null == INSTANCE) {
                INSTANCE = Instance()
            }
            return INSTANCE
        }
    }

    fun show(name:String){
        println(name)
    }
}
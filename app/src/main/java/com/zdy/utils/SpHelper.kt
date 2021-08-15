package com.zdy.utils

import android.content.Context

/**
 * 创建日期：8/15/21 on 2:33 PM
 * 描述：
 * 作者：zhudongyong
 */

const val LOGIN_STATE: String = "login_state";

private val SP: String = "sp"

fun <T> getValue(
    context: Context,
    key: String,
    defaultVal: T
): T {
    val sharedPreferences = context.getSharedPreferences(SP, Context.MODE_PRIVATE)
    return when (defaultVal) {
        is Boolean -> sharedPreferences.getBoolean(key, defaultVal) as T
        is Float -> sharedPreferences.getFloat(key, defaultVal) as T
        is Int -> sharedPreferences.getInt(key, defaultVal) as T
        is Long -> sharedPreferences.getLong(key, defaultVal) as T
        else -> sharedPreferences.getString(key, defaultVal?.toString()) as T
    }
}


fun <T> putValue(
    context: Context,
    key: String,
    value: T
) {
    val editor = context.getSharedPreferences(SP, Context.MODE_PRIVATE).edit()
    when (value) {
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
        else -> editor.putString(key, value?.toString())
    }
    editor.commit()
}

fun remove(
    context: Context,
    key: String
) {
    val editor = context.getSharedPreferences(SP, Context.MODE_PRIVATE).edit()
    editor.remove(key).apply()
}

fun clear(
    context: Context
) {
    val editor = context.getSharedPreferences(SP, Context.MODE_PRIVATE).edit()
    editor.clear().apply()
}
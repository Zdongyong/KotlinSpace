package com.zdy.entity

/**
 * 创建日期：8/1/21 on 3:16 PM
 * 描述：
 * 作者：zhudongyong
 */

//    "data": {
//    "admin": false,
//    "chapterTops": [],
//    "collectIds": [],
//    "email": "",
//    "icon": "",
//    "id": 66720,
//    "nickname": "Derry-vip",
//    "password": "",
//    "publicName": "Derry-vip",
//    "token": "",
//    "type": 0,
//    "username": "Derry-vip"
//},

//通配符 java中的？ == kt *
//String ? 表示服务器字段可能会null
data class LoginResponse(
    val admin: Boolean,
    val chapterTops: List<*>,
    val email: String ?,
    val icon: String ?,
    val id: String ?,
    val nickname: String ?,
    val password: String ?,
    val publicName: String ?,
    val token: String ?,
    val type: Int,
    val username: String ?
)

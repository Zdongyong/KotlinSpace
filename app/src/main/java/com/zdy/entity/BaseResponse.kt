package com.zdy.entity

/**
 * 创建日期：8/1/21 on 3:05 PM
 * 描述：
 * 作者：zhudongyong
 */

//{
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
//    "errorCode": 0,
//    "errorMsg": ""
//}
//
//{
//    "data": null,
//    "errorCode": -1,
//    "errorMsg": "账号密码不匹配！"
//}

data class BaseResponse<T>(val data: T, val errorCode: String, val errorMsg: String)
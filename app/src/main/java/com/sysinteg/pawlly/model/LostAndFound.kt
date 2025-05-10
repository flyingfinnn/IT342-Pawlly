package com.sysinteg.pawlly.model

data class LostAndFound(
    val reportid: Int,
    val reporttype: String,
    val petname: String,
    val datereported: String,
    val lastseen: String,
    val description: String,
    val imageurl: String?,
    val creatorid: Int
) 
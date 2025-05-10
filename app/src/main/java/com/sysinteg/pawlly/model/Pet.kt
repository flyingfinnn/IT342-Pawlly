package com.sysinteg.pawlly.model

data class Pet(
    val id: Int,
    val name: String,
    val breed: String,
    val age: String,
    val type: String? = null,
    val location: String? = null,
    val photo: String? = null,
    val photo1: String? = null,
    val photo1Thumb: String? = null,
    val photo2: String? = null,
    val photo3: String? = null,
    val photo4: String? = null,
    val weight: String? = null,
    val color: String? = null,
    val height: String? = null,
    val user_name: String? = null
) 
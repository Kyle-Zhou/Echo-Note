package com.example.echonote.data.persistence

interface IAuth {
    suspend fun signupUser(userEmail: String, userPassword: String, userName: String)
    suspend fun loginUser(userEmail: String, userPassword: String)
    suspend fun logoutUser()
    suspend fun editUserName(newUserName: String)
    fun getCurrentUserID(): String
    fun getName(): String
}
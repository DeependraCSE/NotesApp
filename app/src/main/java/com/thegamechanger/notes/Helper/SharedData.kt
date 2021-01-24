package com.thegamechanger.notes.Helper

import android.content.Context
import android.app.Activity

class SharedData internal constructor(context: Context){
    val sharedPref = context.getSharedPreferences(AppConstant.SharedData, Context.MODE_PRIVATE)

    fun setToken(s : String) {
        with(sharedPref.edit()) {
            putString(AppConstant.Token, s)
            commit()
        }
    }

    fun getToken() : String? {
        val data = sharedPref.getString(AppConstant.Token, null)
        return data
    }

    fun setPassword(oldPassword : String, newPassword : String) : Int {
        var response = 0
        var storedPassword = getPassword()
        if (null != storedPassword){
            if (storedPassword.equals(oldPassword)){
                with (sharedPref.edit()) {
                    putString(AppConstant.Password,newPassword)
                    commit()
                    response = AppConstant.PasswordChanged
                }
            }else{
                response = AppConstant.PasswordMismatch
            }
        }else{
            with (sharedPref.edit()) {
                putString(AppConstant.Password,newPassword)
                commit()
                response = AppConstant.PasswordChanged
            }
        }
        return response
    }

    fun getPassword() : String? {
        val data = sharedPref.getString(AppConstant.Password, null)
        return data
    }
}
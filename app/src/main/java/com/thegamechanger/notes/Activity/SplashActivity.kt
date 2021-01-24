package com.thegamechanger.notes.Activity

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.facebook.stetho.Stetho
import com.thegamechanger.notes.Fragment.Splash
import com.thegamechanger.notes.R

class SplashActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Stetho.initializeWithDefaults(this)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportFragmentManager.beginTransaction().replace(R.id.container, Splash()).commit()
    }

}
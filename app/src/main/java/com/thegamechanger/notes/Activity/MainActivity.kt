package com.thegamechanger.notes.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.thegamechanger.notes.Fragment.Dashboard
import com.thegamechanger.notes.Helper.AppConstant
import com.thegamechanger.notes.R

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAnalytics = Firebase.analytics
        
        supportFragmentManager.addOnBackStackChangedListener {
            val backStackCount = supportFragmentManager.backStackEntryCount
            if(backStackCount > 0){
                supportActionBar!!.setDisplayHomeAsUpEnabled(true);
            }else{
                supportActionBar!!.setDisplayHomeAsUpEnabled(false);
            }
        }

        LoadHomeFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val  fragment = supportFragmentManager.findFragmentByTag(AppConstant.HomeFragment);
        if(null != fragment && fragment.isVisible){
            super.onBackPressed()
        }else{
            if(supportFragmentManager.backStackEntryCount > 0){
                supportFragmentManager.popBackStack()
            }else{
                LoadHomeFragment()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.SOURCE, "Main_Activity")
        }
    }

    fun LoadHomeFragment(){
        supportFragmentManager.beginTransaction().replace(R.id.container, Dashboard(),AppConstant.HomeFragment).commit()
    }
}

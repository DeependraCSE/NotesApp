package com.thegamechanger.notes.Fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thegamechanger.notes.Activity.MainActivity
import com.thegamechanger.notes.R

class Splash : Fragment()  {
    internal var r: Runnable?=null
    internal var handler: Handler?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.frag_splash, container,false)

        postDelay()

        return view
    }
    fun postDelay(){
        handler = Handler()
        r = Runnable {
            startActivity(Intent(context, MainActivity::class.java))
            activity!!.finish()
        }
        handler!!.postDelayed(r, 2000)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        handler!!.removeCallbacks(r)
    }
}

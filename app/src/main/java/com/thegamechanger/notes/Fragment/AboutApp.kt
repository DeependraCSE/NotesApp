package com.thegamechanger.notes.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.thegamechanger.notes.BuildConfig
import com.thegamechanger.notes.Helper.AppConstant
import com.thegamechanger.notes.R

class AboutApp : Fragment() {
    lateinit var tv_app_virsion : TextView
    lateinit var btn_feedback : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.frag_about_app, container,false)
        tv_app_virsion = view.findViewById(R.id.tv_app_virsion)
        val versionName = BuildConfig.VERSION_NAME
        tv_app_virsion.setText("" + versionName)

        btn_feedback = view.findViewById(R.id.btn_feedback);
        btn_feedback.setOnClickListener(View.OnClickListener { SendMailFeedback() })
        return view
    }

    fun SendMailFeedback(){
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                AppConstant.APP_MAIL, null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback Notes")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Team,")
        startActivity(Intent.createChooser(emailIntent, getString(R.string.app_name)))
    }
}
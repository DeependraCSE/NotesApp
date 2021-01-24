package com.thegamechanger.notes.Fragment

import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.thegamechanger.notes.Adapter.NotesAdpt
import com.thegamechanger.notes.Helper.AppConstant
import com.thegamechanger.notes.Helper.SharedData
import com.thegamechanger.notes.LocalDatabase.ViewModel.NotesViewModel
import com.thegamechanger.notes.R
import com.thegamechanger.notes.Interface.OnAdptItemClickListner
import com.thegamechanger.notes.LocalDatabase.Classess.Notes

class Dashboard :Fragment() {
    private lateinit var notesViewModel: NotesViewModel
    lateinit var adapter : NotesAdpt
    lateinit var searchView : SearchView
    lateinit var myContext : Context
    private var myNotes = emptyList<Notes>()
    var ll_no_data : LinearLayout?=null
    var ll_data : LinearLayout?=null
    var type : Int =0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.frag_notes, container,false)
        myContext = requireContext()
        adapter = NotesAdpt(myContext,onAdptItemClickListner)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        ll_no_data = view.findViewById<LinearLayout>(R.id.ll_no_data)
        ll_data = view.findViewById<LinearLayout>(R.id.ll_data)

        notesViewModel = ViewModelProviders.of(this).get(NotesViewModel::class.java)
        CheckPassword()
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_dashboard, menu)
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        if (searchItem != null) {
            searchView = MenuItemCompat.getActionView(searchItem) as SearchView
//            val searchManager = myContext.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName))
            searchView.setOnCloseListener(object : SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    searchView.onActionViewCollapsed()
                    return true
                }
            })

            val searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
            searchPlate.hint = "Title, Notes"
            val searchPlateView: View = searchView.findViewById(androidx.appcompat.R.id.search_plate)
            searchPlateView.setBackgroundColor(ContextCompat.getColor(myContext,android.R.color.transparent))

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {// do your logic here                Toast.makeText(applicationContext, query, Toast.LENGTH_SHORT).show()
                    adapter.filter.filter(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return false
                }
            })

        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item!!.itemId
        if (id == R.id.add){
            fragmentManager!!.beginTransaction().replace(R.id.container, CreateEditNotes()).addToBackStack(null).commit()
        }else if (id == R.id.all_notes){
            PasswordDialog()
        }else if (id == R.id.change_password){
            ChangePasswordDialog(true)
        }else if (id == R.id.comp_sugg){
            SendMailComplaintSuggestion()
        }else if (id == R.id.share_app){
            ShareApp()
        }else if (id == R.id.rate_us){
            RateUs()
        }else if (id == R.id.about_app){
            fragmentManager!!.beginTransaction().replace(R.id.container, AboutApp()).addToBackStack(null).commit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        ( requireContext() as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
        SetAdapter()
        super.onResume()
    }

    fun SetAdapter(){
        var data : LiveData<List<Notes>> = notesViewModel.getNotes(type)
        data.observe(this, Observer { notes ->
            notes?.let {
                adapter.setNotes(it)
                myNotes = it
                if (it.size > 0){
                    ll_data?.setVisibility(View.VISIBLE)
                    ll_no_data?.setVisibility(View.GONE)
                }else{
                    ll_data?.setVisibility(View.GONE)
                    ll_no_data?.setVisibility(View.VISIBLE)
                }
            }
        })
    }

    var onAdptItemClickListner: OnAdptItemClickListner = object : OnAdptItemClickListner {
        override fun OnAdptItemClick(possition: Int) {
            var fragment = CreateEditNotes()
            var bundal = Bundle()
            bundal.putInt(AppConstant.NotesTableId,myNotes[possition].table_id)
            fragment.setArguments(bundal)
            fragmentManager!!.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit()
        }
    }

    fun CheckPassword(){
        var context : Context = requireContext()
        var sharedData = SharedData(context)
        var password = sharedData.getPassword()
        if (null == password){
            ChangePasswordDialog(false)
        }
    }

    fun ChangePasswordDialog(isCancelable : Boolean){
        var context : Context = requireContext()
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(isCancelable)
        dialog.setContentView(R.layout.dialog_change_password)
        dialog.window.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        dialog.window.setBackgroundDrawable(null)
        val lp = dialog.window.attributes
        lp.dimAmount = 0.0f // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.window.attributes = lp

        val til_old = dialog.findViewById(R.id.til_old) as TextInputLayout
        val et_old_password = dialog.findViewById(R.id.et_old_password) as EditText
        val et_new_password = dialog .findViewById(R.id.et_new_password) as EditText
        val btn_submit = dialog .findViewById(R.id.btn_submit) as Button
        if (!isCancelable)
            til_old.setVisibility(View.GONE)
        btn_submit.setOnClickListener {
            var old = et_old_password.getText().toString()
            var new = et_new_password.getText().toString()
            if (null != new && new.length > 3){
                var sharedData = SharedData(context)
                var response = sharedData.setPassword(old,new)
                if (response == AppConstant.PasswordChanged){
                    Toast.makeText(context,"Password Changed Successfully.",Toast.LENGTH_LONG).show()
                    dialog .dismiss()
                }else if (response == AppConstant.PasswordMismatch){
                    Toast.makeText(context,"Old Password Mismatch.",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context,"Some Error Occured ,Try After Some Time.",Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }else{
                Toast.makeText(context,"New password minimum length is 4",Toast.LENGTH_LONG).show()
            }
        }
        dialog .show()
    }

    fun PasswordDialog(){
        var context : Context = requireContext()
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_password)
        dialog.window.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        dialog.window.setBackgroundDrawable(null)
        val lp = dialog.window.attributes
        lp.dimAmount = 0.0f // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.window.attributes = lp

        val et_password = dialog .findViewById(R.id.et_password) as EditText
        val btn_submit = dialog .findViewById(R.id.btn_submit) as Button
        btn_submit.setOnClickListener {
            var password = et_password.getText().toString()
            if (null != password && password.length > 0){
                var sharedData = SharedData(context)
                var response = sharedData.getPassword()
                if (password.equals(response)){
                    type = AppConstant.TypePrivateKey
                    SetAdapter()
                    dialog .dismiss()
                }else {
                    Toast.makeText(context,"Password Mismatch.",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(context,"Password can't be empty",Toast.LENGTH_LONG).show()
            }
        }
        dialog .show()
    }

    fun ShareApp(){
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.str_share_app) + " " + AppConstant.APP_LINK)
        startActivity(Intent.createChooser(shareIntent,"Share " + getString(R.string.app_name)))
    }

    fun RateUs(){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context!!.packageName)))
    }

    fun SendMailComplaintSuggestion(){
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",AppConstant.APP_MAIL, null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Complaint & Suggestion Notes")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Team,")
        startActivity(Intent.createChooser(emailIntent, getString(R.string.app_name)))
    }
}
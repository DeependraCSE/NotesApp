package com.thegamechanger.notes.Fragment

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.thegamechanger.notes.Activity.MyCameraClickImage
import com.thegamechanger.notes.Adapter.MyImagesAdpt
import com.thegamechanger.notes.Adapter.SpinnerAdpt
import com.thegamechanger.notes.Classes.SpinnerClass
import com.thegamechanger.notes.Helper.AppConstant
import com.thegamechanger.notes.Helper.CommanHelper
import com.thegamechanger.notes.Interface.OnAdptItemClickListner
import com.thegamechanger.notes.LocalDatabase.Classess.MyImages
import com.thegamechanger.notes.LocalDatabase.Classess.Notes
import com.thegamechanger.notes.LocalDatabase.ViewModel.MyImagesViewModel
import com.thegamechanger.notes.LocalDatabase.ViewModel.NotesViewModel
import com.thegamechanger.notes.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class CreateEditNotes : Fragment() {
    val  TAG = "CreateEditNotes"
    val sdf = SimpleDateFormat("dd-MMM-yy HH:mm")
    var sp_type : Spinner?=null
    var et_title : EditText?=null
    var et_notes : EditText?=null
    var recyclerView : RecyclerView?=null
    lateinit var adapter : MyImagesAdpt
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var myImagesViewModel: MyImagesViewModel
    var notesType = arrayListOf<SpinnerClass>()
    private var myImagesList = emptyList<MyImages>()
    var selectedPoss = 0
    var table_id : Int = 0
    val ClickImage : Int = 1
    val SaveImage : Int = 2
    var dialog : Dialog?=null

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.frag_create_edit_notes, container,false)
        val context  = requireContext()
        notesViewModel = ViewModelProviders.of(this).get(NotesViewModel::class.java)
        myImagesViewModel = ViewModelProviders.of(this).get(MyImagesViewModel::class.java)

        notesType.add(SpinnerClass(AppConstant.TypePublicKey,AppConstant.TypePublicName))
        notesType.add(SpinnerClass(AppConstant.TypePrivateKey,AppConstant.TypePrivateName))

        var type = arrayListOf<String>()
        for (item in notesType){
            type.add(item.name)
        }

        var spAdapter  = SpinnerAdpt(context,type)
        sp_type = view.findViewById<Spinner>(R.id.sp_type)
        sp_type?.adapter = spAdapter

        et_title = view.findViewById<EditText>(R.id.et_title)
        et_notes = view.findViewById<EditText>(R.id.et_notes)

        sp_type?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPoss = position
            }
        }

        sp_type?.setSelection(selectedPoss)

        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = MyImagesAdpt(context,onAdptItemClickListner)
        recyclerView!!.adapter = adapter


        var bundle = getArguments()
        if (null != bundle && bundle.containsKey(AppConstant.NotesTableId)){
            ( requireContext() as AppCompatActivity).supportActionBar?.title = "Edit Notes"
            table_id = bundle.getInt(AppConstant.NotesTableId)
            SetData(table_id)
            SetAdapter(table_id)
        }else{
            ( requireContext() as AppCompatActivity).supportActionBar?.title = "Create Notes"
        }
        return view
    }

    fun SaveNotes(){
        val currentDate = sdf.format(Date())
        val timeInMS = System.currentTimeMillis()
        if (!CommanHelper.isEmptyEditText(et_title) &&
            !CommanHelper.isEmptyEditText(et_notes)){
            val strTitle:String = et_title?.text.toString()
            val strNotes:String = et_notes?.text.toString()

            var item = notesType[selectedPoss]
            if (table_id == 0){
                var notes = Notes(item.id , item.name , strTitle, strNotes,currentDate,timeInMS,currentDate,timeInMS)
                notesViewModel.insertNotes(notes)
            }else{
                var MyNotes = notesViewModel.getSingleNotes(table_id)
                var notes = Notes(item.id , item.name , strTitle, strNotes,MyNotes.created_at,MyNotes.created_at_ms,currentDate,timeInMS)
                notes.table_id = MyNotes.table_id

                notesViewModel.updateNotes(notes)
            }

            if (item.id == AppConstant.TypePrivateKey){
                Toast.makeText(context,"Saved, Check Menu -> Show All Notes",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context,"Saved",Toast.LENGTH_LONG).show()
            }

            fragmentManager!!.popBackStack()
        }
    }

    fun SetData(table_id : Int){
        var notes = notesViewModel.getSingleNotes(table_id)
        et_title!!.setText(notes.title)
        et_notes!!.setText(notes.notes)

        var type_id : Int  = notes.type_id
        if (type_id == AppConstant.TypePublicKey){
            sp_type!!.setSelection(0)
        }else{
            sp_type!!.setSelection(1)
        }
    }

    fun DeleteNotes(table_id : Int){
        val activity = activity as Context
        AlertDialog.Builder(activity)
            .setTitle("Delete...???")
            .setMessage("Do you want to delete it, Action cann't be undo.")
            .setCancelable(true)
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Delete") { dialogInterface, i ->
                notesViewModel.deleteSingleNotes(table_id)
                myImagesViewModel.deleteNotesMyImages(table_id)
                fragmentManager!!.popBackStack()
            }
            .setIcon(R.drawable.ic_delete_red)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_add_edit_notes, menu)
        if (table_id == 0){
            var itemDelete = menu?.findItem(R.id.delete)
            itemDelete?.setVisible(false)
            var itemAdd_a_photo = menu?.findItem(R.id.add_a_photo)
            itemAdd_a_photo?.setVisible(false)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item!!.itemId
        if (id == R.id.save){
            SaveNotes()
        }else if(id == R.id.delete){
            DeleteNotes(table_id)
        }else if(id == R.id.add_a_photo){
            CheckPermissionClickImage(ClickImage)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun CheckPermissionClickImage(click : Int) : Boolean{
        var isPerGra = false
        if (ActivityCompat.checkSelfPermission(context!!,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context!!,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE), click)
        } else {
            if (click == ClickImage)
                ClickImage(click)
            isPerGra = true
        }
        return isPerGra
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ClickImage) {
            CheckPermissionClickImage(requestCode)
        }
    }

    fun ClickImage(click : Int){
        val intent = Intent(context, MyCameraClickImage::class.java)
        startActivityForResult(intent, click)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == ClickImage) {
            try {
                val bundle = data!!.extras
                val filePath = bundle!!.getString(AppConstant.MEDIA_PATH)
                InsertImage(filePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun InsertImage(filepath : String){
        val currentDate = sdf.format(Date())
        val timeInMS = System.currentTimeMillis()
        var myImages = MyImages(table_id,filepath,currentDate,timeInMS)
        myImagesViewModel.insertMyImages(myImages)
    }

    var onAdptItemClickListner: OnAdptItemClickListner = object : OnAdptItemClickListner {
        override fun OnAdptItemClick(possition: Int) {
            DisplayImage(myImagesList[possition])
        }
    }

    fun SetAdapter(notes_table_id : Int){
        val data : LiveData<List<MyImages>> = myImagesViewModel.getAllMyImages(notes_table_id)
        data.observe(this, Observer { myImages ->
            myImages?.let {
                adapter.setMyImages(it)
                myImagesList = it
                if (it.size > 0){
                    recyclerView?.setVisibility(View.VISIBLE)
                }else{
                    recyclerView?.setVisibility(View.GONE)
                }
            }
        })
    }

    fun DisplayImage(myImages: MyImages){
        var notes : Notes = notesViewModel.getSingleNotes(table_id)
        var context : Context = requireContext()
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
        dialog!!.setContentView(R.layout.dialog_diaplay_image)
        dialog!!.window.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        dialog!!.window.setBackgroundDrawable(null)
        val lp = dialog!!.window.attributes
        lp.dimAmount = 0.0f // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog!!.window.attributes = lp


        val photo_view = dialog!!.findViewById(R.id.photo_view) as ImageView
        val attacher = PhotoViewAttacher(photo_view)
        attacher.update()

        var imgFile =   File(myImages.saved_image)
        if(imgFile.exists()){
            var myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
            photo_view.setImageBitmap(myBitmap)
        }else{
            photo_view.setImageDrawable(resources.getDrawable(R.drawable.ic_notepad))
            Toast.makeText(context,"Image Doesn't Exist",Toast.LENGTH_LONG).show()
        }


        var ll_option = dialog!!.findViewById<LinearLayout>(R.id.ll_option)
        /*photo_view.setOnClickListener(View.OnClickListener {
            if (ll_option.getVisibility() == View.VISIBLE){
                ll_option.setVisibility(View.GONE)
            }else{
                ll_option.setVisibility(View.VISIBLE)
            }
        })*/

        var iv_back = dialog!!.findViewById(R.id.iv_back) as ImageView
        iv_back.setOnClickListener(View.OnClickListener {
            dialog!!.dismiss()
        })

        var tv_title = dialog!!.findViewById<TextView>(R.id.tv_title)
        tv_title.setText("" + notes.title)

        var tv_sub_title = dialog!!.findViewById<TextView>(R.id.tv_sub_title)
        tv_sub_title.setText("" + myImages.created_at)

        var iv_delete = dialog!!.findViewById(R.id.iv_delete) as ImageView
        iv_delete.setOnClickListener(View.OnClickListener {
            DeleteImage(myImages)
        })

        var iv_save_to_gallery = dialog!!.findViewById(R.id.iv_save_to_gallery) as ImageView
        iv_save_to_gallery.setOnClickListener(View.OnClickListener {
            AddImageToGallery(myImages)
        })

        dialog!!.show()
    }

    fun DeleteImage(myImages: MyImages){
        val activity = activity as Context
        AlertDialog.Builder(activity)
            .setTitle("Delete...???")
            .setMessage("Do you want to delete it, Action cann't be undo.")
            .setCancelable(true)
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Delete") { dialogInterface, i ->
                myImagesViewModel.deleteSingleMyImages(myImages.table_id)
                var file = File(myImages.saved_image)
                if (file.exists())
                    file.delete()
                if (dialog!!.isShowing){
                    dialog!!.dismiss()
                }
            }
            .setIcon(R.drawable.ic_delete_red)
            .show()
    }

    fun AddImageToGallery(myImages: MyImages){
        var imgFile =   File(myImages.saved_image)
        if(imgFile.exists()){
            if (CheckPermissionClickImage(SaveImage)){
                var file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),context!!.getString(R.string.app_name) + "_IMG_" + myImages.created_at_ms + "_" + System.currentTimeMillis() + ".png")

                var fin = FileInputStream(imgFile)
                var fout = FileOutputStream(file)

                var buf = ByteArray(1024)
                var len = 0
                while(fin.read(buf).also { len = it } >=0) {
                    fout.write(buf, 0, len)
                }
                fin.close()
                fout.close()


                val value = ContentValues()
                value.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                value.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                value.put(MediaStore.MediaColumns.DATA, file.absolutePath)
                context!!.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)

                Toast.makeText(context!!,"File saved successfully",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context!!,"Allow Permissions And Try Again",Toast.LENGTH_LONG).show()
            }

        }else{
            Toast.makeText(context,"Image Doesn't Exist",Toast.LENGTH_LONG).show()
        }
    }
}
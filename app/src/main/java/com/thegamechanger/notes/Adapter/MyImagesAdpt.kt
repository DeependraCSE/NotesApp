package com.thegamechanger.notes.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.thegamechanger.notes.Interface.OnAdptItemClickListner
import com.thegamechanger.notes.LocalDatabase.Classess.MyImages
import com.thegamechanger.notes.R
import java.io.File

class MyImagesAdpt internal constructor(
    context: Context,onAdptItemClickListner: OnAdptItemClickListner
) : RecyclerView.Adapter<MyImagesAdpt.MyImagesViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var originalMyImages = emptyList<MyImages>()
    private var onAdptItemClickListner = onAdptItemClickListner;

    inner class MyImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_my_images: ImageView = itemView.findViewById(R.id.iv_my_images)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImagesViewHolder {
        val itemView = inflater.inflate(R.layout.adpt_my_images, parent, false)
        return MyImagesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyImagesViewHolder, position: Int) {
        val current = originalMyImages[position]
        var imgFile =   File(current.saved_image)
        if(imgFile.exists()){
            var myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
            holder.iv_my_images.setImageBitmap(myBitmap)
        }else{
            holder.iv_my_images.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_notepad))
        }

        holder.itemView.setOnClickListener(View.OnClickListener {
            onAdptItemClickListner.OnAdptItemClick(position)
        })
    }

    internal fun setMyImages(originalMyImages: List<MyImages>) {
        this.originalMyImages = originalMyImages
        notifyDataSetChanged()
    }

    override fun getItemCount() = originalMyImages.size
}
package com.thegamechanger.notes.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.thegamechanger.notes.Helper.AppConstant
import com.thegamechanger.notes.R

class SpinnerAdpt(context : Context,val datas : List<String>) : BaseAdapter() {

    val inflater : LayoutInflater= LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.adpt_spinner, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }
        vh.tv_text.text = datas.get(position)
        var s = datas.get(position)
        if(s.equals(AppConstant.TypePrivateName)){
            vh.ll_type.setBackgroundColor(Color.parseColor("#FF0000"))
        }else{
            vh.ll_type.setBackgroundColor(Color.parseColor("#00FF00"))
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return datas.get(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return datas.size
    }

    private class ItemRowHolder(row: View?) {
        val tv_text: TextView
        val ll_type: LinearLayout
        init {
            this.tv_text = row?.findViewById(R.id.tv_text) as TextView
            this.ll_type = row?.findViewById(R.id.ll_type) as LinearLayout
        }
    }
}
package com.thegamechanger.notes.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.thegamechanger.notes.Helper.AppConstant
import com.thegamechanger.notes.Interface.OnAdptItemClickListner
import com.thegamechanger.notes.LocalDatabase.Classess.Notes
import com.thegamechanger.notes.R

class NotesAdpt internal constructor(
    context: Context,onAdptItemClickListner: OnAdptItemClickListner
) : RecyclerView.Adapter<NotesAdpt.NotesViewHolder>(), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var originalNotes = emptyList<Notes>()
    private var filteredNotes = emptyList<Notes>()
    private var onAdptItemClickListner = onAdptItemClickListner;

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_title: TextView = itemView.findViewById(R.id.tv_title)
        val tv_notes: TextView = itemView.findViewById(R.id.tv_notes)
        val tv_created_at: TextView = itemView.findViewById(R.id.tv_created_at)
        val tv_modify_at: TextView = itemView.findViewById(R.id.tv_modify_at)
        val ll_type: LinearLayout = itemView.findViewById(R.id.ll_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView = inflater.inflate(R.layout.adpt_notes_list, parent, false)
        return NotesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val current = filteredNotes[position]
        holder.tv_title.text = current.title
        holder.tv_notes.text = current.notes
        holder.tv_created_at.text = "Created:"+current.created_at
        holder.tv_modify_at.text = "Modify:"+current.last_modify

        if(current.type_id == AppConstant.TypePrivateKey){
            holder.ll_type.setBackgroundColor(Color.parseColor("#FF0000"))
        }else{
            holder.ll_type.setBackgroundColor(Color.parseColor("#00FF00"))
        }

        holder.itemView.setOnClickListener(View.OnClickListener {
            onAdptItemClickListner.OnAdptItemClick(position)
        })
    }

    internal fun setNotes(notes: List<Notes>) {
        this.originalNotes = notes
        this.filteredNotes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = filteredNotes.size

    override fun getFilter(): Filter {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filteredNotes = originalNotes
                } else {
                    val filteredList: ArrayList<Notes> = ArrayList()
                    for (row in originalNotes) {
                        if (row.title.toLowerCase().contains(charString.toLowerCase()) ||
                            row.notes.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredNotes = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = filteredNotes
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence,filterResults: Filter.FilterResults) {
                filteredNotes = filterResults.values as ArrayList<Notes>
                notifyDataSetChanged()
            }
        }
    }
}
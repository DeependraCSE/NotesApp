package com.thegamechanger.notes.LocalDatabase.Repository

import androidx.lifecycle.LiveData
import com.thegamechanger.notes.LocalDatabase.Classess.Notes
import com.thegamechanger.notes.LocalDatabase.DAO.NotesDao
import android.os.AsyncTask
import android.util.Log
import com.thegamechanger.notes.Helper.AppConstant

class NotesRepository(private val notesDao: NotesDao)  {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    //val allRepositoryNotes: LiveData<List<Notes>> = GetNotes(0)

    fun GetNotes(type : Int) : LiveData<List<Notes>>{
        return getNotesAsyncTask(notesDao).execute(type).get()
    }

    suspend fun insertNotes(notes: Notes) {
        notesDao.insertNotes(notes)
    }

    suspend fun updateNotes(notes: Notes) {
        notesDao.updateNotes(notes)
    }

    fun deleteSingleNotes(id : Int){
        deleteSingleNotesAsyncTask(notesDao).execute(id)
    }

    fun getSingleNotes(id : Int) : Notes{
        var notes : Notes = getSingleNotesAsyncTask(notesDao).execute(id).get()
        return notes
    }

    private class deleteSingleNotesAsyncTask internal constructor(private val notesDao: NotesDao) :
        AsyncTask<Int, Unit, Unit>() {
        override fun doInBackground(vararg params: Int?) {
            var table_id = params[0]!!
            notesDao.deleteSingleNotes(table_id)
        }
    }

    private class getSingleNotesAsyncTask internal constructor(private val notesDao: NotesDao) :
        AsyncTask<Int, Unit, Notes>() {
        override fun doInBackground(vararg params: Int?) : Notes {
            var table_id = params[0]!!
            var notes : Notes = notesDao.getSingleNotes(table_id)
            return  notes
        }
    }

    private class getNotesAsyncTask internal constructor(private val notesDao: NotesDao) :
        AsyncTask<Int, Unit, LiveData<List<Notes>>>() {
        override fun doInBackground(vararg params: Int?): LiveData<List<Notes>> {
            var type = params[0]!!
            if (type == AppConstant.TypePrivateKey){
                return notesDao.getAllNotes()
            }else{
                return notesDao.getAllPublicNotes()
            }
        }
    }
}
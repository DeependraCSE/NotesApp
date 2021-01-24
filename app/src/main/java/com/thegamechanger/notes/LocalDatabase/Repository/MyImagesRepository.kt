package com.thegamechanger.notes.LocalDatabase.Repository

import androidx.lifecycle.LiveData
import android.os.AsyncTask
import com.thegamechanger.notes.LocalDatabase.Classess.MyImages
import com.thegamechanger.notes.LocalDatabase.DAO.MyImageDao

class MyImagesRepository(private val myImagesDao: MyImageDao)  {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    //val allRepositoryNotes: LiveData<List<Notes>> = GetNotes(0)

    fun GetAllMyImages(notes_table_id : Int) : LiveData<List<MyImages>>{
        return getAllMyImagesAsyncTask(myImagesDao).execute(notes_table_id).get()
    }

    suspend fun InsertMyImages(myImages: MyImages) {
        myImagesDao.insertMyImages(myImages)
    }

    fun DeleteSingleMyImages(table_id : Int){
        deleteSingleNotesAsyncTask(myImagesDao).execute(table_id)
    }

    fun DeleteNotesMyImages(notes_table_id : Int){
        deleteNotesMyImagesAsyncTask(myImagesDao).execute(notes_table_id)
    }

    private class deleteSingleNotesAsyncTask internal constructor(private val myImagesDao: MyImageDao) :
        AsyncTask<Int, Unit, Unit>() {
        override fun doInBackground(vararg params: Int?) {
            var table_id = params[0]!!
            myImagesDao.deleteSingleMyImages(table_id)
        }
    }

    private class deleteNotesMyImagesAsyncTask internal constructor(private val myImagesDao: MyImageDao) :
        AsyncTask<Int, Unit, Unit>() {
        override fun doInBackground(vararg params: Int?) {
            var notes_table_id = params[0]!!
            myImagesDao.deleteNotesMyImages(notes_table_id)
        }
    }

    private class getAllMyImagesAsyncTask internal constructor(private val myImagesDao: MyImageDao) :
        AsyncTask<Int, Unit, LiveData<List<MyImages>>>() {
        override fun doInBackground(vararg params: Int?): LiveData<List<MyImages>> {
            var notes_table_id = params[0]!!
            return myImagesDao.getAllMyImages(notes_table_id)
        }
    }
}
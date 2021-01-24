package com.thegamechanger.notes.LocalDatabase.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.thegamechanger.notes.LocalDatabase.Classess.MyImages
import com.thegamechanger.notes.LocalDatabase.Repository.MyImagesRepository
import com.thegamechanger.notes.LocalDatabase.RoomDBFile
import kotlinx.coroutines.launch

class MyImagesViewModel(application: Application) : AndroidViewModel(application) {
    private val myImagesRepository : MyImagesRepository

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val myImageDao = RoomDBFile.getDatabase(application, viewModelScope).myImagesDao()
        myImagesRepository = MyImagesRepository(myImageDao)
        //allNotes = notesRepository.AllNotes()
    }

    // The implementation of insert() is completely hidden from the UI.
    // We don't want insert to block the main thread, so we're launching a new
    // coroutine. ViewModels have a coroutine scope based on their lifecycle called
    // viewModelScope which we can use here.
    fun insertMyImages(myImages: MyImages) = viewModelScope.launch {
        myImagesRepository.InsertMyImages(myImages)
    }

    fun getAllMyImages(notes_table_id : Int) : LiveData<List<MyImages>> {
        return myImagesRepository.GetAllMyImages(notes_table_id)
    }

    fun deleteSingleMyImages(table_id: Int) = viewModelScope.launch {
        myImagesRepository.DeleteSingleMyImages(table_id)
    }

    fun deleteNotesMyImages(notes_table_id: Int) = viewModelScope.launch {
        myImagesRepository.DeleteNotesMyImages(notes_table_id)
    }

}
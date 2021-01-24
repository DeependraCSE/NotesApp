package com.thegamechanger.notes.LocalDatabase.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.thegamechanger.notes.LocalDatabase.Classess.Notes
import com.thegamechanger.notes.LocalDatabase.Repository.NotesRepository
import com.thegamechanger.notes.LocalDatabase.RoomDBFile
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    // The ViewModel maintains a reference to the repository to get data.
    private val notesRepository : NotesRepository
    // LiveData gives us updated words when they change.
    //var allNotes: LiveData<List<Notes>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val notesDao = RoomDBFile.getDatabase(application, viewModelScope).notesDao()
        notesRepository = NotesRepository(notesDao)
        //allNotes = notesRepository.AllNotes()
    }

    // The implementation of insert() is completely hidden from the UI.
    // We don't want insert to block the main thread, so we're launching a new
    // coroutine. ViewModels have a coroutine scope based on their lifecycle called
    // viewModelScope which we can use here.
    fun insertNotes(notes: Notes) = viewModelScope.launch {
        notesRepository.insertNotes(notes)
    }

    fun getNotes(type : Int) : LiveData<List<Notes>> {
        return notesRepository.GetNotes(type)
    }

    fun updateNotes(notes: Notes) = viewModelScope.launch {
        notesRepository.updateNotes(notes)
    }

    fun deleteSingleNotes(id: Int) = viewModelScope.launch {
        notesRepository.deleteSingleNotes(id)
    }

    fun getSingleNotes(id: Int):Notes {
        val notes = notesRepository.getSingleNotes(id)
        return notes
    }

}
package com.thegamechanger.notes.LocalDatabase.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.thegamechanger.notes.Helper.AppConstant
import com.thegamechanger.notes.LocalDatabase.Classess.Notes
import com.thegamechanger.notes.LocalDatabase.DBConfig

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotes(notes: Notes)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateNotes(notes: Notes)

    @Query("DELETE FROM "+DBConfig.TABLE_NOTES)
    suspend fun deleteAllNotes()

//    @Query("SELECT * FROM " + DBConfig.TABLE_NOTES + " ORDER BY :columnName :ASC_DESC")
//    fun getAllNotes(columnName:String, ASC_DESC:String): LiveData<List<Notes>>
    @Query("SELECT * FROM " + DBConfig.TABLE_NOTES + " ORDER BY " + DBConfig.COLUMN_LAST_MODIFY_MS +" DESC")
    fun getAllNotes(): LiveData<List<Notes>>

    @Query("SELECT * FROM " + DBConfig.TABLE_NOTES + " WHERE " + DBConfig.COLUMN_TYPE_ID + " = " + AppConstant.TypePublicKey + " ORDER BY " + DBConfig.COLUMN_LAST_MODIFY_MS +" DESC")
    fun getAllPublicNotes(): LiveData<List<Notes>>

    @Query("SELECT * FROM " + DBConfig.TABLE_NOTES + " WHERE " + DBConfig.COLUMN_TABLE_ID + " =:id")
    fun getSingleNotes(id : Int): Notes

    @Query("DELETE FROM "+DBConfig.TABLE_NOTES + " WHERE " + DBConfig.COLUMN_TABLE_ID + " =:id" )
    fun deleteSingleNotes(id : Int)
}
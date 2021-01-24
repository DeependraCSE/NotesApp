package com.thegamechanger.notes.LocalDatabase.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.thegamechanger.notes.Helper.AppConstant
import com.thegamechanger.notes.LocalDatabase.Classess.MyImages
import com.thegamechanger.notes.LocalDatabase.DBConfig

@Dao
interface MyImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMyImages(myImages: MyImages)

    @Query("SELECT * FROM " + DBConfig.TABLE_IMAGES + " WHERE " + DBConfig.COLUMN_NOTES_TABLE_ID + " =:notes_table_id")
    fun getAllMyImages(notes_table_id : Int): LiveData<List<MyImages>>

    @Query("DELETE FROM "+DBConfig.TABLE_IMAGES + " WHERE " + DBConfig.COLUMN_TABLE_ID + " =:table_id" )
    fun deleteSingleMyImages(table_id : Int)

    @Query("DELETE FROM "+DBConfig.TABLE_IMAGES + " WHERE " + DBConfig.COLUMN_NOTES_TABLE_ID + " =:notes_table_id" )
    fun deleteNotesMyImages(notes_table_id : Int)
}
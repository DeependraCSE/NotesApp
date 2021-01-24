package com.thegamechanger.notes.LocalDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thegamechanger.notes.LocalDatabase.Classess.MyImages
import com.thegamechanger.notes.LocalDatabase.Classess.Notes
import com.thegamechanger.notes.LocalDatabase.DAO.MyImageDao
import com.thegamechanger.notes.LocalDatabase.DAO.NotesDao
import kotlinx.coroutines.CoroutineScope

@Database(entities = arrayOf(Notes::class,MyImages::class), version = 1, exportSchema = false)
abstract class RoomDBFile : RoomDatabase(){

    abstract fun notesDao(): NotesDao
    abstract fun myImagesDao(): MyImageDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDBFile? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): RoomDBFile {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDBFile::class.java,
                    DBConfig.DB_NAME
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
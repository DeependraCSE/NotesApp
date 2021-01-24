package com.thegamechanger.notes.LocalDatabase.Classess

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thegamechanger.notes.LocalDatabase.DBConfig
import java.io.Serializable

@Entity(tableName = DBConfig.TABLE_IMAGES)
class MyImages(@ColumnInfo(name = DBConfig.COLUMN_NOTES_TABLE_ID) val notes_table_id: Int,
               @ColumnInfo(name = DBConfig.COLUMN_IMAGES) val saved_image: String,
               @ColumnInfo(name = DBConfig.COLUMN_CREATED_AT) val created_at: String,
               @ColumnInfo(name = DBConfig.COLUMN_CREATED_AT_MS) val created_at_ms: Long) : Serializable{
    @PrimaryKey(autoGenerate = true)
    var table_id : Int = 0
}
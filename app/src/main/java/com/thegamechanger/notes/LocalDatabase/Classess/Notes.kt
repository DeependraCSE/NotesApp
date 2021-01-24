package com.thegamechanger.notes.LocalDatabase.Classess

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thegamechanger.notes.LocalDatabase.DBConfig
import java.io.Serializable

@Entity(tableName = DBConfig.TABLE_NOTES)
class Notes(@ColumnInfo(name = DBConfig.COLUMN_TYPE_ID) val type_id: Int,
            @ColumnInfo(name = DBConfig.COLUMN_TYPE_NAME) val type_name: String,
            @ColumnInfo(name = DBConfig.COLUMN_TITLE) val title: String,
            @ColumnInfo(name = DBConfig.COLUMN_NOTES) val notes: String,
            @ColumnInfo(name = DBConfig.COLUMN_CREATED_AT) val created_at: String,
            @ColumnInfo(name = DBConfig.COLUMN_CREATED_AT_MS) val created_at_ms: Long,
            @ColumnInfo(name = DBConfig.COLUMN_LAST_MODIFY) val last_modify: String,
            @ColumnInfo(name = DBConfig.COLUMN_LAST_MODIFY_MS) val last_modify_ms: Long) : Serializable{
    @PrimaryKey(autoGenerate = true)
    var table_id : Int = 0
}
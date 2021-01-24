package com.thegamechanger.notes.LocalDatabase

class DBConfig{
    companion object{
        const val DB_NAME = "notes.db"

        const val TABLE_NOTES = "notes"
        const val COLUMN_TABLE_ID = "table_id"
        const val COLUMN_TYPE_ID = "type_id"
        const val COLUMN_TYPE_NAME = "type_name"
        const val COLUMN_TITLE = "title"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_CREATED_AT_MS = "created_at_ms"
        const val COLUMN_LAST_MODIFY = "last_modify"
        const val COLUMN_LAST_MODIFY_MS = "last_modify_ms"

        const val TABLE_IMAGES = "images"
        //const val COLUMN_TABLE_ID = "table_id"
        const val COLUMN_NOTES_TABLE_ID = "notes_table_id"
        const val COLUMN_IMAGES = "saved_image"
        //const val COLUMN_CREATED_AT = "created_at"
        //const val COLUMN_CREATED_AT_MS = "created_at_ms"
    }
}
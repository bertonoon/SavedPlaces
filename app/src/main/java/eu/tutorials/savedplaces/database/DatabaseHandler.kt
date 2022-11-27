package eu.tutorials.savedplaces.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import eu.tutorials.savedplaces.models.SavedPlaceModel


class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Database version
        private const val DATABASE_NAME = "SavedPlacesDatabase" // Database name
        private const val TABLE_SAVED_PLACE = "SavedPlacesTable" // Table Name

        //All the Columns names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_SAVED_PLACE_TABLE = ("CREATE TABLE " + TABLE_SAVED_PLACE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_SAVED_PLACE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_SAVED_PLACE")
        onCreate(db)
    }


    fun addSavedPlace(savedPlace: SavedPlaceModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, savedPlace.title) // HappyPlaceModelClass TITLE
        contentValues.put(KEY_IMAGE, savedPlace.image) // HappyPlaceModelClass IMAGE
        contentValues.put(KEY_DESCRIPTION, savedPlace.description) // HappyPlaceModelClass DESCRIPTION
        contentValues.put(KEY_DATE, savedPlace.date) // HappyPlaceModelClass DATE
        contentValues.put(KEY_LOCATION, savedPlace.location) // HappyPlaceModelClass LOCATION
        contentValues.put(KEY_LATITUDE, savedPlace.latitude) // HappyPlaceModelClass LATITUDE
        contentValues.put(KEY_LONGITUDE, savedPlace.longitude) // HappyPlaceModelClass LONGITUDE

        // Inserting Row
        val result = db.insert(TABLE_SAVED_PLACE, null, contentValues)

        db.close() // Closing database connection
        return result
    }

    fun updateSavedPlace(savedPlace: SavedPlaceModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, savedPlace.title) // HappyPlaceModelClass TITLE
        contentValues.put(KEY_IMAGE, savedPlace.image) // HappyPlaceModelClass IMAGE
        contentValues.put(KEY_DESCRIPTION, savedPlace.description) // HappyPlaceModelClass DESCRIPTION
        contentValues.put(KEY_DATE, savedPlace.date) // HappyPlaceModelClass DATE
        contentValues.put(KEY_LOCATION, savedPlace.location) // HappyPlaceModelClass LOCATION
        contentValues.put(KEY_LATITUDE, savedPlace.latitude) // HappyPlaceModelClass LATITUDE
        contentValues.put(KEY_LONGITUDE, savedPlace.longitude) // HappyPlaceModelClass LONGITUDE

        // Inserting Row
        val success = db.update(TABLE_SAVED_PLACE, contentValues, KEY_ID + "=" + savedPlace.id,null)

        db.close() // Closing database connection
        return success
    }


    @SuppressLint("Range")
    fun getPlacesList():ArrayList<SavedPlaceModel>{
        val placeList : ArrayList<SavedPlaceModel> = arrayListOf()
        val selectQuery = "SELECT * FROM $TABLE_SAVED_PLACE"
        val db = this.readableDatabase

        try{
            val cursor : Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()){
                do {
                    val place = SavedPlaceModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                    )
                    placeList.add(place)
                } while (cursor.moveToNext())
            }
            cursor.close()

        }catch(e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return placeList
    }

    fun deleteSavedPlace(savedPlace: SavedPlaceModel) : Int{
        val db = this.writableDatabase
        val success = db.delete(TABLE_SAVED_PLACE, KEY_ID +"=" + savedPlace.id,null)
        db.close()
        return success
    }

}

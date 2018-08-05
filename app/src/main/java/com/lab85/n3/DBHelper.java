package com.lab85.n3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.EditText;

import com.lab85.n3.note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "notes_db";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // create db
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(note.CREATE_TABLE);
    }

    // update db
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + note.TABLE_NAME);
        DATABASE_VERSION += 1;
        onCreate(db);
    }

    public boolean isEmpty(EditText et){
        return et.getText().toString().trim().length() == 0;
    }

    public String nowTime(){
        //        converts now time and date to string
        Date date = new java.util.Date();
        String dateString = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM . hh:mm a");

        try{
            dateString = formatter.format(date);
        }catch (Exception ex ){
            dateString = "unknown";
        }

        return dateString;
    }

    public long insertNote(String title, String regular){

        String time = nowTime();

        if(regular.isEmpty() && title.isEmpty()){
            title = "Empty note";
            regular = "you just created an empty note";
        }

        else if(title.isEmpty()){
            String timeOfDay = (time.substring(15,16).equals("A")) ? "Morning, " : "Evening, ";
//            timeOfDay += time.substring(15,16);
            title = timeOfDay + time.substring(0,6);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(note.COLUMN_NOTE, title);
        values.put(note.COLUMN_NOTE2, regular);
        values.put(note.COLUMN_TIMESTAMP, time);

        long id = db.insert(note.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public note getNote(long id){

        // exception for new note
        if(id == -1){
            return new note(-1, "", "");
        }

        // get readable version of database
        SQLiteDatabase db = this.getReadableDatabase();

        // some weird ass query
        Cursor cursor = db.query(note.TABLE_NAME,
                new String[]{note.COLUMN_ID, note.COLUMN_NOTE, note.COLUMN_NOTE2, note.COLUMN_TIMESTAMP},
                note.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
                );

        if (cursor != null)
            cursor.moveToFirst();

        // create a note object from db entry
        note Note = new note(
                cursor.getInt(cursor.getColumnIndex(note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(note.COLUMN_NOTE2)));

        cursor.close();

        return Note;
    }

    public List<note> getAllNotes(){

        List<note> noteList = new ArrayList<>();

        // select all query
        String selectQuery = "SELECT * FROM " + note.TABLE_NAME + " ORDER BY " + note.COLUMN_ID + " DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                note Note = new note();
                Note.setId(cursor.getInt(cursor.getColumnIndex(note.COLUMN_ID)));
                Note.setTitle(cursor.getString(cursor.getColumnIndex(note.COLUMN_NOTE)));
                Note.setRegular(cursor.getString(cursor.getColumnIndex(note.COLUMN_NOTE2)));
                Note.setTime(cursor.getString(cursor.getColumnIndex(note.COLUMN_TIMESTAMP)));

                noteList.add(Note);

            }while (cursor.moveToNext());
        }

        db.close();

        return noteList;
    }

    public List<note> getAllNotesForView(){

        List<note> noteList = new ArrayList<>();

        // select all query
        String selectQuery = "SELECT * FROM " + note.TABLE_NAME + " ORDER BY " + note.COLUMN_ID + " DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                note Note = new note();
                Note.setId(cursor.getInt(cursor.getColumnIndex(note.COLUMN_ID)));
                Note.setTitle(cursor.getString(cursor.getColumnIndex(note.COLUMN_NOTE)));
                String regular = cursor.getString(cursor.getColumnIndex(note.COLUMN_NOTE2));
                regular = regular.replace("\n", "  ").replace("\r", "  ");
                String cutoff = regular.substring(0, Math.min(regular.length(), 400));
                Note.setRegular(cutoff);
                Note.setTime(cursor.getString(cursor.getColumnIndex(note.COLUMN_TIMESTAMP)));

                noteList.add(Note);

            }while (cursor.moveToNext());
        }

        db.close();

        return noteList;
    }

    public int getNotesCount(){
        String countQuery = "SELECT * FROM " + note.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        return count;
    }

    public int updateNote(note Note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(note.COLUMN_NOTE, Note.getTitle());
        values.put(note.COLUMN_NOTE2, Note.getRegular());
        values.put(note.COLUMN_TIMESTAMP, nowTime());

        int id = db.update(note.TABLE_NAME, values, note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(Note.getId())});

        return id;

    }

    public void deleteNote(note Note){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(note.TABLE_NAME, note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(Note.getId())});
        db.close();
    }

    public ArrayList<note> search(String s){
        ArrayList<note> searchResult = new ArrayList<note>();

        String searchQuery = "SELECT * FROM "+ note.TABLE_NAME + " WHERE " + note.COLUMN_NOTE + " LIKE  '%"+s+"%' OR " + note.COLUMN_NOTE2 + " LIKE  '%"+s+"%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(searchQuery, null);

        if (cursor == null)
            return null;

        if(cursor.moveToFirst()){
            do{
                note Note = new note();
                Note.setId(cursor.getInt(cursor.getColumnIndex(note.COLUMN_ID)));
                Note.setTitle(cursor.getString(cursor.getColumnIndex(note.COLUMN_NOTE)));
                String regular = cursor.getString(cursor.getColumnIndex(note.COLUMN_NOTE2));
                regular = regular.replace("\n", "  ").replace("\r", "  ");
                String cutoff = regular.substring(0, Math.min(regular.length(), 200));
                Note.setRegular(cutoff);
                Note.setTime(cursor.getString(cursor.getColumnIndex(note.COLUMN_TIMESTAMP)));

                searchResult.add(Note);

            }while (cursor.moveToNext());
        }

        db.close();

        return searchResult;

    }
}

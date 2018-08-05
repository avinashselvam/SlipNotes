package com.lab85.n3;

import java.util.Date;
import java.text.SimpleDateFormat;

public class note {
    private String time;
    private int id;
    private String title;
    private String regular;

    public static final String TABLE_NAME = "notes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOTE = "title";
    public static final String COLUMN_NOTE2 = "regular";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_NOTE2 + " TEXT,"
                    + COLUMN_TIMESTAMP + " TEXT"
                    + ")";

    public note(){}

    public note(int id_, String title_, String regular_){

//      assigning values
        this.id = id_;
        this.time = nowTime();
        this.title = title_;
        this.regular = regular_;

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

    public int getId() {return this.id;}

    public String getTime(){
        return this.time;
    }

    public String getTitle(){
        return this.title;
    }

    public  String getRegular(){
        return this.regular;
    }

    public void setId(int i){
        this.id = i;
    }

    public void setTime(String S){
        this.time = S;
    }

    public void setTitle(String S){
        this.title = S;
    }

    public void setRegular(String S){
        this.regular = S;
    }

}

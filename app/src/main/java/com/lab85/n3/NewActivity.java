package com.lab85.n3;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class NewActivity extends AppCompatActivity {

    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_view);

        final Intent myIntent = new Intent(NewActivity.this, MainActivity.class);

        db = new DBHelper(this);

        final EditText editTitle = (EditText) findViewById(R.id.edit_title);
        editTitle.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editTitle.setRawInputType(InputType.TYPE_CLASS_TEXT);

        final EditText editReg = (EditText) findViewById(R.id.edit_regular);

        final int id = getIntent().getIntExtra("id",-1);

        final note currentNote = db.getNote(id);
        editTitle.setText(currentNote.getTitle());
        editReg.setText(currentNote.getRegular());

        TextView done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(id != -1){
                    int update = db.updateNote(new note(id, editTitle.getText().toString(), editReg.getText().toString()));
                    Snackbar.make(findViewById(R.id.done), String.valueOf(id), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();}

                else
                    db.insertNote(editTitle.getText().toString(), editReg.getText().toString());

                startActivity(myIntent);
            }
        });

        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(myIntent);
            }
        });

        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN)
                    v.setBackground(getResources().getDrawable(R.drawable.ic_lens_black_24dp));
                else if(event.getAction() == android.view.MotionEvent.ACTION_UP)
                    v.setBackground(null);

                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(NewActivity.this, MainActivity.class);
        startActivity(myIntent);
    }
}

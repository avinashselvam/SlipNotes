package com.lab85.n3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<note> noteList = new ArrayList<note>();
    ArrayList<note> notesToBeDeleted = new ArrayList<note>();
    ArrayList<note> notesResult = new ArrayList<note>();


    ArrayList<Integer> toBeDeleted = new ArrayList<Integer>();

    private DBHelper db;

    RotateAnimation rotate1 = new RotateAnimation(-135, 0,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f);

    RotateAnimation rotate2 = new RotateAnimation(135, 0,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        db = new DBHelper(this);

        int count = db.getNotesCount();
        if(count == 0){
//            db.insertNote("It's empty in here", "- tap the + button to create a new note \n - tap on any note to view/edit/delete \n - use the search bar to look up notes by date and title");
            final TextView instructions = (TextView) findViewById(R.id.instructions);
            instructions.setVisibility(TextView.VISIBLE);
        }

        final Intent myIntent = new Intent(MainActivity.this,
                NewActivity.class);

        rotate1.setDuration(60);
        rotate1.setInterpolator(new AccelerateInterpolator(1.0f));

        rotate2.setDuration(60);
        rotate2.setInterpolator(new AccelerateInterpolator(1.0f));

        noteList.clear();
        noteList.addAll(db.getAllNotes());

        final ListView notesView = (ListView) findViewById(R.id.notesView);

//        findViewById(R.id.search_box).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(findViewById(R.id.fab), String.valueOf(1), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // Adapter thingy
        final noteAdapter NoteAdapter = new noteAdapter(this, 0, noteList);

        if(noteList!=null && noteList.size() > 0 )
        {
            notesView.setAdapter(NoteAdapter);
        }

        final ImageButton delete = (ImageButton) findViewById(R.id.delete);

        // FAB on click
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(myIntent);
            }
        });

        // note on click
        notesView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id)
            {
                v.setBackgroundColor(getResources().getColor(R.color.gray3));

                note Note = (note) parent.getItemAtPosition(position);
                int sqlID = Note.getId();
                final Intent editIntent = new Intent(MainActivity.this,
                        NewActivity.class).putExtra("id", sqlID);
                startActivity(editIntent);
//                Snackbar.make(findViewById(R.id.fab), String.valueOf(Note.getId()), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });


        notesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                view.setBackgroundColor(getResources().getColor(R.color.gray2));

                delete.setVisibility(TextView.VISIBLE);

                notesToBeDeleted.add((note) notesView.getItemAtPosition(position));

                return true;
            }
        });

        CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.cl);

        // for softKeyboard off
        cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View focus = getCurrentFocus();
                if (focus != null)
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if(notesToBeDeleted.size() > 0) {
                    notesToBeDeleted.clear();
                    delete.setVisibility(TextView.INVISIBLE);
                    noteList.clear();
                    noteList.addAll(db.getAllNotes());
                    NoteAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });

        final EditText searchet = (EditText) findViewById(R.id.search_box);

        final ListView searchResults = (ListView) findViewById(R.id.search_results);

        searchResults.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(getResources().getColor(R.color.white));

                note Note = (note) parent.getItemAtPosition(position);
                int sqlID = Note.getId();
                final Intent editIntent = new Intent(MainActivity.this,
                        NewActivity.class).putExtra("id", sqlID);
                startActivity(editIntent);
            }
        });

        searchet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    notesResult = db.search(searchet.getText().toString());
                    final noteAdapter ResultsAdapter = new noteAdapter(MainActivity.this, 0, notesResult);
                    if (notesResult != null){
                        Snackbar.make(findViewById(R.id.fab), String.valueOf(notesResult.size() + " note(s) found"), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    if(notesResult!=null && noteList.size() > 0 )
                    {
                        searchResults.setAdapter(ResultsAdapter);
                    }
                }
                return false;
            }
        });

        //search button
        final ImageButton search = (ImageButton) findViewById(R.id.search_button);

        search.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    view.setBackground(getResources().getDrawable(R.drawable.ic_lens_black_24dp));

                    int visibility = searchet.getVisibility();
                    // while back button
                    if(visibility == TextView.VISIBLE){
                        searchet.setVisibility(TextView.INVISIBLE);
                        search.setImageResource(R.drawable.ic_search_black_24dp);
                        search.setScaleX(-1);
                        view.startAnimation(rotate1);
                        View focus = getCurrentFocus();
                        if (focus != null)
                            imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
                        notesResult.clear();
                        searchResults.setVisibility(TextView.GONE);
                        fab.setVisibility(TextView.VISIBLE);

                    }
                    // while search button
                    else{
                        searchet.setVisibility(TextView.VISIBLE);
                        searchet.requestFocus();
                        search.setImageResource(R.drawable.ic_arrow_back_black_24dp);
                        search.setScaleX(1);
                        view.startAnimation(rotate2);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        searchet.setText("");
                        searchResults.setVisibility(TextView.VISIBLE);
                        fab.setVisibility(TextView.GONE);
                    }

                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    view.setBackground(null);
                }
                return false;
            }
        });

        // delete button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int len = notesToBeDeleted.size();

                for(int i=0; i<notesToBeDeleted.size(); i += 1) {
                    note Current = notesToBeDeleted.get(i);
                    db.deleteNote(Current);
                }
                notesToBeDeleted.clear();
                noteList.clear();
                noteList.addAll(db.getAllNotes());
                NoteAdapter.notifyDataSetChanged();

                v.setBackground(null);

                Snackbar.make(findViewById(R.id.fab), String.valueOf(len) + " note(s) deleted successfully", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                delete.setVisibility(TextView.INVISIBLE);
            }
        });

        delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN)
                    v.setBackground(getResources().getDrawable(R.drawable.ic_lens_black_24dp));
                return false;
            }
        });

        // confirms that intent calls onCreate
//        Snackbar.make(findViewById(R.id.fab), String.valueOf(notesToBeDeleted.size()), Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();


    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }




}


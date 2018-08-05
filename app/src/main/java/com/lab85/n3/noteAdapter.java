package com.lab85.n3;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class noteAdapter extends ArrayAdapter<note> {
    public noteAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View noteView = convertView;
        if(noteView == null){
            noteView = LayoutInflater.from(getContext()).inflate(R.layout.note_view, parent, false);
        }

        note currentNote = getItem(position);

        TextView time = (TextView) noteView.findViewById(R.id.time);
        TextView title = (TextView) noteView.findViewById(R.id.title);
        TextView regular = (TextView) noteView.findViewById(R.id.regular);

        noteView.setBackgroundColor(getContext().getResources().getColor(R.color.white));

        time.setText(currentNote.getTime());
        title.setText(currentNote.getTitle());
        regular.setText(currentNote.getRegular());

//        noteView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN)
////                    return false;
//                    v.setBackgroundColor(getContext().getResources().getColor(R.color.gray3));
//                else if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
//                    v.setBackgroundColor(getContext().getResources().getColor(R.color.white));
//                    return false;
//                }
//                else if(event.getAction() == MotionEvent.ACTION_MOVE){
//                    v.setBackgroundColor(getContext().getResources().getColor(R.color.white));
//                    return false;
//                }
////                else if(event.getAction() == MotionEvent.ACTION_UP)
////                    v.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
//
//                return false;
//            }
//        });


//        noteView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
//            }
//        });
//
//        noteView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                v.setBackgroundColor(getContext().getResources().getColor(R.color.gray2));
//                return false;
//            }
//        });

        return noteView;
    }


}

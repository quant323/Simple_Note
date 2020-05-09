package com.stanislav_xyz.simplenote_2.activities;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.model.Note;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    private List<Note> mNotes;

    // Устанавливает список заметок
    void setNotes(List<Note> notes) {
        mNotes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_list,
                parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(mNotes.get(position));
    }

    @Override
    public int getItemCount() {
        if (mNotes != null)
            return mNotes.size();
        else
            return 0;
    }


    class NoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView noteTitle_tV;
        private final TextView date_tv;

        // Конструктор
        public NoteViewHolder(@NonNull final View itemView) {
            super(itemView);
            noteTitle_tV = itemView.findViewById(R.id.note_title_textView);
            date_tv = itemView.findViewById(R.id.note_date_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoteActivity.startThisActivity((Activity) itemView.getContext(),
                            mNotes.get(getAdapterPosition()));
                }
            });
        }


        public void bind(Note note) {
            noteTitle_tV.setText(note.title);
            date_tv.setText(String.valueOf(note.date));
        }
    }

}

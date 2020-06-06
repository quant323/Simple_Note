package com.stanislav_xyz.simplenote_2.activities;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.model.Note;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    public static final int CONTEXT_OPEN_ID = 100;
    public static final int CONTEXT_DEL_ID = 101;
    public static final int CONTEXT_MOVE_ID = 102;

    private View mContextMenuView;

    private SortedList<Note> mSortedNotes;
    private ClickListener mClickListener;


    // Конструктор
    public NoteListAdapter() {
        mSortedNotes = new SortedList<>(Note.class, new SortedList.Callback<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                return (int)((o2.getDate() - o1.getDate())/100);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Note oldItem, Note newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Note item1, Note item2) {
                return item1.getUdi() == item2.getUdi();
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    // Устанавливает список заметок
    void setNotes(List<Note> notes) {
        mSortedNotes.replaceAll(notes);
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
        holder.bind(mSortedNotes.get(position));
    }

    @Override
    public int getItemCount() {
        return mSortedNotes != null ? mSortedNotes.size() : 0;
    }

    public SortedList<Note> getSortedNotes() {
        return mSortedNotes;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClickListener(View v, Note note);
    }

    public View getContextMenuView() {
        return mContextMenuView;
    }


    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

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
                    mClickListener.onItemClickListener(v, mSortedNotes.get(getAdapterPosition()));
                }
            });
            itemView.setOnCreateContextMenuListener(this);
        }


        private void bind(Note note) {
            noteTitle_tV.setText(note.getTitle());
            String date = DateFormat.getDateTimeInstance().format(new Date(note.getDate()));
            date_tv.setText(date);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //todo needs call super?
            menu.add(this.getAdapterPosition(), CONTEXT_OPEN_ID, 0, R.string.action_context_open);
            menu.add(this.getAdapterPosition(), CONTEXT_DEL_ID, 0, R.string.action_context_delete);
            menu.add(this.getAdapterPosition(), CONTEXT_MOVE_ID, 0, R.string.action_context_move);
            mContextMenuView = v;
        }

    }

}

package com.stanislav_xyz.simplenote_2.activities;

import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;

import java.util.List;

public interface MainInterface {

    void setToolbarTitle(String title);

    void addDrawerMenuItem(Folder folder, int iconId);

    void deleteDrawerMenuItem(Folder folder);

    void renameDrawerMenuItem(Folder folder);

    void setCheckedDrawerMenuItem(Folder folder);

    void updateNoteResView(List<Note> notes);

    void showToast(String text);

    void showToast(int id);

    void fabStateControl(boolean visibility);

    void showSnack(String text);

    void showSnack(int id);
}

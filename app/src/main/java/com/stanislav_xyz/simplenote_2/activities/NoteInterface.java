package com.stanislav_xyz.simplenote_2.activities;

public interface NoteInterface {

    void setEditToEditText(boolean editable);

    void setTextEditText(String text);

    void hideFab();

    void showKeyBoard(boolean inFab);

    String getTextFromEditText();

}

package com.stanislav_xyz.simplenote_2.presenters;

public interface NoteInterface {

    void setEditToEditText(boolean editable);

    void setTextEditText(String text);

    void hideFab();

    void showKeyBoard(boolean inFab);

    String getTextFromEditText();

    void showOptionDelItem(boolean show);

}

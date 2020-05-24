package com.stanislav_xyz.simplenote_2.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.stanislav_xyz.simplenote_2.R;

import com.stanislav_xyz.simplenote_2.controllers.MainController;
import com.stanislav_xyz.simplenote_2.controllers.MainInterface;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainInterface {

    public static final String TAG = "myTag";
    private static final String STATE_CUR_FOLDER_ID = "cur_folder_id";
    private static final String STATE_MENU_ITEMS = "menu_items_state";
    private static final String STATE_DEL_MENU = "del_menu_state";

    private boolean mItemsVisibleState = true;
    private boolean mMenuEnableState = false;

    private NoteListAdapter mAdapter = new NoteListAdapter();
    private DrawerLayout mDrawer;
    private Menu mNavigationMenu;
    private FloatingActionButton fab;
    private MainController mMainController;
    private Menu mMainMenu;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mNavigationMenu = navigationView.getMenu();

        int curFolderId;
        if (savedInstanceState != null) {
            curFolderId = savedInstanceState.getInt(STATE_CUR_FOLDER_ID);
            mItemsVisibleState = savedInstanceState.getBoolean(STATE_MENU_ITEMS);
            mMenuEnableState = savedInstanceState.getBoolean(STATE_DEL_MENU);
        }
        else curFolderId = MainController.INITIAL_FOLDER_ID;

        mMainController = new MainController(this, this, curFolderId);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainController.onFabPressed();
            }
        });

        // Слушатель нажатия на заметку в RecyclerView
        mAdapter.setOnItemClickListener(new NoteListAdapter.ClickListener() {
            @Override
            public void onItemClickListener(View v, Note note) {
                mMainController.onItemNotePressed(note);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMainMenu = menu;
        setItemsVisibility(mItemsVisibleState);
        setEnableMenuItems(mMenuEnableState);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_folder:
                mMainController.onMenuDelPressed();
                return true;
            case R.id.action_rename_folder:
                mMainController.onMenuRenamePressed();
                return true;
            case R.id.action_clean_bin:
                mMainController.onMenuCleanBinPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        final int position = item.getGroupId();
        Note note = mAdapter.getSortedNotes().get(position);
        switch (item.getItemId()) {
            case NoteListAdapter.CONTEXT_OPEN_ID:
                mMainController.onContextOpenPressed(note);
                return true;
            case NoteListAdapter.CONTEXT_DEL_ID:
                mMainController.onContextDelPressed(note);
                return true;
            case NoteListAdapter.CONTEXT_MOVE_ID:
                mMainController.onContextMovePressed(note);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_bin:
                mMainController.onNavBinPressed();
                setItemsVisibility(false);
                break;
            case R.id.nav_settings:
                mMainController.onNavSettingsPressed();
                break;
            case R.id.nav_about:
                mMainController.onNavAboutPressed();
                break;
            case R.id.nav_add_new_folder:
                mMainController.onNavAddFolderPressed();
                setItemsVisibility(true);
                break;
            default:
                mMainController.onNavNormalFolderPressed(id);
                setItemsVisibility(true);
                break;
        }
        mRecyclerView.scrollToPosition(0);
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Управляет отображением пунктов главного меню
    private void setItemsVisibility(boolean visibility) {
        mMainMenu.findItem(R.id.action_delete_folder).setVisible(visibility);
        mMainMenu.findItem(R.id.action_rename_folder).setVisible(visibility);
        mMainMenu.findItem(R.id.action_clean_bin).setVisible(!visibility);
        if (visibility) fab.show();
        else fab.hide();
        mItemsVisibleState = visibility;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(STATE_CUR_FOLDER_ID, mMainController.getCurFolderId());
        outState.putBoolean(STATE_MENU_ITEMS, mItemsVisibleState);
        outState.putBoolean(STATE_DEL_MENU, mMenuEnableState);
        super.onSaveInstanceState(outState);
    }

    // Методы интерфейса
    @Override
    public void setToolbarTitle(String title) {
        setTitle(title);
    }

    @Override
    public void addDrawerMenuItem(Folder folder, int iconId) {
        TextView view = new TextView(this);
        view.setGravity(Gravity.CENTER_VERTICAL);
        mNavigationMenu.add(R.id.folder_group, folder.getId(), mNavigationMenu.NONE, folder.getName())
                .setIcon(iconId)
                .setCheckable(true)
                .setActionView(view);
    }

    @Override
    public void deleteDrawerMenuItem(Folder folder) {
        mNavigationMenu.removeItem(folder.getId());
    }

    @Override
    public void renameDrawerMenuItem(Folder folder) {
        mNavigationMenu.findItem(folder.getId()).setTitle(folder.getName());
    }

    @Override
    public void setCheckedDrawerMenuItem(Folder folder) {
        mNavigationMenu.findItem(folder.getId()).setChecked(true);
    }

    @Override
    public void setExtraTextToDrawerMenuItem(Folder folder, int noteAmount) {
        ((TextView) mNavigationMenu.findItem(folder.getId()).getActionView())
                .setText(String.valueOf(noteAmount));
    }

    @Override
    public void setEnableMenuItems(boolean enable) {
        mMainMenu.findItem(R.id.action_delete_folder).setEnabled(enable);
        mMainMenu.findItem(R.id.action_rename_folder).setEnabled(enable);
        mMenuEnableState = enable;
    }

    @Override
    public void updateNoteResView(List<Note> notes) {
        mAdapter.setNotes(notes);
    }

    @Override
    public void showSnack(int id) {
        Snackbar.make(findViewById(R.id.coordinator_main), id, Snackbar.LENGTH_LONG).show();
    }

}

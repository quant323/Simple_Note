package com.stanislav_xyz.simplenote_2.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.stanislav_xyz.simplenote_2.R;
import com.stanislav_xyz.simplenote_2.data.NoteViewModel;
import com.stanislav_xyz.simplenote_2.model.Folder;
import com.stanislav_xyz.simplenote_2.model.Note;
import com.stanislav_xyz.simplenote_2.utils.WorkWithSharedPref;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "myTag";
    private static final int INITIAL_FOLDER_ID = 0;

    private NoteViewModel mNoteViewModel;
    private NoteListAdapter mAdapter;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private WorkWithSharedPref mSharedPref;
    private RecyclerView mRecyclerView;

    private List<Note> mNotes;
    private List<Note> mNotesInCurFolder;
    private List<Folder> mFolderList;
    private Folder mCurFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "onCreate()", Toast.LENGTH_SHORT).show();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNotesInCurFolder = new ArrayList<>();

        // Инициализируем RecyclerView
        initializeRecyclerView();

        // Инициализируем список папок
        initializeFolderList();

        // Инициализируем Drawer меню
        initializeDrawer(toolbar);

        // Инициализация ViewModel
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        mNoteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                Log.d(TAG, "onChanged: ");
                mNotes = notes;
                updateNoteList();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteActivity.startThisActivity(MainActivity.this, null, mCurFolder.getName());
            }
        });
    }

    // Собирает Notes, относящиеся к текущей папке и передает их в RecyclerView
    private void updateNoteList() {
        mNotesInCurFolder.clear();
        for (int i = 0; i < mNotes.size(); i++) {
            if (mNotes.get(i).getFolder().equals(mCurFolder.getName())) {
                mNotesInCurFolder.add(mNotes.get(i));
            }
        }
        mAdapter.setNotes(mNotesInCurFolder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_folder:
                mSharedPref.cleanFromSharedPref();
                return true;
            case R.id.action_rename_folder:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = item.getGroupId();
        switch (item.getItemId()) {
            case NoteListAdapter.CONTEXT_OPEN_ID:
                NoteActivity.startThisActivity(this, mNotesInCurFolder.get(position), null);
                return true;
            case NoteListAdapter.CONTEXT_DEL_ID:
                mNoteViewModel.deleteNote(mNotesInCurFolder.get(position));
                return true;
            case NoteListAdapter.CONTEXT_MOVE_ID:
                Snackbar.make(findViewById(R.id.coordinator_main), "Moving..." + item.getGroupId(),
                        Snackbar.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Bin folder is pressed!", Toast.LENGTH_SHORT).show();
                setTitle(R.string.nav_bin);
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Settings folder is pressed!", Toast.LENGTH_SHORT).show();
                setTitle(R.string.nav_settings);
                break;
            case R.id.nav_about:
                Toast.makeText(this, "About folder is pressed!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_add_new_folder:
                new FolderDialog(this, new FolderDialog.FolderDialogListener() {
                    @Override
                    public void onFolderConfirm(java.lang.String name) {
                        createNewFolder(name);
                    }
                }).show(getSupportFragmentManager(), null);
                break;
            default:
                mCurFolder = getPressedFolder(id);
                updateNoteList();
                setTitle(mCurFolder.getName());
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new NoteListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    private void initializeFolderList() {
        mSharedPref = new WorkWithSharedPref(this);
        mFolderList = mSharedPref.loadFromSharedPref();
        if (mFolderList == null) {
            Folder folder = new Folder(getString(R.string.default_folder_name),
                    System.currentTimeMillis(), INITIAL_FOLDER_ID);
            mFolderList = new ArrayList<>();
            mFolderList.add(folder);
        }
        // Устанавливаем папку по-умолчанию - первую папку
        mCurFolder = mFolderList.get(0);
        // Устанавливаем имя папки в Toolbar
        setTitle(mCurFolder.getName());
    }

    private void initializeDrawer(Toolbar toolbar) {
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Устанавливаем пункты меню
        Menu menu = mNavigationView.getMenu();
        for (int i = 0; i < mFolderList.size(); i++) {
            if (i != 0) {
                addMenuItem(menu, mFolderList.get(i));
            } else {
                addMenuItem(menu, mFolderList.get(i))
                        .setIcon(R.drawable.ic_home)
                        .setChecked(true);
            }
        }
    }

    // Добавляет пункт меню в Drawer
    private MenuItem addMenuItem(Menu menu, Folder folder) {
        return menu.add(R.id.folder_group, folder.getId(), menu.NONE, folder.getName())
                .setIcon(R.drawable.ic_folder)
                .setCheckable(true);
    }

    private void createNewFolder(java.lang.String name) {
        // Находим папку, находящуюся в списке последней
        Folder lastFolder = mFolderList.get(mFolderList.size() - 1);
        // Создаем новую папку. В качестве id указываем id последней папки, увеличенную на 1
        mCurFolder = new Folder(name, System.currentTimeMillis(), (lastFolder.getId() + 1));
        mFolderList.add(mCurFolder);
        mSharedPref.saveInSharedPref(mFolderList);
        updateNoteList();
        Menu menu = mNavigationView.getMenu();
        addMenuItem(menu, mCurFolder).setChecked(true);
        setTitle(mCurFolder.getName());
    }

    // Возвращает нажатую папку по id
    private Folder getPressedFolder(int id) {
        for (int i = 0; i < mFolderList.size(); i++) {
            if (mFolderList.get(i).getId() == id)
                return mFolderList.get(i);
        }
        return mFolderList.get(0);
    }

}

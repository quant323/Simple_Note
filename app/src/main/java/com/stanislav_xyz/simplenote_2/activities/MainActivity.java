package com.stanislav_xyz.simplenote_2.activities;

import android.os.Bundle;
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

    private NoteViewModel mNoteViewModel;
    private NoteListAdapter mAdapter;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private WorkWithSharedPref mSharedPref;

    private List<Note> mNotes;
    private List<Folder> mFolderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteActivity.startThisActivity(MainActivity.this, null);
            }
        });

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new NoteListAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mSharedPref = new WorkWithSharedPref(this);
        mFolderList = mSharedPref.loadFromSharedPref();
        if (mFolderList == null) {
            Folder folder = new Folder(getString(R.string.default_folder_name), System.currentTimeMillis(), 300);
            mFolderList = new ArrayList<>();
            mFolderList.add(folder);
        }
        // Инициализируем пункты Drawer меню
        initializeFolders();

        // Инициализация ViewModel
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        mNoteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                mNotes = notes;
                mAdapter.setNotes(mNotes);
            }
        });
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
                NoteActivity.startThisActivity(this, mNotes.get(position));
                return true;
            case NoteListAdapter.CONTEXT_DEL_ID:
                mNoteViewModel.deleteNote(mNotes.get(position));
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

        switch (item.getItemId()) {
            case R.id.nav_bin:
                Toast.makeText(this, "Bin folder is pressed!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Settings folder is pressed!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about:
                Toast.makeText(this, "About folder is pressed!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_add_new_folder:
                createNewFolder();
                break;
            default:
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeFolders() {
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
        return menu.add(R.id.folder_group, folder.id, menu.NONE, folder.name)
                .setIcon(R.drawable.ic_folder)
                .setCheckable(true);
    }

    private void createNewFolder() {
        // Находим папку, находящуюся в списке последней
        Folder lastFolder = mFolderList.get(mFolderList.size() - 1);
        // Создаем новую папку. В качестве id указываем id последней папки, увеличенную на 1
        Folder newFolder = new Folder("New Folder", System.currentTimeMillis(), (lastFolder.id + 1));
        Toast.makeText(this, "Folder id is: " + newFolder.id, Toast.LENGTH_SHORT).show();
        mFolderList.add(newFolder);
        mSharedPref.saveInSharedPref(mFolderList);
        Menu menu = mNavigationView.getMenu();
        addMenuItem(menu, newFolder).setChecked(true);
    }
}

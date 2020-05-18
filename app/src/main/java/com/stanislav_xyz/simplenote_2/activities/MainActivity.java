package com.stanislav_xyz.simplenote_2.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.stanislav_xyz.simplenote_2.R;

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

    private NoteListAdapter mAdapter;
    private DrawerLayout mDrawer;
    private Menu mNavigationMenu;
    private Controller mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mController = new Controller(this, this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new NoteListAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mNavigationMenu = navigationView.getMenu();

        // Инициализируем список папок
        mController.initFolderList();

        // Устанавливаем пункты меню
        mController.initDrawerMenu();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mController.onFabPressed();
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
                mController.onMenuDelPressed();
                return true;
            case R.id.action_rename_folder:
                mController.onMenuRenamePressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        final int position = item.getGroupId();
        switch (item.getItemId()) {
            case NoteListAdapter.CONTEXT_OPEN_ID:
                mController.onContextOpenPressed();
                return true;
            case NoteListAdapter.CONTEXT_DEL_ID:
                mController.onContextDelPressed(position);
                return true;
            case NoteListAdapter.CONTEXT_MOVE_ID:
                mController.onContextMovePressed(position);
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
                mController.onNavBinPressed();
                break;
            case R.id.nav_settings:
                mController.onNavSettingsPressed();
                break;
            case R.id.nav_about:
                mController.onNavAboutPressed();
                break;
            case R.id.nav_add_new_folder:
                mController.onNavAddFolderPressed();
                break;
            default:
                mController.onNavNormalFolderPressed(id);
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Методы интерфейса
    @Override
    public void setToolbarTitle(String title) {
        setTitle(title);
    }

    @Override
    public void addDrawerMenuItem(Folder folder, int iconId) {
        mNavigationMenu.add(R.id.folder_group, folder.getId(), mNavigationMenu.NONE, folder.getName())
                .setIcon(iconId)
                .setCheckable(true);
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
    public void updateNoteResView(List<Note> notes) {
        mAdapter.setNotes(notes);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

}

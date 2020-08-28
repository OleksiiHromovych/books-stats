package com.hromovych.android.bookstats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hromovych.android.bookstats.database.BookBaseHelper;
import com.hromovych.android.bookstats.menuOption.Import.ImportDataActivity;
import com.hromovych.android.bookstats.slider.IntroSlider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Callbacks {

    public static final int REQUEST_CODE_BOOK = 1;
    public static final int REQUEST_CODE_IMPORT = 2;
    public static final String GET_SHARED_PREFERENCES = "com.hromovych.android.bookstats";
    public static final String SHOW_DATE_PREFERENCES = "date_format";
    private BottomNavigationView navView;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = getSharedPreferences(GET_SHARED_PREFERENCES,
                MODE_PRIVATE);
        if (mSharedPreferences.getBoolean("first_run", true)) {
            startActivity(new Intent(this, IntroSlider.class));
            mSharedPreferences.edit().putBoolean("first_run", false).apply();
        }

        if (getIntent().getData() != null) {
            startActivityForResult(ImportDataActivity.newIntent(MainActivity.this,
                     getIntent().getData().getPath()),
                    REQUEST_CODE_IMPORT);
            Toast.makeText(getApplicationContext(), getIntent().getData().getPath(), Toast.LENGTH_LONG).show();
            getIntent().setData(null);

        }
        navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.read_yet, R.id.read_now, R.id.want_read)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        FloatingActionButton fab =
                findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                BookLab.get(MainActivity.this).addBook(book);

                onBookSelected(book);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_book:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("What book category you want to delete?");
                final ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.status_spinner_list));

                final Spinner sp = new Spinner(this);
                sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                sp.setAdapter(adp);
                sp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                sp.setPadding(5, 15, 5, 10);

                builder.setView(sp);
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBooks(sp.getSelectedItem().toString());
                    }
                });

                builder.show();
                return true;
            case R.id.menu_date_format:
                mSharedPreferences.edit().putBoolean(SHOW_DATE_PREFERENCES,
                        !mSharedPreferences.getBoolean(SHOW_DATE_PREFERENCES, true)).apply();
                this.recreate();
                return true;
            case R.id.import_books:
                startActivityForResult(ImportDataActivity.newIntent(MainActivity.this),
                        REQUEST_CODE_IMPORT);

                return true;
            case R.id.export_books:
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();
                FileChannel source = null;
                FileChannel destination = null;
                String currentDBPath = "/data/" + "com.hromovych.android.bookstats" + "/databases/" +
                        BookBaseHelper.DATABASE_NAME;
                String backupDBPath = BookBaseHelper.DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                try {
                    source = new FileInputStream(currentDB).getChannel();
                    destination = new FileOutputStream(backupDB).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    source.close();
                    destination.close();
                    Toast.makeText(this, "DB Exported! " + sd.getAbsolutePath() +
                            backupDBPath, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void deleteBooks(String status) {
        BookLab bookLab = BookLab.get(MainActivity.this);
        List<Book> books = bookLab.getBooksByStatus(status);
        for (Book book : books)
            bookLab.deleteBook(book);

        MainActivity.this.recreate();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onBookSelected(Book book) {

        startActivityForResult(BookActivity.newIntent(MainActivity.this, book.getId(),
                navView.getSelectedItemId())
                , REQUEST_CODE_BOOK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}

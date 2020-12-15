package com.hromovych.android.bookstats;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hromovych.android.bookstats.HelpersItems.Book;
import com.hromovych.android.bookstats.HelpersItems.BookActivity;
import com.hromovych.android.bookstats.HelpersItems.Callbacks;
import com.hromovych.android.bookstats.HelpersItems.FileUtils;
import com.hromovych.android.bookstats.database.BookLab;
import com.hromovych.android.bookstats.database.ValueConvector;
import com.hromovych.android.bookstats.menuOption.Export.ExportDataActivity;
import com.hromovych.android.bookstats.menuOption.Import.ImportDataActivity;
import com.hromovych.android.bookstats.menuOption.settings.SettingsActivity;
import com.hromovych.android.bookstats.slider.IntroSlider;
import com.hromovych.android.bookstats.ui.abandoned.AbandonedActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Callbacks {

    public static final int REQUEST_CODE_BOOK = 1;
    public static final int REQUEST_CODE_IMPORT = 2;
    public static final int REQUEST_CODE_RECREATE_APPLICATION = 3;
    public static final String GET_SHARED_PREFERENCES = "com.hromovych.android.bookstats";
    private static final String FIRST_RUN_PREFERENCES = "first_run";
    private BottomNavigationView navView;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = getSharedPreferences(GET_SHARED_PREFERENCES,
                MODE_PRIVATE);
        if (mSharedPreferences.getBoolean(FIRST_RUN_PREFERENCES, true)) {
            startActivity(new Intent(this, IntroSlider.class));
            mSharedPreferences.edit().putBoolean(FIRST_RUN_PREFERENCES, false).apply();
        }

        if (getIntent().getData() != null) {
            Uri uri = getIntent().getData();
            String path = new FileUtils(this).getPath(uri);
            startActivityForResult(ImportDataActivity.newIntent(MainActivity.this,
                    path),
                    REQUEST_CODE_IMPORT);
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.delete_book:
                showDeleteDialog();
                return true;

            case R.id.menu_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class),
                        REQUEST_CODE_RECREATE_APPLICATION);
                return true;

            case R.id.menu_abandoned_books:
                startActivity(new Intent(this, AbandonedActivity.class));
                return true;

            case R.id.import_books:
                startActivityForResult(ImportDataActivity.newIntent(MainActivity.this),
                        REQUEST_CODE_IMPORT);
                return true;

            case R.id.export_books:
                startActivity(ExportDataActivity.newIntent(this));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.delete_books_alert_dialog_title);
        List<String> list = new ArrayList<>(Arrays.asList(getResources()
                .getStringArray(R.array.status_spinner_list)));
        list.add(getString(R.string.all_title));
        final ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        final Spinner sp = new Spinner(this);
        sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sp.setAdapter(adp);
        sp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        sp.setMinimumHeight(80);
        sp.setPadding(5, 15, 5, 10);

        builder.setView(sp);
        builder.setNeutralButton(R.string.cancel_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(R.string.delete_book, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteBooks(sp.getSelectedItem().toString());
            }
        });

        builder.show();
    }


    private void deleteBooks(String status) {
        BookLab bookLab = BookLab.get(MainActivity.this);
        if (status.equals(getString(R.string.all_title))) {
            bookLab.deleteBooks();
        } else {
            status = ValueConvector.ToConstant.toStatusConstant(getApplicationContext(), status);
            List<Book> books = bookLab.getBooksByStatus(status);
            for (Book book : books)
                bookLab.deleteBook(book);

        }

        this.recreate();
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
        if (requestCode == REQUEST_CODE_RECREATE_APPLICATION)
            this.recreate();
    }
}

package com.hromovych.android.bookstats;

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
import com.hromovych.android.bookstats.HelpersItems.BookLab;
import com.hromovych.android.bookstats.HelpersItems.Callbacks;
import com.hromovych.android.bookstats.HelpersItems.FileUtils;
import com.hromovych.android.bookstats.database.ValueConvector;
import com.hromovych.android.bookstats.menuOption.Export.ExportDataActivity;
import com.hromovych.android.bookstats.menuOption.Import.ImportDataActivity;
import com.hromovych.android.bookstats.slider.IntroSlider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Callbacks {

    public static final int REQUEST_CODE_BOOK = 1;
    public static final int REQUEST_CODE_IMPORT = 2;
    public static final String GET_SHARED_PREFERENCES = "com.hromovych.android.bookstats";
    public static final String SHOW_DATE_PREFERENCES = "date_format";
    public static final String SORT_BY_DATE = "sort_format";
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_book:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("What book category you want to delete?");
                List<String> list = new ArrayList<>(Arrays.asList(getResources()
                        .getStringArray(R.array.status_spinner_list)));
                list.add("All");
                final ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, list);
                final Spinner sp = new Spinner(this);
                sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                sp.setAdapter(adp);
                sp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                sp.setMinimumHeight(80);
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
            case R.id.menu_sort_format:
                mSharedPreferences.edit().putBoolean(SORT_BY_DATE,
                        !mSharedPreferences.getBoolean(SORT_BY_DATE, true)).apply();
                this.recreate();
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


    private void deleteBooks(String status) {
        BookLab bookLab = BookLab.get(MainActivity.this);
        if (status.equals("All")) {
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
    }


}

package com.hromovych.android.bookstats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.hromovych.android.bookstats.database.BookDBSchema;
import com.hromovych.android.bookstats.slider.IntroSlider;

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

                builder.setTitle("Delete all books?");
                builder.setMessage("You are about to delete all books info. Do you really want to proceed ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Want", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "I don't think so. Програміст" +
                                " ще не написав цього. Sorry \\-_-/ ♥", Toast.LENGTH_LONG).show();
                        BookLab bookLab = BookLab.get(MainActivity.this);
                        List<Book> books = bookLab.getBooksByStatus(getResources()
                                .getString(R.string.title_want_read), BookDBSchema.BookTable.Cols.AUTHOR);
                        for (Book book: books)
                            bookLab.deleteBook(book);
                    }
                });

                builder.setNegativeButton("Yet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BookLab bookLab = BookLab.get(MainActivity.this);
                        List<Book> books = bookLab.getBooksByStatus(getResources()
                                .getString(R.string.title_read_yet));
                        for (Book book: books)
                            bookLab.deleteBook(book);
                    }
                });

                builder.show();
                return true;
            case R.id.menu_date_format:
                mSharedPreferences.edit().putBoolean(SHOW_DATE_PREFERENCES,
                        !mSharedPreferences.getBoolean(SHOW_DATE_PREFERENCES, true)).apply();
                return true;
            case R.id.import_books:
//                Toast.makeText(getApplicationContext(), "phhh, phh, phh. Import not success," +
//                        " try later", Toast.LENGTH_SHORT).show();
                startActivityForResult(ImportDataActivity.newIntent(MainActivity.this),
                        REQUEST_CODE_IMPORT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

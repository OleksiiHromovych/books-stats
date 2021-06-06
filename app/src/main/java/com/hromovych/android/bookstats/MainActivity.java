package com.hromovych.android.bookstats;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hromovych.android.bookstats.database.BookLab;
import com.hromovych.android.bookstats.database.ValueConvector;
import com.hromovych.android.bookstats.helpersItems.Book;
import com.hromovych.android.bookstats.helpersItems.BookActivity;
import com.hromovych.android.bookstats.helpersItems.Callbacks;
import com.hromovych.android.bookstats.helpersItems.PreferencesHelper;
import com.hromovych.android.bookstats.menuOption.export.ExportDataActivity;
import com.hromovych.android.bookstats.menuOption.import_option.ImportDataActivity;
import com.hromovych.android.bookstats.menuOption.settings.SettingsActivity;
import com.hromovych.android.bookstats.ui.abandoned.AbandonedActivity;

import java.util.List;

import static com.hromovych.android.bookstats.helpersItems.UiUtilsKt.showNotYetImplementedDialog;

public class MainActivity extends AppCompatActivity implements Callbacks {

    public static final int REQUEST_CODE_BOOK = 1;
    public static final int REQUEST_CODE_IMPORT = 2;
    public static final int REQUEST_CODE_RECREATE_APPLICATION = 3;

    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferencesHelper preferencesHelper = new PreferencesHelper(this);

// TODO: Redesign slider photos or delete it ;)
//        if (preferencesHelper.isFirstRun()) {
//            startActivity(new Intent(this, IntroSlider.class));
//            preferencesHelper.putIsFirstRun(false);
//        }

        // todo: delete this ;) or no ?)
//        if (getIntent().getData() != null) {
//            Uri uri = getIntent().getData();
//            String path = new FileUtils(this).getPath(uri);
//            startActivityForResult(ImportDataActivity.newIntent(MainActivity.this,
//                    path),
//                    REQUEST_CODE_IMPORT);
//            getIntent().setData(null);
//
//        }
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

            case R.id.action_delete_book:
                showDeleteDialog();
                break;

            case R.id.menu_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class),
                        REQUEST_CODE_RECREATE_APPLICATION);
                break;

            case R.id.menu_abandoned_books:
                startActivity(new Intent(this, AbandonedActivity.class));
                break;

            case R.id.import_books:
                startActivityForResult(ImportDataActivity.newIntent(MainActivity.this),
                        REQUEST_CODE_IMPORT);
                break;

            case R.id.export_books:
                startActivity(ExportDataActivity.newIntent(this));
                break;

            default:
                showNotYetImplementedDialog(this);
        }
        return true;
    }

    private void showDeleteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.delete_books_alert_dialog_title);

        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (String title : getResources().getStringArray(R.array.status_spinner_list)) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(title);
            checkBox.setButtonDrawable(R.drawable.checkbox_circle);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            checkBox.setPadding(5, 5, 5, 5);
            linearLayout.addView(checkBox);

        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        linearLayout.setLayoutParams(layoutParams);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(linearLayout);

        builder.setView(frameLayout);
        builder.setNeutralButton(R.string.cancel_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(R.string.delete_book, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < linearLayout.getChildCount(); i++) {
                    CheckBox checkBox = (CheckBox) linearLayout.getChildAt(i);
                    if (checkBox.isChecked())
                        deleteBooks(checkBox.getText().toString());
                }
            }
        });

        builder.show();
    }


    private void deleteBooks(String status) {
        BookLab bookLab = BookLab.get(MainActivity.this);
        status = ValueConvector.ToConstant.toStatusConstant(getApplicationContext(), status);
        List<Book> books = bookLab.getBooksByStatus(status);
        for (Book book : books)
            bookLab.deleteBook(book);

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

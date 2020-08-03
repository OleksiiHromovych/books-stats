package com.hromovych.android.bookstats;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements Callbacks {

    public static final int REQUEST_CODE_BOOK = 1;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    public void onBookSelected(Book book) {

        startActivityForResult(BookActivity.newIntent(MainActivity.this, book.getId(),
                navView.getSelectedItemId())
                , REQUEST_CODE_BOOK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snack.getView();
//        v.setBackgroundColor(getResources().getColor(R.color.secondary));
        ((TextView) v.findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }
}

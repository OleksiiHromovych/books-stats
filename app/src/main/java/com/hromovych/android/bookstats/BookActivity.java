package com.hromovych.android.bookstats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hromovych.android.bookstats.ui.readNow.BookNowFragment;
import com.hromovych.android.bookstats.ui.readYet.BookYetFragment;
import com.hromovych.android.bookstats.ui.wantRead.WantBookFragment;

import java.util.UUID;

public class BookActivity extends AppCompatActivity
        implements BookFragment.Callbacks {

    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_BOOK_FRAGMENT = "book_layout";
    private FragmentManager fm;
    private Fragment fragment;
    private UUID bookId;


    public static Intent newIntent(Context context, UUID uuid, int fragment_id) {
        Intent intent = new Intent(context, BookActivity.class);
        intent.putExtra(ARG_BOOK_FRAGMENT, fragment_id);
        intent.putExtra(ARG_BOOK_ID, uuid);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        bookId = (UUID) getIntent().getSerializableExtra(ARG_BOOK_ID);

        Integer fragment_id = getIntent().getIntExtra(ARG_BOOK_FRAGMENT, -1);

        fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            if (fragment_id.equals(BookNowFragment.BOOK_FRAGMENT_ID)) {
                fragment = BookNowFragment.newInstance(bookId);
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();

            } else if (fragment_id.equals(BookYetFragment.BOOK_FRAGMENT_ID)) {
                fragment = BookYetFragment.newInstance(bookId);
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();


            } else if (fragment_id.equals(WantBookFragment.BOOK_FRAGMENT_ID)) {
                fragment = WantBookFragment.newInstance(bookId);
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            } else {
                fragment = BookFragment.newInstance(bookId);
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            }
        }

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }


    @Override
    public void changeFragmentByStatus(Integer fragment_id) {

        if (fragment_id.equals(BookNowFragment.BOOK_FRAGMENT_ID)) {
            Fragment fragment_new = BookNowFragment.newInstance(bookId);
            fm.beginTransaction()
                    .replace(fragment.getId(), fragment_new)
                    .commit();
            fragment = fragment_new;

        } else if (fragment_id.equals(BookYetFragment.BOOK_FRAGMENT_ID)) {
            Fragment fragment_new = BookYetFragment.newInstance(bookId);
            fm.beginTransaction()
                    .replace(fragment.getId(), fragment_new)
                    .commit();
            fragment = fragment_new;

        } else if (fragment_id.equals(WantBookFragment.BOOK_FRAGMENT_ID)) {
            Fragment fragment_new = WantBookFragment.newInstance(bookId);
            fm.beginTransaction()
                    .replace(fragment.getId(), fragment_new)
                    .commit();
            fragment = fragment_new;
        }
    }

}

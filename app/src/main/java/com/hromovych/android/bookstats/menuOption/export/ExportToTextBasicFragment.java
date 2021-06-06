package com.hromovych.android.bookstats.menuOption.export;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.helpersItems.Book;
import com.hromovych.android.bookstats.helpersItems.DateHelper;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.hromovych.android.bookstats.helpersItems.DateHelper.getDateFormatString;

public abstract class ExportToTextBasicFragment extends Fragment {

    protected void sendByIntent(String text) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, text);
        i.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.send_books_data_extra_subject));
        startActivity(i);
    }


    protected void copyToClipboard(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = null;
        clipData = ClipData.newPlainText(getString(R.string.clipboard_data_label), text);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getContext(), R.string.copy_to_clipboard, Toast.LENGTH_SHORT).show();
    }

    public void showAlertMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    protected String getFieldValueFromBook(String field, Book book) {
        if (field.equals(getString(R.string.book_name_title))) {
            String value = book.getBookName();
            if (value == null || value.isEmpty())
                value = getString(R.string.unknown_value);
            return value;
        } else if (field.equals(getString(R.string.book_author_title))) {
            String value = book.getAuthor();
            if (value == null || value.isEmpty())
                value = getString(R.string.unknown_value);
            return value;
        } else if (field.equals(getString(R.string.book_end_date_title))) {
            Date date = book.getEndDate();
            String value;
            if (date.equals(DateHelper.unknownDate))
                value = getString(R.string.unknown_date);
            else if (date.equals(DateHelper.undefinedDate))
                value = getString(R.string.undefined_date);
            else
                value = Integer.toString(getDateFormatString(date));
            return value;

        } else if (field.equals(getString(R.string.book_pages_title))) {
            return Integer.toString(book.getPages());

        } else if (field.equals(getString(R.string.book_category_title))) {
            String value = book.getCategory();
            if (value == null || value.isEmpty())
                value = getString(R.string.without_category_book);
            return value;

        } else if (field.equals(getString(R.string.book_description_title))) {
            String value = book.getDescription();
            if (value == null || value.isEmpty())
                value = getString(R.string.empty_description_book);
            return value;

        } else if (field.equals(getString(R.string.book_label_title))) {
            String value = book.getLabel();
            if (value == null || value.isEmpty())
                value = getString(R.string.no_label_book);
            return value;
        } else {
            Toast.makeText(getContext(), getString(R.string.unknown_field, field), Toast.LENGTH_SHORT).show();
            return getString(R.string.unknown_field, field);
        }
    }

    protected String bookToText(List<String> fieldsList, Book book, String joinText) {
        StringBuilder result = new StringBuilder();

        Iterator<String> iterator = fieldsList.iterator();

        while (iterator.hasNext()) {
            result.append(getFieldValueFromBook(iterator.next(), book));
            if (iterator.hasNext())
                result.append(joinText);
        }
        result.append("\n");
        return result.toString();
    }

    public static String join(String joinText, Iterable<String> iterable) {
        StringBuilder result = new StringBuilder();

        Iterator<String> iterator = iterable.iterator();

        while (iterator.hasNext()) {
            result.append(iterator.next());
            if (iterator.hasNext())
                result.append(joinText);
        }
        return result.toString();
    }


}

package com.hromovych.android.bookstats.database;

import android.content.Context;
import android.widget.Toast;

import com.hromovych.android.bookstats.R;

public class ValueConvector {
    public static final class Constants {
        // For save status in bd.
        public static final String READ_NOW = "read now";
        public static final String READ_YET = "read yet";
        public static final String WANT_READ = "want read";

        //For save priority in bd (Type is field for want read book: low, medium, high)
        public static final String LOW = "low";
        public static final String MEDIUM = "medium";
        public static final String HIGH = "high";

        //For save type in bd (Type is field for read now and yet books: paper, audio, electronic)
        public static final String PAPER = "paper";
        public static final String ELECTRONIC = "electronic";
        public static final String AUDIO = "audio";
    }

    public static final class FromConstant {
        public static String fromStatusConstant(Context context, String s) {
            if (s == null)
                return s;
            switch (s) {
                case Constants.READ_NOW:
                    return context.getString(R.string.title_read_now);
                case Constants.READ_YET:
                    return context.getString(R.string.title_read_yet);
                case Constants.WANT_READ:
                    return context.getString(R.string.title_want_read);
                default:
                    Toast.makeText(context, "Error from. Unknown status name", Toast.LENGTH_SHORT).show();
                    return s;
            }
        }

        public static String fromPriorityConstant(Context context, String s) {
            if (s == null)
                return s;
            switch (s) {
                case Constants.HIGH:
                    return context.getString(R.string.priority_book_1);
                case Constants.MEDIUM:
                    return context.getString(R.string.priority_book_2);
                case Constants.LOW:
                    return context.getString(R.string.priority_book_3);
                default:
                    Toast.makeText(context, "Error from. Unknown priority name", Toast.LENGTH_SHORT).show();
                    return s;
            }
        }

        public static String fromTypeConstant(Context context, String s) {
            if (s == null)
                return s;
            switch (s) {
                case Constants.PAPER:
                    return context.getString(R.string.type_book_1);
                case Constants.ELECTRONIC:
                    return context.getString(R.string.type_book_2);
                case Constants.AUDIO:
                    return context.getString(R.string.type_book_3);
                default:
                    Toast.makeText(context, "Error from. Unknown status name", Toast.LENGTH_SHORT).show();
                    return s;
            }
        }

    }

    public static final class ToConstant {
        public static String toStatusConstant(Context context, String s) {
            if (s.equals(context.getString(R.string.title_read_now)))
                return Constants.READ_NOW;
            else if (s.equals(context.getString(R.string.title_read_yet)))
                return Constants.READ_YET;
            else if (s.equals(context.getString(R.string.title_want_read)))
                return Constants.WANT_READ;
            else {
                Toast.makeText(context, "Error to. Unknown status name", Toast.LENGTH_SHORT).show();
                return s;
            }
        }

        public static String toPriorityConstant(Context context, String s) {
            if (s.equals(context.getString(R.string.priority_book_1)))
                return Constants.HIGH;
            else if (s.equals(context.getString(R.string.priority_book_2)))
                return Constants.MEDIUM;
            else if (s.equals(context.getString(R.string.priority_book_3)))
                return Constants.LOW;
            else {
                Toast.makeText(context, "Error to. Unknown priority name", Toast.LENGTH_SHORT).show();
                return s;
            }
        }

        public static String toTypeConstant(Context context, String s) {
            if (s.equals(context.getString(R.string.type_book_1)))
                return Constants.PAPER;
            else if (s.equals(context.getString(R.string.type_book_2)))
                return Constants.ELECTRONIC;
            else if (s.equals(context.getString(R.string.type_book_3)))
                return Constants.AUDIO;
            else {
                Toast.makeText(context, "Error to. Unknown status name", Toast.LENGTH_SHORT).show();
                return s;
            }
        }


    }

}
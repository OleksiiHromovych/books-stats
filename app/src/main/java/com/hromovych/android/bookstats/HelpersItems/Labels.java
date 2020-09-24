package com.hromovych.android.bookstats.HelpersItems;

import android.content.Context;
import android.content.res.Resources;

import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.ValueConvector;

import java.util.ArrayList;
import java.util.List;

public class Labels {
    // Nen access for string resources
    private static Resources mResources;

    public Labels(Context context) {
        mResources = context.getResources();
    }
    // Possible value for read yet label
//    private List<YetLabel> mYetLabels(){
//        List<YetLabel> yetLabels = new ArrayList<>();
//
//        yetLabels.add(new YetLabel(mResources.getString(R.string.reread), mResources.getColor(R.color.reread)));
//
//        return yetLabels;
//    }


    private enum YetLabel {
        REREAD(mResources.getString(R.string.reread), mResources.getColor(R.color.reread)),
        BUY_PAPER(mResources.getString(R.string.buy_paper), mResources.getColor(R.color.buyPaper));

        private String name;
        private int color;

        YetLabel(String name, int color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public int getColor() {
            return color;
        }
    }

    public static List<String> getLabelsNames(String status) {
        List<String> list = new ArrayList<>();
        switch (status) {
            case ValueConvector.Constants.READ_YET:
                for (YetLabel label : YetLabel.values())
                    list.add(label.getName());
                break;
            case ValueConvector.Constants.READ_NOW:
                break;
        }
        return list;
    }

    public static String getLabelConstantName(String status, String labelName) {
        switch (status) {
            case ValueConvector.Constants.READ_YET:
                for (YetLabel label : YetLabel.values())
                    if (label.getName().equals(labelName))
                        return label.name();
                break;
            case ValueConvector.Constants.READ_NOW:
                break;
        }
        return "";
    }
//    private class YetLabel{
//        private String name;
//        private int color;
//
//        YetLabel(String name, int color) {
//            this.name = name;
//            this.color = color;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public int getColor() {
//            return color;
//        }
//    }
}

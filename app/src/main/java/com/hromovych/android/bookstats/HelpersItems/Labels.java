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
    private String status;
    public static final String NONE_VALUE = "NONE";

    public Labels(Context context, String status) {
        mResources = context.getResources();
        this.status = status;
    }
    // Possible value for read yet label
     private enum YetLabel {
        NONE(mResources.getString(R.string.none), mResources.getColor(R.color.transparent)),
        REREAD(mResources.getString(R.string.reread), mResources.getColor(R.color.reread)),
        BUY_PAPER(mResources.getString(R.string.buy_paper), mResources.getColor(R.color.buyPaper)),

        BRONZE(mResources.getString(R.string.bronze), mResources.getColor(R.color.bronze)),
        SILVER(mResources.getString(R.string.silver), mResources.getColor(R.color.silver)),
        GOLD(mResources.getString(R.string.gold), mResources.getColor(R.color.gold)),;
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
  private enum NowLabel {
        NONE(mResources.getString(R.string.none), mResources.getColor(R.color.transparent)),

        BRONZE(mResources.getString(R.string.bronze), mResources.getColor(R.color.bronze)),
        SILVER(mResources.getString(R.string.silver), mResources.getColor(R.color.silver)),
        GOLD(mResources.getString(R.string.gold), mResources.getColor(R.color.gold)),;
        private String name;
        private int color;

        NowLabel(String name, int color) {
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

  private enum WantLabel {
        NONE(mResources.getString(R.string.none), mResources.getColor(R.color.transparent)),

        BRONZE(mResources.getString(R.string.bronze), mResources.getColor(R.color.bronze)),
        SILVER(mResources.getString(R.string.silver), mResources.getColor(R.color.silver)),
        GOLD(mResources.getString(R.string.gold), mResources.getColor(R.color.gold)),;
        private String name;
        private int color;

        WantLabel(String name, int color) {
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

    public List<String> getLabelsNames() {
        List<String> list = new ArrayList<>();
        switch (status) {
            case ValueConvector.Constants.READ_YET:
                for (YetLabel label : YetLabel.values())
                    list.add(label.getName());
                break;
            case ValueConvector.Constants.READ_NOW:
                for (NowLabel label : NowLabel.values())
                    list.add(label.getName());
                break;
            case ValueConvector.Constants.WANT_READ:
                for (WantLabel label : WantLabel.values())
                    list.add(label.getName());
                break;
        }
        return list;
    }

    public int getLabelColor(String labelConstantName){
        switch (status) {
            case ValueConvector.Constants.READ_YET:
                return YetLabel.valueOf(labelConstantName).getColor();
            case ValueConvector.Constants.READ_NOW:
                return NowLabel.valueOf(labelConstantName).getColor();
            case ValueConvector.Constants.WANT_READ:
                return WantLabel.valueOf(labelConstantName).getColor();
            default:
                return 1;
        }
    }

    public String getLabelName(String constantName){
        switch (status) {
            case ValueConvector.Constants.READ_YET:
                return YetLabel.valueOf(constantName).getName();
            case ValueConvector.Constants.READ_NOW:
                return NowLabel.valueOf(constantName).getName();
            case ValueConvector.Constants.WANT_READ:
                return WantLabel.valueOf(constantName).getName();
            default:
                return "None";
        }
    }

    public String getLabelConstantName(String labelName) {
        switch (status) {
            case ValueConvector.Constants.READ_YET:
                for (YetLabel label : YetLabel.values())
                    if (label.getName().equals(labelName))
                        return label.name();
            case ValueConvector.Constants.READ_NOW:
                for (NowLabel label : NowLabel.values())
                    if (label.getName().equals(labelName))
                        return label.name();
            case ValueConvector.Constants.WANT_READ:
                for (WantLabel label : WantLabel.values())
                    if (label.getName().equals(labelName))
                        return label.name();
            default:
                return "smth go wrong";
        }
    }

}

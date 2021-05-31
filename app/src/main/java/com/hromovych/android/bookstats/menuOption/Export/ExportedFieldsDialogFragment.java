package com.hromovych.android.bookstats.menuOption.Export;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.helpersItems.Holders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class ExportedFieldsDialogFragment extends DialogFragment {

    private RecyclerView mRecyclerView;
    private FieldAdapter mAdapter;
    private List<String> fields;
    private final ArrayList<String> activeFields;
    private LinkedHashMap<String, Boolean> fieldActive;
    private final CallBack mCallBack;

    public ExportedFieldsDialogFragment(CallBack callBack, ArrayList<String> activeFields) {
        this.activeFields = activeFields;
        mCallBack = callBack;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.ThemeFieldsDialog);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_text_change_fields, container, false);

        mRecyclerView = view.findViewById(R.id.exported_fields_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Button cancelButton = view.findViewById(R.id.exported_fields_cancel_btn);
        cancelButton.setOnClickListener(buttonOnClickListener);
        Button okButton = view.findViewById(R.id.exported_fields_ok_btn);
        okButton.setOnClickListener(buttonOnClickListener);

        ImageButton helpButton = view.findViewById(R.id.exported_fields_help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertMessage(R.string.exportCriteriaHelpTitle, R.string.exportedFieldsHelpText);
            }
        });

        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        fields = Arrays.asList(getResources().getStringArray(R.array.export_fields_list));
        fieldActive = new LinkedHashMap<>();
        for (String s : activeFields)
            fieldActive.put(s, true);
        for (String s : fields)
            if (!fieldActive.containsKey(s))
                fieldActive.put(s, false);
        updateUI();
        return view;
    }

    private final ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    return true;
                }

                @Override
                public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    return super.getMovementFlags(recyclerView, viewHolder);
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                }

                @Override
                public boolean isItemViewSwipeEnabled() {
                    return false;
                }
            };

    private final View.OnClickListener buttonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.exported_fields_cancel_btn) {
                dismiss();

            } else if (v.getId() == R.id.exported_fields_ok_btn) {

                ArrayList<String> fields = new ArrayList<>();
                for (String key : mAdapter.getFields()) {
                    if (fieldActive.get(key))
                        fields.add(key);

                }
                if (fields.isEmpty()) {
                    showAlertMessage(R.string.valueExceptionTitle,
                            R.string.exportedFieldsEmptyFieldsException);
                    return;
                }
                mCallBack.setFields(fields);
                dismiss();
            }
        }
    };

    public interface CallBack {
        void setFields(ArrayList<String> fields);
    }

    public void showAlertMessage(int titleId, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(titleId);
        builder.setMessage(messageId);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }


    public void updateUI() {

        if (mAdapter == null) {
            mAdapter = new FieldAdapter(new ArrayList<String>(fieldActive.keySet()));
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setFields(new ArrayList<String>(fieldActive.keySet()));
            mAdapter.notifyDataSetChanged();
        }

    }


    private class FieldHolder extends Holders.BookHolder
            implements View.OnClickListener {

        private final TextView fieldView;

        public FieldHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_export_change_field, parent, false));
            fieldView = itemView.findViewById(R.id.exported_fields_item_textView);
        }

        public void bind(String fieldText) {
            fieldView.setText(fieldText);
            customFieldView(fieldActive.get(fieldText));
        }

        @Override
        public void onClick(View v) {
            boolean isActive = !fieldActive.get(fieldView.getText().toString());
            fieldActive.put(fieldView.getText().toString(), isActive);
            customFieldView(isActive);
        }

        private void customFieldView(Boolean isActive) {
            if (isActive) {
                fieldView.setTextColor(getResources().getColor(R.color.activeIcon));
                fieldView.setBackgroundColor(getResources().getColor(R.color.backgroundFont));
            } else {
                fieldView.setTextColor(getResources().getColor(R.color.backgroundFont));
                fieldView.setBackgroundColor(getResources().getColor(R.color.backgroundItem));
            }
        }
    }

    private class FieldAdapter extends RecyclerView.Adapter<FieldHolder> {

        private List<String> fields;

        public FieldAdapter(List<String> fields) {
            this.fields = fields;
        }

        @NonNull
        @Override
        public FieldHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new FieldHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FieldHolder holder, int position) {
            holder.bind(fields.get(position));
        }

        @Override
        public int getItemCount() {
            return fields.size();
        }

        public List<String> getFields() {
            return fields;
        }

        public void setFields(List<String> fields) {
            this.fields = fields;
        }

        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(fields, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(fields, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }
    }
}

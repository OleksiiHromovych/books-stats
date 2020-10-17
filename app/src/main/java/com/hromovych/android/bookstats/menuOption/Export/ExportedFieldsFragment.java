package com.hromovych.android.bookstats.menuOption.Export;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hromovych.android.bookstats.HelpersItems.Holders;
import com.hromovych.android.bookstats.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExportedFieldsFragment extends Fragment {
    public static final String BUNDLE_FIELDS_LIST_KEY = "bundle fields list key";

    private RecyclerView mRecyclerView;
    private FieldAdapter mAdapter;
    private List<String> fields;

    public static ExportedFieldsFragment newInstance(ArrayList<String> strings) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(BUNDLE_FIELDS_LIST_KEY, strings);
        ExportedFieldsFragment fragment = new ExportedFieldsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_yet, container, false);

        ArrayList<String> activeFields = getArguments().getStringArrayList(BUNDLE_FIELDS_LIST_KEY);

        mRecyclerView = view.findViewById(R.id.read_yet_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        fields = Arrays.asList(getResources().getStringArray(R.array.export_fields_list));
        updateUI();
        return view;
    }

    private ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    return super.getMovementFlags(recyclerView, viewHolder);
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                }
            };

    public void updateUI() {

        if (mAdapter == null) {
            mAdapter = new FieldAdapter(fields);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setFields(fields);
            mAdapter.notifyDataSetChanged();
        }

    }

    private class FieldHolder extends Holders.BookHolder
            implements View.OnClickListener {

        private TextView fieldView;

        public FieldHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(android.R.layout.simple_list_item_1, parent, false));

            fieldView = itemView.findViewById(android.R.id.text1);
            fieldView.setBackgroundColor(getResources().getColor(R.color.backgroundFont));
            fieldView.setTextColor(getResources().getColor(R.color.activeIcon));
            fieldView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            fieldView.setPadding(20, 15, 20, 0);
        }

        public void bind(String fieldText) {
            fieldView.setText(fieldText);
        }

        @Override
        public void onClick(View v) {
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
    }
}

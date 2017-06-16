package com.papermelody.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.papermelody.R;
import com.papermelody.activity.CalibrationActivity;
import com.papermelody.activity.MainActivity;
import com.papermelody.activity.PlayActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class ModeFreeSettingsFragment extends BaseFragment {
    /**
     * 用例：演奏乐器（流程三）
     * 自由模式演奏前设置页面
     */

    @BindView(R.id.spinner_free_instrument)
    Spinner spinnerInstrument;
    @BindView(R.id.spinner_free_category)
    Spinner spinnerCategory;
    @BindView(R.id.btn_free_cfm)
    Button btnFreeConfirm;
    @BindView(R.id.lay11)
    LinearLayout ll_first;
    @BindView(R.id.lay22)
    LinearLayout ll_second;

    @BindView(R.id.listView)
    AnimatedExpandableListView listView;

    private ModeFreeSettingsFragment.ExampleAdapter adapter;

    private int instrument, category;
    private ArrayAdapter<CharSequence> arrayAdapterInstrument, arrayAdapterCategory;

    public static ModeFreeSettingsFragment newInstance() {
        return new ModeFreeSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mode_free_settings, container, false);
        ButterKnife.bind(this, view);
        initView();
        test();
        return view;

    }

    private void initView() {
        arrayAdapterInstrument = ArrayAdapter.createFromResource(getContext(), R.array.spinner_instrument, android.R.layout.simple_spinner_item);
        arrayAdapterInstrument.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstrument.setAdapter(arrayAdapterInstrument);
        spinnerInstrument.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    arrayAdapterCategory = ArrayAdapter.createFromResource(getContext(), R.array.spinner_category_piano, android.R.layout.simple_spinner_item);
                } else {
                    arrayAdapterCategory = ArrayAdapter.createFromResource(getContext(), R.array.spinner_category_flute, android.R.layout.simple_spinner_item);
                }
                instrument = position;
                arrayAdapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(arrayAdapterCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    category = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnFreeConfirm.setOnClickListener((View v) -> {
            //__TEST();
            Intent intent = new Intent(getContext(), CalibrationActivity.class);
            intent.putExtra(PlayActivity.EXTRA_MODE, PlayActivity.MODE_FREE);
            intent.putExtra(PlayActivity.EXTRA_INSTRUMENT, instrument);
            intent.putExtra(PlayActivity.EXTRA_CATIGORY, category);
            startActivity(intent);
            MainActivity activity = (MainActivity) getActivity();
            activity.updateFragment(MainActivity.MAIN_HOME);
        });
    }


    public void test() {
        List<GroupItem> items = new ArrayList<GroupItem>();

        // Populate our list with groups and it's children
        for (int i = 2; i > 0; i--) {
            GroupItem item = new GroupItem();

            item.title = "Group " + i;

            for (int j = 0; j < i; j++) {
                ChildItem child = new ChildItem();
                child.title = "Awesome item " + j;
                child.hint = "Too awesome";
                item.items.add(child);
            }

            items.add(item);
        }
        // items.add(new GroupItem());

        adapter = new ExampleAdapter(this.getContext());
        adapter.setData(items);


        listView.setAdapter(adapter);
        listView.setGroupIndicator(null);

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });
    }

    private static class GroupItem {
        String title;
        List<ChildItem> items = new ArrayList<ChildItem>();
    }

    private static class ChildItem {
        String title;
        String hint;
    }

    private static class ChildHolder {
        TextView title;
        TextView hint;
    }

    private static class GroupHolder {
        TextView title;
        ImageView arrow;
    }

    /**
     * Adapter for our list of {@link GroupItem}s.
     */
    private class ExampleAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private LayoutInflater inflater;

        private List<GroupItem> items;

        public ExampleAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupItem> items) {
            this.items = items;
        }

        @Override
        public ChildItem getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;
            ChildItem item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = inflater.inflate(R.layout.list_item, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
                holder.hint = (TextView) convertView.findViewById(R.id.textHint);

                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.title.setText(item.title);
            holder.hint.setText(item.hint);

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).items.size();
        }

        @Override
        public GroupItem getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            GroupItem item = getGroup(groupPosition);

            if (convertView == null) {
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.group_item, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            holder.title.setText(item.title);
            holder.arrow = (ImageView) convertView.findViewById(R.id.question_arrow_iv);

            if (isExpanded) {
                holder.arrow.setBackgroundResource(R.drawable.ic_thumb_up_black_18dp);
            } else {
                holder.arrow.setBackgroundResource(R.drawable.ic_favorite_border_white_48dp);
            }
            if (item.title == "") return null;

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }
    }


}

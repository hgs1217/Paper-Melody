package com.papermelody.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.papermelody.R;
import com.papermelody.activity.CalibrationActivity;
import com.papermelody.activity.MainActivity;
import com.papermelody.activity.PlayActivity;
import com.papermelody.model.instrument.Instrument;
import com.papermelody.util.ToastUtil;
import com.papermelody.widget.ModeSettingsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class ModeOpernSettingsFragment extends BaseFragment {
    /**
     * 用例：演奏乐器（流程三）
     * 跟谱模式演奏前设置页面
     */

    @BindView(R.id.spinner_opern)
    Spinner spinnerOpern;
    @BindView(R.id.btn_opern_cfm)
    Button btnOpernConfirm;
    @BindView(R.id.listView_opern)
    AnimatedExpandableListView listView;

    private int opern, instrument, category;
    private ArrayAdapter<CharSequence> arrayAdapterOpern, arrayAdapterInstrument, arrayAdapterCategory;


    private ModeSettingsAdapter adapter;

    private static ModeOpernSettingsFragment fragment;

    public static ModeOpernSettingsFragment newInstance() {
        if (fragment != null) {
            return fragment;
        }
        ModeOpernSettingsFragment fragment = new ModeOpernSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceBundle) {
        View view = inflater.inflate(R.layout.fragment_mode_opern_settings, container, false);
        ButterKnife.bind(this, view);
        initView();
        initExpandableList();
        return view;
    }

    private void initView() {
        arrayAdapterOpern = ArrayAdapter.createFromResource(getContext(), R.array.spinner_opern, R.layout.spinner_mode_settings);
        arrayAdapterOpern.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOpern.setAdapter(arrayAdapterOpern);
        spinnerOpern.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                opern = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnOpernConfirm.setOnClickListener((View v) -> {
            if (instrument < 0 || category < 0 || opern < 0) {
                ToastUtil.showShort(getString(R.string.error));
                return;
            }
            Intent intent = new Intent(getContext(), CalibrationActivity.class);
            intent.putExtra(PlayActivity.EXTRA_MODE, PlayActivity.MODE_OPERN);
            intent.putExtra(PlayActivity.EXTRA_OPERN, opern);
            intent.putExtra(PlayActivity.EXTRA_INSTRUMENT, instrument);
            intent.putExtra(PlayActivity.EXTRA_CATIGORY, category);
            startActivity(intent);
            MainActivity activity = (MainActivity) getActivity();
            activity.updateFragment(MainActivity.MAIN_HOME);
        });
    }

    private void initExpandableList() {
        String[][] menu = {
                {
                        getString(R.string.piano),
                        getString(R.string.piano_with_14_keys_c3_to_b4),
                        getString(R.string.piano_with_14_keys_c4_to_b5),
                        getString(R.string.piano_with_21_keys_c3_to_b5),
                        getString(R.string.piano_with_21_keys_c4_to_b6)
                },
                {
                        getString(R.string.flute),
                        getString(R.string.flute_with_7_holes)
                }
        };

        List<ModeSettingsAdapter.GroupItem> items = new ArrayList<ModeSettingsAdapter.GroupItem>();

        for (int i = 0; i < 2; ++i) {
            ModeSettingsAdapter.GroupItem item = new ModeSettingsAdapter.GroupItem();
            item.title = menu[i][0];
            for (int j = 1; j < menu[i].length; ++j) {
                ModeSettingsAdapter.ChildItem child = new ModeSettingsAdapter.ChildItem();
                child.title = menu[i][j];
                child.hint = "AWESOME!";
                item.items.add(child);
            }
            items.add(item);
        }
        //这个第三方组件有bug，因此需要有一个辅助的空group，否则动画显示有问题
        items.add(new ModeSettingsAdapter.GroupItem());

        adapter = new ModeSettingsAdapter(this.getContext());
        adapter.setData(items);
        listView.setAdapter(adapter);
        listView.setGroupIndicator(null);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);


        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Log.d("PYJ", Integer.toString(groupPosition));
                btnOpernConfirm.setText(getString(R.string.choose_instrument));
                btnOpernConfirm.setTextColor(getResources().getColor(R.color.bb_inActiveBottomBarItemColor));
                btnOpernConfirm.setBackgroundColor(getResources().getColor(R.color.btn_default));
                instrument = -1;
                category = -1;
                if (listView.isGroupExpanded(groupPosition)) {
                    Log.d("PYJ", "is Expanded!");
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    Log.d("PYJ", "start to expand!");
                    listView.expandGroupWithAnimation(groupPosition);
                    for (int i = 0; i < menu.length; ++i) {
                        if (i != groupPosition) listView.collapseGroupWithAnimation(i);
                    }
                }
                return true;
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {

                if (groupPosition == 0) {
                    instrument = Instrument.INSTRUMENT_PIANO;
                    switch (childPosition) {
                        case 0: {
                            category = Instrument.INSTRUMENT_PIANO14C3TOB4;
                            break;
                        }
                        case 1: {
                            category = Instrument.INSTRUMENT_PIANO14C4TOB5;
                            break;
                        }
                        case 2: {
                            category = Instrument.INSTRUMENT_PIANO21C3TOB5;
                            break;
                        }
                        case 3: {
                            category = Instrument.INSTRUMENT_PIANO21C4TOB6;
                            break;
                        }
                    }
                }
                if (groupPosition == 1) {
                    instrument = Instrument.INSTRUMENT_FLUTE;
                    if (childPosition == 0)
                        category = Instrument.INSTRUMENT_FLUTE7;
                }
                btnOpernConfirm.setText(getString(R.string.use_xx_to_play).replace(
                        "xx", menu[groupPosition][childPosition + 1]));
                btnOpernConfirm.setTextColor(getResources().getColor(R.color.white));
                btnOpernConfirm.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                Log.d("PYJ", "CHOOSE: " + menu[groupPosition][childPosition + 1]);
                Log.d("PYJ", Integer.toString(instrument * 100 + category));
                return true;
            }
        });

        instrument = -1;
        category = -1;
        opern = -1;
    }
}

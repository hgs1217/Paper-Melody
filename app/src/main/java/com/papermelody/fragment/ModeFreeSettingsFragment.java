package com.papermelody.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.papermelody.R;
import com.papermelody.activity.CalibrationActivity;
import com.papermelody.activity.MainActivity;
import com.papermelody.activity.PlayActivity;
import com.papermelody.model.instrument.Instrument;
import com.papermelody.util.ToastUtil;
import com.papermelody.widget.ModeSettingsAdapter;
import com.papermelody.widget.ModeSettingsAdapter.ChildItem;

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

    @BindView(R.id.btn_free_cfm)
    Button btnFreeConfirm;
    @BindView(R.id.listView)
    AnimatedExpandableListView listView;

    private ModeSettingsAdapter adapter;

    private int instrument;
    private int category;

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

        initButton();
        initExpandableList();
        return view;

    }

    private void initButton() {
        instrument = -1;
        category = -1;
        btnFreeConfirm.setOnClickListener((View v) -> {
            //__TEST();
            if (instrument < 0 || category < 0) {
                ToastUtil.showShort(getString(R.string.error));
                return;
            }
            Intent intent = new Intent(getContext(), CalibrationActivity.class);
            intent.putExtra(PlayActivity.EXTRA_MODE, PlayActivity.MODE_FREE);
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
                        getString(R.string.flute),
                        getString(R.string.flute_with_7_holes)
                },
                {
                        getString(R.string.piano),
                        getString(R.string.piano_with_14_keys_c3_to_b4),
                        getString(R.string.piano_with_14_keys_c4_to_b5),
                        getString(R.string.piano_with_21_keys_c3_to_b5),
                        getString(R.string.piano_with_21_keys_c4_to_b6)
                }
        };

        List<ModeSettingsAdapter.GroupItem> items = new ArrayList<ModeSettingsAdapter.GroupItem>();

        for (int i = 0; i < 2; ++i) {
            ModeSettingsAdapter.GroupItem item = new ModeSettingsAdapter.GroupItem();
            item.title = menu[i][0];
            for (int j = 1; j < menu[i].length; ++j) {
                ModeSettingsAdapter.ChildItem child = new ChildItem();
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
                btnFreeConfirm.setText(getString(R.string.confirm));
                btnFreeConfirm.setTextColor(getResources().getColor(R.color.bb_inActiveBottomBarItemColor));
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

                if (groupPosition == 1) {
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
                if (groupPosition == 0) {
                    instrument = Instrument.INSTRUMENT_FLUTE;
                    if (childPosition == 0)
                        category = Instrument.INSTRUMENT_FLUTE7;
                }
                btnFreeConfirm.setText(getString(R.string.use_xx_to_play).replace(
                        "xx", menu[groupPosition][childPosition + 1]));
                btnFreeConfirm.setTextColor(getResources().getColor(R.color.black));
                Log.d("PYJ", "CHOOSE: " + menu[groupPosition][childPosition + 1]);
                Log.d("PYJ", Integer.toString(instrument * 100 + category));
                return true;
            }
        });
    }
}

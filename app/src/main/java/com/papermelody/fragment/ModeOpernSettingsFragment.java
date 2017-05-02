package com.papermelody.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObservable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.papermelody.R;
import com.papermelody.activity.MainActivity;
import com.papermelody.activity.PlayActivity;

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
    @BindView(R.id.spinner_opern_instrument)
    Spinner spinnerInstrument;
    @BindView(R.id.spinner_opern_category)
    Spinner spinnerCategory;
    @BindView(R.id.btn_opern_cfm)
    Button btnOpernConfirm;

    private int opern, instrument, category;
    private ArrayAdapter<CharSequence> arrayAdapterOpern, arrayAdapterInstrument, arrayAdapterCategory;

    public static ModeOpernSettingsFragment newInstance() {
        ModeOpernSettingsFragment fragment = new ModeOpernSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    这里貌似会引起闪退
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceBundle) {
//        View view = inflater.inflate(R.layout.fragment_mode_opern_settings, container);
//        ButterKnife.bind(this, view);
//        initView();
//        return view;
//    }

    private void initView() {
        arrayAdapterOpern = ArrayAdapter.createFromResource(getContext(), R.array.spinner_opern, android.R.layout.simple_spinner_item);
        arrayAdapterOpern.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOpern.setAdapter(arrayAdapterOpern);
        arrayAdapterInstrument = ArrayAdapter.createFromResource(getContext(), R.array.spinner_instrument, android.R.layout.simple_spinner_item);
        arrayAdapterInstrument.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstrument.setAdapter(arrayAdapterInstrument);
        spinnerOpern.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                opern = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        btnOpernConfirm.setOnClickListener((View v) -> {
            Intent intent = new Intent(getContext(), PlayActivity.class);
            intent.putExtra("mode", 1);
            intent.putExtra("opern", opern);
            intent.putExtra("instrument", instrument);
            intent.putExtra("category", category);
            startActivity(intent);
        });
    }
}

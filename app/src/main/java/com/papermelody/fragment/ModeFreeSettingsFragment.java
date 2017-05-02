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
import android.widget.Spinner;

import com.papermelody.R;
import com.papermelody.activity.PlayActivity;

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

    @BindView(R.id.spinner1)
    Spinner spinnerInstrument;
    @BindView(R.id.spinner2)
    Spinner spinnerCategory;
    @BindView(R.id.btn_free_cfm)
    Button btn_free_cfm;

    private int instrument, category;
    private Context context;
    private ArrayAdapter<CharSequence> arrayAdapterInstrument, arrayAdapterCategory;

    public static ModeFreeSettingsFragment newInstance() {
        ModeFreeSettingsFragment fragment = new ModeFreeSettingsFragment();
        return fragment;
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
        context = view.getContext();
        initView();
        return view;
    }

    private void initView() {
        arrayAdapterInstrument = ArrayAdapter.createFromResource(context, R.array.spinner_instrument, android.R.layout.simple_spinner_item);
        arrayAdapterInstrument.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstrument.setAdapter(arrayAdapterInstrument);
        spinnerInstrument.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    arrayAdapterCategory = ArrayAdapter.createFromResource(context, R.array.spinner_category_piano, android.R.layout.simple_spinner_item);
                    instrument = 0;
                } else {
                    arrayAdapterCategory = ArrayAdapter.createFromResource(context, R.array.spinner_category_flute, android.R.layout.simple_spinner_item);
                    instrument = 1;
                }
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
        btn_free_cfm.setOnClickListener((View v)->{
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra("mode", 0);
            intent.putExtra("instrument", instrument);
            intent.putExtra("category", category);
            startActivity(intent);
        });
    }

}

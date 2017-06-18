package com.papermelody.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.papermelody.R;

import java.util.ArrayList;
import java.util.List;

public class ModeSettingsAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;

    private List<GroupItem> items;

    public ModeSettingsAdapter(Context context) {
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
            convertView = inflater.inflate(R.layout.item_mode_settings_child, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.mode_setting_child_text);
            holder.hint = (TextView) convertView.findViewById(R.id.mode_setting_child_hint);
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
            convertView = inflater.inflate(R.layout.item_mode_settings_group, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.mode_setting_group_text);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        holder.title.setText(item.title);
        holder.arrow = (ImageView) convertView.findViewById(R.id.question_arrow_iv);

        if (isExpanded) {
            holder.arrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_48dp);
        } else {
            holder.arrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_48dp);
        }

        if (item.title == null) {
            convertView.findViewById(R.id.__group).setVisibility(View.GONE);
        }
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


    public static class GroupItem {
        public String title;
        public List<ChildItem> items = new ArrayList<ChildItem>();
    }

    public static class ChildItem {
        public String title;
        public String hint;
    }

    public static class ChildHolder {
        public TextView title;
        public TextView hint;
    }

    public static class GroupHolder {
        public TextView title;
        public ImageView arrow;
    }

}
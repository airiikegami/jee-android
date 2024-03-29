/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jeebook.android.gtd.view;

import com.jeebook.android.gtd.R;
import com.jeebook.android.gtd.model.Preferences;
import com.jeebook.android.gtd.model.Task;
import com.jeebook.android.gtd.util.DateUtils;
import com.jeebook.android.gtd.util.DrawableUtils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class TaskView extends ItemView<Task> {
    protected LabelView mContext;
    protected TextView mDescription;
    protected TextView mDateDisplay;
    protected TextView mProject;
    protected TextView mDetails;
    protected boolean mShowContext;
    protected boolean mShowProject;
    
    public TaskView(Context androidContext) {
        super(androidContext);
        
        LayoutInflater vi = (LayoutInflater)androidContext.
            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(getViewResourceId(), this, true); 
        
        mContext = (LabelView) findViewById(R.id.context);
        mDescription = (TextView) findViewById(R.id.description);
        mDateDisplay = (TextView) findViewById(R.id.due_date);
        mProject = (TextView) findViewById(R.id.project);
        mDetails = (TextView) findViewById(R.id.details);
        mShowContext = true;
        mShowProject = true;
        
        int bgColour = getResources().getColor(R.drawable.list_background);
        GradientDrawable drawable = DrawableUtils.createGradient(bgColour, Orientation.TOP_BOTTOM, 1.1f, 0.95f);
//        drawable.setCornerRadius(4.0f);
//        int strokeColour = getResources().getColor(R.drawable.white);
//        drawable.setStroke(1, strokeColour);
        setBackgroundDrawable(drawable);
    }
        
    protected int getViewResourceId() {
        return R.layout.list_task_view;
    }
    
    public void setShowContext(boolean showContext) {
        mShowContext = showContext;
    }
    
    public void setShowProject(boolean showProject) {
        mShowProject = showProject;
    }
    
    
    public void updateView(Task task) {
        updateContext(task);
        updateDescription(task);
        updateWhen(task);
        updateProject(task);
        updateDetails(task);
    }
    
    private void updateContext(Task task) {
        com.jeebook.android.gtd.model.Context context = task.context;
        boolean displayContext = Preferences.displayContextName(getContext());
        boolean displayIcon = Preferences.displayContextIcon(getContext());
        if (mShowContext && context != null && (displayContext || displayIcon)) {           
            mContext.setText(displayContext ? context.name : "");
            mContext.setColourIndex(context.colourIndex);
            // add context icon if preferences indicate to
            int id = context.icon.smallIconId;
            if (id > 0 && displayIcon) {
                mContext.setIcon(getResources().getDrawable(id));
            } else {
                mContext.setIcon(null);
            }
            mContext.setVisibility(View.VISIBLE);
        } else {
            mContext.setVisibility(View.GONE);
        }               
    }
    
    private void updateDescription(Task task) {
        CharSequence description = task.description;
        if (task.complete) {
            // add strike-through for completed tasks
            SpannableString desc = new SpannableString(description);
            desc.setSpan(new StrikethroughSpan(), 0, description.length(), Spanned.SPAN_PARAGRAPH);
            description = desc;
        }
        mDescription.setText(description);  
    }

    private void updateWhen(Task task) {
        if (Preferences.displayDueDate(getContext())) {
            CharSequence dateRange = DateUtils.displayDateRange(
                    getContext(), task.startDate, task.dueDate, !task.allDay);
            mDateDisplay.setText(dateRange);
            if (task.dueDate < System.currentTimeMillis()) {
                // task is overdue
                mDateDisplay.setTypeface(Typeface.DEFAULT_BOLD);
                mDateDisplay.setTextColor(Color.RED);
            } else {
                mDateDisplay.setTypeface(Typeface.DEFAULT);
                mDateDisplay.setTextColor(
                        getContext().getResources().getColor(R.drawable.dark_blue));
            }
            mDateDisplay.setVisibility(View.VISIBLE);
        } else {
            mDateDisplay.setVisibility(View.INVISIBLE);
        }
    }
    
    private void updateProject(Task task) {
        if (mShowProject && Preferences.displayProject(getContext()) && (task.project != null)) {
            mProject.setText(task.project.name);
            mProject.setVisibility(View.VISIBLE);
        } else {
            mProject.setVisibility(View.INVISIBLE);
        }
    }
    
    private void updateDetails(Task task) {
        if (Preferences.displayDetails(getContext()) && (task.details != null)) {
            mDetails.setText(task.details);
            mDetails.setVisibility(View.VISIBLE);
        } else {
            mDetails.setVisibility(View.INVISIBLE);
        }
    }
}

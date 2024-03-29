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

package com.jeebook.android.gtd.activity.preferences;

import static com.jeebook.android.gtd.model.Preferences.DISPLAY_CONTEXT_ICON_KEY;
import static com.jeebook.android.gtd.model.Preferences.DISPLAY_CONTEXT_NAME_KEY;
import static com.jeebook.android.gtd.model.Preferences.DISPLAY_DETAILS_KEY;
import static com.jeebook.android.gtd.model.Preferences.DISPLAY_DUE_DATE_KEY;
import static com.jeebook.android.gtd.model.Preferences.DISPLAY_PROJECT_KEY;

import com.jeebook.android.gtd.R;

import com.jeebook.android.gtd.model.Context;
import com.jeebook.android.gtd.model.Preferences;
import com.jeebook.android.gtd.model.Project;
import com.jeebook.android.gtd.model.Task;
import com.jeebook.android.gtd.util.ModelUtils;
import com.jeebook.android.gtd.view.TaskView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableRow.LayoutParams;

public class PreferencesAppearanceActivity extends Activity  {
    private static final String cTag = "PreferencesAppearanceActivity";
	
    private TaskView mTaskView;
    private Task mSampleTask;
	private CheckBox mDisplayIconCheckbox;
	private CheckBox mDisplayContextCheckbox;
	private CheckBox mDisplayDueDateCheckbox;
	private CheckBox mDisplayProjectCheckbox;
	private CheckBox mDisplayDetailsCheckbox;
	private boolean mSaveChanges;	
	private boolean mDisplayIcon, mDisplayContext, mDisplayDueDate, mDisplayProject, mDisplayDetails;
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        setContentView(R.layout.preferences_appearance);
        
        
        mDisplayIconCheckbox = (CheckBox) findViewById(R.id.display_icon);
        mDisplayContextCheckbox = (CheckBox) findViewById(R.id.display_context);
        mDisplayDueDateCheckbox = (CheckBox) findViewById(R.id.display_due_date);
        mDisplayProjectCheckbox = (CheckBox) findViewById(R.id.display_project);
        mDisplayDetailsCheckbox = (CheckBox) findViewById(R.id.display_details);
        
        setupSampleTask();
        
        // need to add task view programatically due to issues adding via XML
        
        mTaskView = new TaskView(this);
        mTaskView.updateView(mSampleTask);
        LayoutParams taskLayout = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT );
        LinearLayout layout = (LinearLayout)findViewById(R.id.appearance_layout);
        layout.addView(mTaskView, 0, taskLayout);
        
        // currently no cancel button
        mSaveChanges = true;
        
        OnCheckedChangeListener listener = new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
	        	savePrefs();
		        mTaskView.updateView(mSampleTask);
			}
        };
        mDisplayIconCheckbox.setOnCheckedChangeListener(listener);
        mDisplayContextCheckbox.setOnCheckedChangeListener(listener);
        mDisplayDueDateCheckbox.setOnCheckedChangeListener(listener);
        mDisplayProjectCheckbox.setOnCheckedChangeListener(listener);
        mDisplayDetailsCheckbox.setOnCheckedChangeListener(listener);

	}
	
	private void setupSampleTask() {
        long now = System.currentTimeMillis();
        Project sampleProject = new Project("Sample project", null, false, null, System.currentTimeMillis(), false);
        Context sampleContext = ModelUtils.getSampleContext(getResources());
        mSampleTask = new Task("Sample action", "Additional action details", 
        		sampleContext, sampleProject, now, now, 
        		now, now + DateUtils.HOUR_IN_MILLIS * 3, null, false, false,
        		null, 1, false, null, null);
	}
	
    @Override
	protected void onResume() {
        Log.d(cTag, "onResume+");
		super.onResume();
		
        readPrefs();
	}

	@Override
    protected void onPause() {
        Log.d(cTag, "onPause+");
        super.onPause();

        if (!mSaveChanges) {
        	revertPrefs();
        }
    }
    
	private void readPrefs() {
		Log.d(cTag, "Settings prefs controls");
		mDisplayIcon = Preferences.displayContextIcon(this);
		mDisplayContext = Preferences.displayContextName(this);
		mDisplayDueDate = Preferences.displayDueDate(this);
		mDisplayProject = Preferences.displayProject(this);
		mDisplayDetails = Preferences.displayDetails(this);
		
		mDisplayIconCheckbox.setChecked(mDisplayIcon);
		mDisplayContextCheckbox.setChecked(mDisplayContext);
		mDisplayDueDateCheckbox.setChecked(mDisplayDueDate);
		mDisplayProjectCheckbox.setChecked(mDisplayProject);
		mDisplayDetailsCheckbox.setChecked(mDisplayDetails);
	}
	
	private void revertPrefs() {
		Log.d(cTag, "Reverting prefs");
		SharedPreferences.Editor ed = Preferences.getEditor(this);
		ed.putBoolean(DISPLAY_CONTEXT_ICON_KEY, mDisplayIcon);
		ed.putBoolean(DISPLAY_CONTEXT_NAME_KEY, mDisplayContext);
		ed.putBoolean(DISPLAY_DUE_DATE_KEY, mDisplayDueDate);
		ed.putBoolean(DISPLAY_PROJECT_KEY, mDisplayProject);
		ed.putBoolean(DISPLAY_DETAILS_KEY, mDisplayDetails);
		ed.commit();
	}
	
	private void savePrefs() {
		Log.d(cTag, "Saving prefs");
		SharedPreferences.Editor ed = Preferences.getEditor(this);
		ed.putBoolean(DISPLAY_CONTEXT_ICON_KEY, mDisplayIconCheckbox.isChecked());
		ed.putBoolean(DISPLAY_CONTEXT_NAME_KEY, mDisplayContextCheckbox.isChecked());
		ed.putBoolean(DISPLAY_DUE_DATE_KEY, mDisplayDueDateCheckbox.isChecked());
		ed.putBoolean(DISPLAY_PROJECT_KEY, mDisplayProjectCheckbox.isChecked());
		ed.putBoolean(DISPLAY_DETAILS_KEY, mDisplayDetailsCheckbox.isChecked());
		ed.commit();
	}
	
}

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

package com.jeebook.android.gtd.activity;

import com.jeebook.android.gtd.activity.config.ListConfig;
import com.jeebook.android.gtd.activity.config.ProjectListConfig;
import com.jeebook.android.gtd.model.Project;
import com.jeebook.android.gtd.provider.Shuffle;
import com.jeebook.android.gtd.util.BindingUtils;
import com.jeebook.android.gtd.view.ProjectView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

/**
 * Display list of projects with task children.
 */
public class ProjectsActivity extends AbstractDrilldownListActivity<Project> {

	private static final String cTag = "ProjectsActivity";

	@Override
	protected void onResume() {
		super.onResume();
		
		Cursor cursor = getContentResolver().query(
				Shuffle.Projects.cProjectTasksContentURI, 
				Shuffle.Projects.cFullTaskProjection, null, null, null);
		mTaskCountArray = BindingUtils.readCountArray(cursor);
		cursor.close();
	}

	@Override
	protected ListConfig<Project> createListConfig()
	{
		return new ProjectListConfig();
	}

	@Override
	protected void deleteChildren(long groupId) {
		getContentResolver().delete(
				getDrilldownListConfig().getChildContentUri(), 
				Shuffle.Tasks.PROJECT_ID + " = ?", new String[] {String.valueOf(groupId)});
	}
	
	@Override
	protected Cursor createItemQuery() {
		Log.d(cTag, "Creating a cursor over all projects");
		return managedQuery(getIntent().getData(), Shuffle.Projects.cFullProjection,
				null, null, 
				Shuffle.Projects.NAME + " ASC");
	}
	
	@Override
	protected ListAdapter createListAdapter(Cursor cursor) {
		ListAdapter adapter =
			new SimpleCursorAdapter(this,
					android.R.layout.simple_list_item_1, cursor,
					new String[] { Shuffle.Projects.NAME },
					new int[] { android.R.id.text1 }) {

			public View getView(int position, View convertView, ViewGroup parent) {
				Cursor cursor = (Cursor)getItem(position);
				Project project = BindingUtils.readProject(cursor);
				ProjectView projectView;
				if (convertView instanceof ProjectView) {
					projectView = (ProjectView) convertView;
				} else {
					projectView = new ProjectView(parent.getContext());
				}
				projectView.setTaskCountArray(mTaskCountArray);
				projectView.updateView(project);
				return projectView;
			}

		};
		return adapter;
	}
	
    /**
     * Return the intent generated when a list item is clicked.
     * 
     * @param url type of data selected
     */ 
	@Override
    protected Intent getClickIntent(Uri uri) {
    	// if a project is clicked on, show tasks for that project.
    	Intent intent = new Intent(this, ProjectTasksActivity.class);
    	intent.setData(uri);
    	return intent;
    }

}

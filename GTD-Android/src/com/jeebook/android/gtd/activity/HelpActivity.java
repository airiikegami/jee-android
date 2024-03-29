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

import com.jeebook.android.gtd.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class HelpActivity extends Activity {
    public static final String cHelpPage = "helpPage";
    
	private Spinner mHelpSpinner;
	private TextView mHelpContent;
	private Button mPrevious;
	private Button mNext;
    	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        setContentView(R.layout.help);
        
        mHelpContent = (TextView) findViewById(R.id.help_text);
        mHelpSpinner = (Spinner) findViewById(R.id.help_screen);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        		this, R.array.help_screens,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHelpSpinner.setAdapter(adapter);
        mHelpSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	public void onNothingSelected(AdapterView<?> arg0) {
        		// do nothing
        	}
        	
        	public void onItemSelected(AdapterView<?> parent, View v,
        			int position, long id) {
        		int resId = HelpActivity.this.getResources().getIdentifier(
        				"help" + position, "string", "org.dodgybits.android.shuffle");
        		mHelpContent.setText(HelpActivity.this.getText(resId));
        		updateNavigationButtons();
        	}
        });

        mPrevious = (Button) findViewById(R.id.previous_button);
        mPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		int position = mHelpSpinner.getSelectedItemPosition();
            	mHelpSpinner.setSelection(position - 1);
            }
        });        

        
        mNext = (Button) findViewById(R.id.next_button);
        mNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		int position = mHelpSpinner.getSelectedItemPosition();
            	mHelpSpinner.setSelection(position + 1);
            }
        });        
        
        setSelectionFromBundle(getIntent().getExtras());
	}
	
	private void setSelectionFromBundle(Bundle bundle) {
        int position = 0;
        if (bundle != null) {
        	position = bundle.getInt(cHelpPage, 0);
        }
        mHelpSpinner.setSelection(position);
	}

	private void updateNavigationButtons() {
		int position = mHelpSpinner.getSelectedItemPosition();
		mPrevious.setEnabled(position > 0);
		mNext.setEnabled(position < mHelpSpinner.getCount() - 1);
	}
	
}

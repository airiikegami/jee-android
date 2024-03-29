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

package com.jeebook.android.gtd.activity.editor;

import com.jeebook.android.gtd.R;
import com.jeebook.android.gtd.model.Context;
import com.jeebook.android.gtd.model.State;
import com.jeebook.android.gtd.model.Context.Icon;
import com.jeebook.android.gtd.provider.Shuffle;
import com.jeebook.android.gtd.util.BindingUtils;
import com.jeebook.android.gtd.util.DrawableUtils;
import com.jeebook.android.gtd.util.TextColours;
import com.jeebook.android.gtd.view.ContextView;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ContextEditorActivity extends AbstractEditorActivity<Context> implements TextWatcher {

    private static final String cTag = "ContextEditorActivity";
   
    private static final int COLOUR_PICKER = 0;
    private static final int ICON_PICKER = 1;
    
    private int mColourIndex;
    private Icon mIcon;
    
    private EditText mNameWidget;

    private TextView mColourWidget;

    private ImageView mIconWidget;
    private TextView mIconNoneWidget;
    private ImageButton mClearIconButton;
	protected ContextView mContext;

	@Override
    protected void onCreate(Bundle icicle) {
        Log.d(cTag, "onCreate+");
        super.onCreate(icicle);
        
        loadCursor();
        findViewsAndAddListeners();
        
        if (mState == State.STATE_EDIT) {
            if (mCursor != null) {
                // Make sure we are at the one and only row in the cursor.
                mCursor.moveToFirst();
                setTitle(R.string.title_edit_context);
                mOriginalItem = BindingUtils.readContext(mCursor,getResources());
              	updateUIFromItem(mOriginalItem);
            } else {
                setTitle(getText(R.string.error_title));
                mNameWidget.setText(getText(R.string.error_message));
            }
        } else if (mState == State.STATE_INSERT) {
            setTitle(R.string.title_new_context);
            Bundle extras = getIntent().getExtras();
            updateUIFromExtras(extras);
        }
    }
    
    @Override
    protected boolean isValid() {
        String name = mNameWidget.getText().toString();
        return !TextUtils.isEmpty(name);
    }    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
    	Log.d(cTag, "Got resultCode " + resultCode + " with data " + data);		
    	switch (requestCode) {
    	case COLOUR_PICKER:
        	if (resultCode == Activity.RESULT_OK) {
    			if (data != null) {
    				mColourIndex = Integer.parseInt(data.getStringExtra("colour"));
    				displayColour();
    	        	updatePreview();
    			}
    		}
    		break;
    	case ICON_PICKER:
        	if (resultCode == Activity.RESULT_OK) {
    			if (data != null) {
    				String iconName = data.getStringExtra("iconName");
    				mIcon = Icon.createIcon(iconName, getResources());
    				displayIcon();
    	        	updatePreview();
    			}
    		}
    		break;
    		default:
    			Log.e(cTag, "Unknown requestCode: " + requestCode);
    	}
	}
    
    @Override
    protected void writeItem(ContentValues values, Context context) {
    	BindingUtils.writeContext(values, context);
    }

    /**
     * Take care of deleting a context.  Simply deletes the entry.
     */
    @Override
    protected void doDeleteAction() {
    	super.doDeleteAction();
        mNameWidget.setText("");
    }
    
    /**
     * @return id of layout for this view
     */
    @Override
    protected int getContentViewResId() {
    	return R.layout.context_editor;
    }

    @Override
    protected Context restoreItem(Bundle icicle) {
    	return BindingUtils.restoreContext(icicle,getResources());
    }
    
    @Override
    protected void saveItem(Bundle outState, Context item) {
    	BindingUtils.saveContext(outState, item);
    }
    
    @Override
    protected Intent getInsertIntent() {
    	return new Intent(Intent.ACTION_INSERT, Shuffle.Contexts.CONTENT_URI);
    }
    
    @Override
    protected CharSequence getItemName() {
    	return getString(R.string.context_name);
    }
    
    @Override
    protected Context createItemFromUI() {
        String name = mNameWidget.getText().toString();
        Long tracksId = null;
        if (mOriginalItem != null) {
            tracksId = mOriginalItem.tracksId;
        }
        return new Context(
                name, mColourIndex, mIcon, 
                tracksId, System.currentTimeMillis());
    }
    
    @Override
    protected void updateUIFromExtras(Bundle extras) {
    	if (mColourIndex == -1) {
    		mColourIndex = 0;
    	}
    	
    	displayIcon();
        displayColour();
    	updatePreview();
    }
    
    @Override
    protected void updateUIFromItem(Context context) {
        mNameWidget.setTextKeepState(context.name);
       	mColourIndex = context.colourIndex;
        mIcon = context.icon;

        displayColour();
    	displayIcon();
    	updatePreview();

        if (mOriginalItem == null) {
        	mOriginalItem = context;
        }    	
    }
    
    private void loadCursor() {
    	if (mUri != null && mState == State.STATE_EDIT)
    	{
            mCursor = managedQuery(mUri, Shuffle.Contexts.cFullProjection, null, null, null);
	        if (mCursor == null || mCursor.getCount() == 0) {
	            // The cursor is empty. This can happen if the event was deleted.
	            finish();
            }
    	}
    }
    
    private void findViewsAndAddListeners() {
        // The text view for our context description, identified by its ID in the XML file.
        mNameWidget = (EditText) findViewById(R.id.name);
        mNameWidget.addTextChangedListener(this);
        
        mColourWidget = (TextView) findViewById(R.id.colour_display);
        mColourIndex = -1;
        
        mIconWidget = (ImageView) findViewById(R.id.icon_display);
        mIconNoneWidget = (TextView) findViewById(R.id.icon_none);
        mIcon = Icon.NONE;

        View colourEntry = findViewById(R.id.colour_entry);
        colourEntry.setOnClickListener(this);
        colourEntry.setOnFocusChangeListener(this);

        View iconEntry = findViewById(R.id.icon_entry);
        iconEntry.setOnClickListener(this);
        iconEntry.setOnFocusChangeListener(this);
        
        mClearIconButton = (ImageButton) findViewById(R.id.icon_clear_button);
        mClearIconButton.setOnClickListener(this);
        mClearIconButton.setOnFocusChangeListener(this);
        
		mContext = (ContextView) findViewById(R.id.context_preview);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.colour_entry: {
            	// Launch activity to pick colour
            	Intent intent = new Intent(Intent.ACTION_PICK);
            	intent.setType(ColourPickerActivity.TYPE);
            	startActivityForResult(intent, COLOUR_PICKER);
                break;
            }

            case R.id.icon_entry: {
            	// Launch activity to pick icon
            	Intent intent = new Intent(Intent.ACTION_PICK);
            	intent.setType(IconPickerActivity.TYPE);
            	startActivityForResult(intent, ICON_PICKER);
                break;
            }
                    
            case R.id.icon_clear_button: {
	        	mIcon = Icon.NONE;
	        	displayIcon();
	        	updatePreview();
	        	break;
            }
            
            default:
            	super.onClick(v);
            	break;
        }
    }
    
	private void displayColour() {
		int bgColour = TextColours.getInstance(this).getBackgroundColour(mColourIndex);
		GradientDrawable drawable = DrawableUtils.createGradient(bgColour, Orientation.TL_BR);
		drawable.setCornerRadius(8.0f);
		mColourWidget.setBackgroundDrawable(drawable);
    }

	private void displayIcon() {
    	if (mIcon == Icon.NONE) {
    		mIconNoneWidget.setVisibility(View.VISIBLE);
    		mIconWidget.setVisibility(View.GONE);
    		mClearIconButton.setEnabled(false);
    	} else {
    		mIconNoneWidget.setVisibility(View.GONE);
    		mIconWidget.setImageResource(mIcon.largeIconId);
    		mIconWidget.setVisibility(View.VISIBLE);
    		mClearIconButton.setEnabled(true);
    	}
    }
	
	private void updatePreview() {
		String name = mNameWidget.getText().toString();
		if (TextUtils.isEmpty(name) || mColourIndex == -1) {
			mContext.setVisibility(View.GONE);
		} else {
			mContext.updateView(createItemFromUI());
			mContext.setVisibility(View.VISIBLE);
		}				
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		updatePreview();
	}
	
	
}

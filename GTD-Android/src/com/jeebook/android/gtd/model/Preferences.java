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

package com.jeebook.android.gtd.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Preferences {
    private static final String cTag = "Preferences";
    
    
	public static final String FIRST_TIME = "first_time";
	public static final String SCREEN_KEY = "screen";
	public static final String DELETE_COMPLETED_PERIOD_KEY = "delete_complete_period_str";
	public static final String LAST_DELETE_COMPLETED_KEY = "last_delete_completed";
	public static final String LAST_INBOX_CLEAN_KEY = "last_inbox_clean";
	
	public static final String DISPLAY_CONTEXT_ICON_KEY = "display_context_icon";
	public static final String DISPLAY_CONTEXT_NAME_KEY = "display_context_name";
	public static final String DISPLAY_PROJECT_KEY = "display_project";
	public static final String DISPLAY_DETAILS_KEY = "display_details";
	public static final String DISPLAY_DUE_DATE_KEY = "display_due_date";
	
	public static final String PROJECT_VIEW_KEY = "project_view";
	public static final String CONTEXT_VIEW_KEY = "context_view";

	public static final String TOP_LEVEL_COUNTS_KEY = "top_level_counts";
	public static final String CALENDAR_ID_KEY = "calendar_id";
	public static final String DEFAULT_REMINDER_KEY = "default_reminder";
	
	public static final String KEY_DEFAULT_REMINDER = "default_reminder";

    public static final String TRACKS_URL = "tracks_url";
    public static final String TRACKS_USER = "tracks_user";
    public static final String TRACKS_PASSWORD = "tracks_password";
    public static final String TRACKS_INTERVAL = "tracks_interval";


    public static boolean validateTracksSettings(Context context) {
        String url = getTracksUrl(context);
        String password = getTracksPassword(context);
        String user = getTracksUser(context);
        return user.length() != 0 && password.length() != 0 && url.length() != 0;

    }

    public static int getTracksInterval(Context context) {
             getSharedPreferences(context);
        return sPrefs.getInt(TRACKS_INTERVAL, 0);
    }

    public enum DeleteCompletedPeriod {
		hourly, daily, weekly, never
	}
	
	private static SharedPreferences sPrefs = null;
	
	private static SharedPreferences getSharedPreferences(Context context) {
		if (sPrefs == null) {
			sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return sPrefs;
	}
	
	public static boolean isFirstTime(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(FIRST_TIME, true);
	}

    public static String getTracksUrl(Context context) {
        getSharedPreferences(context);
        return sPrefs.getString(TRACKS_URL, 
                context.getString(com.jeebook.android.gtd.R.string.tracks_url_settings));
    }

       public static String getTracksUser(Context context) {
        getSharedPreferences(context);
        return sPrefs.getString(TRACKS_USER, "");
    }

        public static String getTracksPassword(Context context) {
        getSharedPreferences(context);
        return sPrefs.getString(TRACKS_PASSWORD, "");
    }
	
	public static String getDeleteCompletedPeriod(Context context) {
		getSharedPreferences(context);
		return sPrefs.getString(DELETE_COMPLETED_PERIOD_KEY, DeleteCompletedPeriod.never.name());
	}

	public static long getLastDeleteCompleted(Context context) {
		getSharedPreferences(context);
		return sPrefs.getLong(LAST_DELETE_COMPLETED_KEY, 0L);
	}

	public static long getLastInboxClean(Context context) {
		getSharedPreferences(context);
		return sPrefs.getLong(LAST_INBOX_CLEAN_KEY, 0L);
	}
	
	public static int getDefaultReminderMinutes(Context context) {
		getSharedPreferences(context);
        String durationString =
        	sPrefs.getString(Preferences.DEFAULT_REMINDER_KEY, "0");
		return Integer.parseInt(durationString);
	}
	

	public static Boolean isProjectViewExpandable(Context context) {
		getSharedPreferences(context);
		return !sPrefs.getBoolean(PROJECT_VIEW_KEY, false);
	}

	public static Boolean isContextViewExpandable(Context context) {
		getSharedPreferences(context);
		return !sPrefs.getBoolean(CONTEXT_VIEW_KEY, true);
	}

	public static boolean displayContextIcon(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(DISPLAY_CONTEXT_ICON_KEY, true);
	}

	public static boolean displayContextName(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(DISPLAY_CONTEXT_NAME_KEY, true);
	}

	public static boolean displayDueDate(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(DISPLAY_DUE_DATE_KEY, true);
	}

	public static boolean displayProject(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(DISPLAY_PROJECT_KEY, true);
	}

	public static boolean displayDetails(Context context) {
		getSharedPreferences(context);
		return sPrefs.getBoolean(DISPLAY_DETAILS_KEY, true);
	}

	public static int[] getTopLevelCounts(Context context) {
		getSharedPreferences(context);
		String countString = sPrefs.getString(Preferences.TOP_LEVEL_COUNTS_KEY, null);
		int[] result = null;
		if (countString != null) {
			String[] counts = countString.split(",");
			result = new int[counts.length];
			for(int i = 0; i < counts.length; i++) {
				result[i] = Integer.parseInt(counts[i]);
			}
		}
		return result;
	}
	
	public static int getCalendarId(Context context) {
        getSharedPreferences(context);
        int id = 1;
        String calendarIdStr = sPrefs.getString(CALENDAR_ID_KEY, null);
        if (calendarIdStr != null) {
            try {
                id = Integer.parseInt(calendarIdStr, 10);
            } catch (NumberFormatException e) {
                Log.e(cTag, "Failed to parse calendar id: " + e.getMessage());
            }
        }
        return id;
	}
	
	public static SharedPreferences.Editor getEditor(Context context) {
		getSharedPreferences(context);
		return sPrefs.edit();
	}
	
	public static void cleanUpInbox(Context context) {
		SharedPreferences.Editor ed = getEditor(context);
		ed.putLong(LAST_INBOX_CLEAN_KEY, System.currentTimeMillis());
		ed.commit();
	}

	
}

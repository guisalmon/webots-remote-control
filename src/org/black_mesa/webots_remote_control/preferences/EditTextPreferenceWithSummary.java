package org.black_mesa.webots_remote_control.preferences;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class EditTextPreferenceWithSummary extends EditTextPreference {
	public EditTextPreferenceWithSummary(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
	    super.onDialogClosed(positiveResult);
	     
	    setSummary(getSummary());
	}
	
	@Override
	public CharSequence getSummary() {
	    return this.getText();
	}
}
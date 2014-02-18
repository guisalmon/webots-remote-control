package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PreferencesFragment extends PreferenceFragment {
	 @Override
     public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
         // Can retrieve arguments from preference XML.
         Log.i("args", "Arguments: " + getArguments());

         // Load the preferences from an XML resource
         addPreferencesFromResource(R.xml.fragmented_preferences_inner);
	 }
	
}

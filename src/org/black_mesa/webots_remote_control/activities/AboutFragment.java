package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class AboutFragment extends PreferenceFragment {
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.about_fragment);
        // Update the version number
        try {
            final PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            findPreference("about_version").setSummary(packageInfo.versionName);
        } catch (final NameNotFoundException e) {
            findPreference("about_version").setSummary("?");
        }

	}

}	
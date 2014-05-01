package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.webkit.WebView;

public class AboutFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.about_fragment);
		// Update the version number
		try {
			final PackageInfo packageInfo =
					getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			findPreference("about_version").setSummary(packageInfo.versionName);
		} catch (final NameNotFoundException e) {
			findPreference("about_version").setSummary("?");
		}
		Preference mLicense = findPreference("about_license");
		mLicense.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				createLicenseDialog(getActivity()).show();
				return true;
			}
		});

	}

	public static final AlertDialog createLicenseDialog(Context context) {
		WebView webView = new WebView(context);
		webView.loadUrl("file:///android_asset/license.html");
		return new AlertDialog.Builder(context).setTitle(R.string.about_open_source_licenses).setView(webView)
				.setPositiveButton(android.R.string.ok, null).create();
	}

}
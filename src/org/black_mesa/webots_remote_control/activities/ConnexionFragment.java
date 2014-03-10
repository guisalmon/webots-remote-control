package org.black_mesa.webots_remote_control.activities;

import java.util.List;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.database.DataSource;
import org.black_mesa.webots_remote_control.database.ServerListAdapter;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConnexionFragment extends ListFragment{
	private DataSource mDatasource;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.connexion_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//Initiate database
		mDatasource = new DataSource(getActivity());
		mDatasource.open();
		
		List<Server> values = mDatasource.getAllServers();
		
		ServerListAdapter adapter = new ServerListAdapter(getActivity(), values);
		setListAdapter(adapter);
		
	}
	
	@Override
	public void onPause() {
		mDatasource.close();
		super.onPause();
	}

	@Override
	public void onResume() {
		mDatasource.open();
		super.onResume();
	}
	

	

}

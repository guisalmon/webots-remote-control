package org.black_mesa.webots_remote_control.activities;

import java.util.List;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.database.DataSource;
import org.black_mesa.webots_remote_control.database.ServerListAdapter;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ConnexionFragment extends ListFragment{
	private DataSource mDatasource;
	private ListAdapter mAdapter;
	private List<Server> mServers;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

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
				
		mServers = mDatasource.getAllServers();
						
		mAdapter = new ServerListAdapter(getActivity(), mServers);
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//mDatasource.deleteServer(mServers.get(position));
				//getListView().invalidate();
				Log.i(getClass().getName(), "Click ! ");
			}
		});
		
		
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getActivity().getMenuInflater().inflate(R.menu.connexion, menu);
		menu.getItem(0).setVisible(false);
		menu.getItem(1).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.connexion_add:
			Log.i(getClass().getName(), "Add server !");
			Intent i = new Intent(getActivity(), AddServerActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
	}

	@Override
	public void onPause() {
		mDatasource.close();
		super.onPause();
	}

	@Override
	public void onResume() {
		mDatasource.open();
		getListView().invalidate();
		super.onResume();
	}
}

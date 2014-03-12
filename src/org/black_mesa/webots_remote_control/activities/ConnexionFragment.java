package org.black_mesa.webots_remote_control.activities;

import java.util.ArrayList;
import java.util.List;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.database.DataSource;
import org.black_mesa.webots_remote_control.database.ServerListAdapter;
import org.black_mesa.webots_remote_control.listeners.OnListEventsListener;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ConnexionFragment extends ListFragment implements OnListEventsListener{
	private DataSource mDatasource;
	private ArrayAdapter<Server> mAdapter;
	private List<Server> mServers;
	private Menu mMenu;
	private List<Server> mSelectedServers;
	
	
	
	//Activity lifecycle
	
	
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
		updateView();
		
		mSelectedServers = new ArrayList<Server>();
	}
	
	@Override
	public void onPause() {
		mDatasource.close();
		super.onPause();
	}

	@Override
	public void onResume() {
		mDatasource.open();
		updateView();
		super.onResume();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getActivity().getMenuInflater().inflate(R.menu.connexion, menu);
		mMenu = menu;
		updateMenu(false);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.connexion_add:
			Intent i = new Intent(getActivity(), AddServerActivity.class);
			startActivity(i);
			break;
		case R.id.server_delete:
			deleteSelection();
			break;
		case R.id.server_edit:
			editServer();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	//OnListEventsListener
	

	@Override
	public void onCheckChanged(boolean isChecked, int position) {
		if (isChecked){
			mSelectedServers.add((Server)getListView().getItemAtPosition(position));
		}else{
			mSelectedServers.remove((Server)getListView().getItemAtPosition(position));
		}
		updateMenu(true);
	}

	@Override
	public void onItemClicked(int position) {
		//TODO Launch server here
		Log.i(getClass().getName(), position+" Click !");
	}

	@Override
	public void onItemLongClicked(int position) {
		getListView().getChildAt(position).setBackgroundColor(Color.CYAN);
		mSelectedServers.add((Server)getListView().getItemAtPosition(position));
		updateMenu(true);
	}
	
	
	
	//Private methods
	
	
	private void updateView(){
		Log.i(getClass().getName(), "Update View");
		mServers = mDatasource.getAllServers();
		mAdapter = new ServerListAdapter(getActivity(), mServers, this);
		setListAdapter(mAdapter);
	}
	
	private void deleteSelection() {
		for(Server s : mSelectedServers){
			mDatasource.deleteServer(s);
		}
		mSelectedServers.clear();
		updateView();
		updateMenu(false);
	}
	
	private void editServer() {
		// TODO Auto-generated method stub
		
	}
	
	private void updateMenu(boolean isSelection){
		if(isSelection){
			switch (mSelectedServers.size()){
			case 0:
				updateMenu(false);
				break;
			case 1:
				mMenu.getItem(0).setVisible(true);
				mMenu.getItem(1).setVisible(true);
				mMenu.getItem(2).setVisible(false);
				break;
			default:
				mMenu.getItem(0).setVisible(false);
				mMenu.getItem(1).setVisible(true);
				mMenu.getItem(2).setVisible(false);
			}
		}else{
			mMenu.getItem(0).setVisible(false);
			mMenu.getItem(1).setVisible(false);
			mMenu.getItem(2).setVisible(true);
		}
	}
}

package org.black_mesa.webots_remote_control.activities;

import java.util.List;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.client.ConnectionState;
import org.black_mesa.webots_remote_control.database.DataSource;
import org.black_mesa.webots_remote_control.database.ServerListAdapter;
import org.black_mesa.webots_remote_control.listeners.ConnectionManagerListener;
import org.black_mesa.webots_remote_control.listeners.OnListEventsListener;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;

public class ConnectionFragment extends ListFragment implements OnListEventsListener, ConnectionManagerListener{
	private DataSource mDatasource;
	private ArrayAdapter<Server> mAdapter;
	private List<Server> mServers;
	private List<View> mRows;
	private Menu mMenu;
	
	
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
		
		MainActivity.CONNECTION_MANAGER.addListener(this);
	}
	
	@Override
	public void onPause() {
		mDatasource.close();
		super.onPause();
	}

	@Override
	public void onResume() {
		mDatasource.open();
		getActivity().invalidateOptionsMenu();
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
		updateMenu(true);
	}

	@Override
	public void onItemClicked(int position) {
		clearChecks();
		updateMenu(true);
		Log.i(getClass().getName(), position+" Click !");
	}

	@Override
	public void onItemLongClicked(int position) {
		updateMenu(true);
	}
	
	@Override
	public void onItemLaunchListener(int position) {
		
		if(MainActivity.CONNECTION_MANAGER.getServerList().contains(mServers.get(position))){
			((Button)mRows.get(position).findViewById(R.id.server_state_button)).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_send, 0);
			((MainActivity)getActivity()).disconnect(mServers.get(position));
		}else{
			mRows.get(position).findViewById(R.id.server_state_button).setVisibility(View.GONE);
			mRows.get(position).findViewById(R.id.server_connecting).setVisibility(View.VISIBLE);
			((MainActivity)getActivity()).connect(mServers.get(position));
		}
		updateMenu(true);
	}
	
	
	
	//ConnectionManagerListener
	
	
	@Override
	public void onStateChange(Server server, ConnectionState state) {
		int i = mServers.indexOf(server);
		Log.i(getClass().getName(), "State Change");
		switch (state) {
		case CONNECTED:
			Log.i(getClass().getName(), "Connected");
			((Button)mRows.get(i).findViewById(R.id.server_state_button)).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
			mRows.get(i).findViewById(R.id.server_state_button).setVisibility(View.VISIBLE);
			mRows.get(i).findViewById(R.id.server_connecting).setVisibility(View.GONE);
			break;
		case COMMUNICATION_ERROR:
		case CONNECTION_ERROR:
			((Button)mRows.get(i).findViewById(R.id.server_state_button)).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_send, 0);
			mRows.get(i).findViewById(R.id.server_state_button).setVisibility(View.VISIBLE);
			mRows.get(i).findViewById(R.id.server_connecting).setVisibility(View.GONE);
		default:
			break;
		}
		updateMenu(false);
		
	}
	
	
	
	//Private methods
	
	
	private void clearChecks() {
		for(int i = 0; i < getListView().getChildCount(); i++){
			((CheckBox)getListView().getChildAt(i).findViewById(R.id.server_select)).setChecked(false);
		}
		
	}
	
	private void updateView(){
		Log.i(getClass().getName(), "Update View");
		mServers = mDatasource.getAllServers();
		mAdapter = new ServerListAdapter(getActivity(), mServers, MainActivity.CONNECTION_MANAGER.getServerList(), this);
		setListAdapter(mAdapter);
		mRows = ((ServerListAdapter)mAdapter).getRows();
	}
	
	private void deleteSelection() {
		for(int i = 0; i < getListView().getChildCount(); i++){
			boolean check = ((CheckBox)mRows.get(i).findViewById(R.id.server_select)).isChecked();
			if(check) mDatasource.deleteServer(mServers.get(i));
		}
		updateView();
		updateMenu(false);
	}
	
	private void editServer() {
		Bundle b = new Bundle();
		Intent intent = new Intent(getActivity(), AddServerActivity.class);
		Long id = (long) 0;
		for (int i = 0; i < mRows.size(); i++){
			if(((CheckBox)mRows.get(i).findViewById(R.id.server_select)).isChecked()){
				id = mServers.get(i).getId();
			}
		}
		b.putLong("id", id);
		intent.putExtras(b);
		startActivity(intent);
	}
	
	private int countChecks(){
		int i = 0;
		for (int j = 0; j < getListView().getChildCount(); j++){
			if(((CheckBox)getListView().getChildAt(j).findViewById(R.id.server_select)).isChecked()) i++;
		}
		return i;
	}
	
	private void updateMenu(boolean isSelection){
		if(MainActivity.CONNECTION_MANAGER.getServerList().isEmpty()){
			if(isSelection){
				switch (countChecks()){
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
		}else{
			mMenu.getItem(0).setVisible(false);
			mMenu.getItem(1).setVisible(false);
			mMenu.getItem(2).setVisible(false);
		}
	}
}

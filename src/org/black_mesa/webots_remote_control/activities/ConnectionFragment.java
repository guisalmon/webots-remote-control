package org.black_mesa.webots_remote_control.activities;

import java.util.List;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.client.ConnectionState;
import org.black_mesa.webots_remote_control.database.DataSource;
import org.black_mesa.webots_remote_control.database.Server;
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
import android.widget.CheckBox;

public class ConnectionFragment extends ListFragment implements OnListEventsListener, ConnectionManagerListener {
	private DataSource mDatasource;
	private ServerListAdapter mAdapter;
	private List<Server> mServers;
	private Menu mMenu;

	// Activity lifecycle

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
		// Initiate database
		mDatasource = new DataSource(getActivity());
		mDatasource.open();

		mServers = mDatasource.getAllServers();

		MainActivity.CONNECTION_MANAGER.addListener(this);
	}

	@Override
	public void onPause() {
		mDatasource.close();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mDatasource.open();
		updateView();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		getActivity().getMenuInflater().inflate(R.menu.connexion, menu);
		mMenu = menu;
		updateMenu(false);
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

	// OnListEventsListener

	@Override
	public void onCheckChanged() {
		updateMenu(true);
	}

	@Override
	public void onItemClicked() {
		clearChecks();
		updateMenu(true);
	}

	@Override
	public void onItemLongClicked() {
		// TODO Nothing to do here
		// updateMenu(true);
	}

	@Override
	public void onItemLaunchListener(Server s) {
		if (MainActivity.CONNECTED_SERVERS.contains(s)) {
			mAdapter.setServerDisconnected(s.getId());
			((MainActivity) getActivity()).disconnect(s);
		} else {
			mAdapter.setServerConnected(s.getId());
			((MainActivity) getActivity()).connect(s);
		}
		updateMenu(true);
	}

	// ConnectionManagerListener

	@Override
	public void onStateChange(Server server, ConnectionState state) {
		int i = mServers.indexOf(server);
		Log.i(getClass().getName(), "State Change : " + i);
		switch (state) {
		case CONNECTED:
			Log.i(getClass().getName(), "Connected");
			mAdapter.setServerConnected(server.getId());
			updateMenu(false);
			break;
		case COMMUNICATION_ERROR:
		case CONNECTION_ERROR:
			mAdapter.setServerDisconnected(server.getId());
		default:
			updateMenu(true);
			break;
		}

	}

	// Private methods

	private void clearChecks() {
		for (int i = 0; i < getListView().getChildCount(); i++) {
			((CheckBox) getListView().getChildAt(i).findViewById(R.id.server_select)).setChecked(false);
		}

	}

	private void updateView() {
		Log.i(getClass().getName(), "Update View");
		mServers = mDatasource.getAllServers();
		mAdapter = new ServerListAdapter(getActivity(), mServers, MainActivity.CONNECTED_SERVERS, this);
		setListAdapter(mAdapter);
	}

	private void deleteSelection() {
		for (Server s : mAdapter.getCheckedServers()) {
			mDatasource.deleteServer(s);
		}
		updateView();
		updateMenu(false);
	}

	private void editServer() {
		Bundle b = new Bundle();
		Intent intent = new Intent(getActivity(), AddServerActivity.class);
		List<Server> checkedServers = mAdapter.getCheckedServers();
		if (!checkedServers.isEmpty()) {
			b.putLong("id", checkedServers.get(0).getId());
			intent.putExtras(b);
			startActivity(intent);
		}
	}

	private int countChecks() {
		int i = 0;
		for (int j = 0; j < getListView().getChildCount(); j++) {
			if (((CheckBox) getListView().getChildAt(j).findViewById(R.id.server_select)).isChecked()) {
				i++;
			}
		}
		return i;
	}

	private void updateMenu(boolean isSelection) {
		if (MainActivity.CONNECTED_SERVERS.isEmpty()) {
			if (isSelection) {
				int i = countChecks();
				switch (i) {
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
			} else {
				mMenu.getItem(0).setVisible(false);
				mMenu.getItem(1).setVisible(false);
				mMenu.getItem(2).setVisible(true);
			}
		} else {
			mMenu.getItem(0).setVisible(false);
			mMenu.getItem(1).setVisible(false);
			mMenu.getItem(2).setVisible(false);
		}
	}
}

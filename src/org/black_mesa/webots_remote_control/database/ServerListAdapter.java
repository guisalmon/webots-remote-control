package org.black_mesa.webots_remote_control.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.listeners.OnListEventsListener;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ServerListAdapter extends ArrayAdapter<Server> {

	private final Context context;
	private final Map<Long, View> mRows;
	private final Map<View, Server> mSRows;
	private final List<Server> mServers;
	private final List<Server> onlineServers;
	private List<CheckBox> boxes;
	private OnListEventsListener eventsListener;

	public ServerListAdapter(Context context, List<Server> servers, List<Server> onlineServers,
			OnListEventsListener eventsListener) {
		super(context, R.layout.server_list_item, servers);
		this.context = context;
		this.mServers = servers;
		this.mRows = new HashMap<Long, View>();
		this.mSRows = new HashMap<View, Server>();
		this.onlineServers = onlineServers;
		this.eventsListener = eventsListener;
		boxes = new ArrayList<CheckBox>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Inflating the layout for the row
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.server_list_item, parent, false);
		
		//Getting the server for this view
		Server server = mServers.get(position);
		
		//Setting visual informations according to the state of the server
		TextView nameText = (TextView) rowView.findViewById(R.id.server_name);
		TextView adressText = (TextView) rowView.findViewById(R.id.server_adress);
		nameText.setText(server.getName());
		adressText.setText(server.getAdress() + ":" + server.getPort());
		Button button = (Button) rowView.findViewById(R.id.server_state_button);
		if (onlineServers.contains(server)) {
			button.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
		} else {
			button.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_send, 0);
		}
		
		//Setting listener for the Launch/Cancel button
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(getClass().getName(), "Server ID : "+mSRows.get(v.getParent()).getId());
				eventsListener.onItemLaunchListener(mSRows.get(v.getParent()));
			}
		});
		
		//Setting listener for the checkbox
		CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.server_select);
		boxes.add(checkBox);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				eventsListener.onCheckChanged();
			}
		});
		
		//Setting listeners for the clicks and long clicks on the row. Not especially useful, but it's there.
		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				eventsListener.onItemClicked();
			}
		});
		rowView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				eventsListener.onItemLongClicked();
				return false;
			}
		});
		mRows.put(server.getId(), rowView);
		mSRows.put(rowView, server);
		rowView.setClickable(true);
		return rowView;
	}
	
	/**
	 * Sets a progression bar instead of the launch button on the specified server
	 * @param id of the server currently waiting for connexion
	 */
	public void setServerWaiting(long id){
		mRows.get(id).findViewById(R.id.server_state_button).setVisibility(View.GONE);
		mRows.get(id).findViewById(R.id.server_connecting).setVisibility(View.VISIBLE);
	}
	
	/**
	 * Sets the launch button with a cancel drawable
	 * @param id of the server that just got connected
	 */
	public void setServerConnected(long id){
		((Button)mRows.get(id).findViewById(R.id.server_state_button)).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
		mRows.get(id).findViewById(R.id.server_state_button).setVisibility(View.VISIBLE);
		mRows.get(id).findViewById(R.id.server_connecting).setVisibility(View.GONE);
	}
	
	/**
	 * Sets the launch button with a launch drawable
	 * @param id of the server that is disconnected
	 */
	public void setServerDisconnected(long id){
		((Button)mRows.get(id).findViewById(R.id.server_state_button)).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_send, 0);
		mRows.get(id).findViewById(R.id.server_state_button).setVisibility(View.VISIBLE);
		mRows.get(id).findViewById(R.id.server_connecting).setVisibility(View.GONE);
	}

	/**
	 * Gives a list of all the server that corresponds to rows with a checked checkbox
	 * @return a list of the checked servers
	 */
	public List<Server> getCheckedServers() {
		List<Server> servers = new ArrayList<Server>();
		for(CheckBox c : boxes){
			if(c.isChecked()){
				servers.add(mSRows.get(c.getParent()));
			}
		}
		return servers;
	}
}

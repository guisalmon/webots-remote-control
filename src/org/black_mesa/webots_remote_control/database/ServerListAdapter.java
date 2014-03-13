package org.black_mesa.webots_remote_control.database;

import java.util.ArrayList;
import java.util.List;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.listeners.OnListEventsListener;

import android.content.Context;
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

public class ServerListAdapter extends ArrayAdapter<Server>{

	private final Context context;
	private final List<Server> servers;
	private List<CheckBox> boxes;
	private List<View> rows;
	private OnListEventsListener eventsListener;

	public ServerListAdapter(Context context,  List<Server> servers, OnListEventsListener eventsListener) {
		super(context,  R.layout.server_list_item, servers);
		this.context = context;
		this.servers = servers;
		this.eventsListener = eventsListener;
		boxes = new ArrayList<CheckBox>();
		rows = new ArrayList<View>();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.server_list_item, parent, false);
		
		Server server = servers.get(position);
		TextView nameText = (TextView) rowView.findViewById(R.id.server_name);
		TextView adressText = (TextView) rowView.findViewById(R.id.server_adress);
		nameText.setText(server.getName());
		adressText.setText(server.getAdress()+":"+server.getPort());
		Button button = (Button)rowView.findViewById(R.id.server_state_button);
		button.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_send, 0);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				eventsListener.onItemLaunchListener(rows.indexOf(v.getParent()));
			}
		});
		CheckBox checkBox = (CheckBox)rowView.findViewById(R.id.server_select);
		boxes.add(checkBox);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				eventsListener.onCheckChanged(isChecked, boxes.indexOf(buttonView));
			}
		});
		rowView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				eventsListener.onItemClicked(rows.indexOf(v));
			}
		});
		rowView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				eventsListener.onItemLongClicked(rows.indexOf(v));
				return false;
			}
		});
		rows.add(rowView);
		rowView.setClickable(true);
		return rowView;
	}
}

package org.black_mesa.webots_remote_control.database;

import java.util.List;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ServerListAdapter extends ArrayAdapter<Server>{

	private final Context context;
	private final List<Server> servers;

	public ServerListAdapter(Context context,  List<Server> servers) {
		super(context,  R.layout.server_list_item, servers);
		this.context = context;
		this.servers = servers;
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
		
		rowView.setClickable(true);
		return rowView;
	}
}

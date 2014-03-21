package org.black_mesa.webots_remote_control.activities;

import java.util.List;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.database.DataSource;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class AddServerActivity extends Activity {
	private DataSource mDatasource;
	private boolean mIsEdit;

	// Activity lifecycle

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIsEdit = getIntent().hasExtra("id");
		setContentView(R.layout.activity_add_server);
		getActionBar().setHomeButtonEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		mDatasource = new DataSource(this);
		mDatasource.open();
		if (mIsEdit) {
			Server server = getServerById(getIntent().getExtras().getLong("id"));
			((EditText) findViewById(R.id.serverName)).setText(server.getName());
			((EditText) findViewById(R.id.serverAdress)).setText(server.getAdress());
			((EditText) findViewById(R.id.serverPort)).setText(String.valueOf(server.getPort()));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_server, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.new_server_validate:
			saveServer();
			finish();
			break;
		case R.id.new_server_cancel:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		mDatasource.close();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mDatasource.open();
		super.onResume();
	}

	// Private methods

	private void saveServer() {
		if (mIsEdit) {
			Server server = getServerById(getIntent().getExtras().getLong("id"));
			server.setName(((EditText) findViewById(R.id.serverName)).getText().toString());
			server.setAdress(((EditText) findViewById(R.id.serverAdress)).getText().toString());
			server.setPort(Integer.parseInt(((EditText) findViewById(R.id.serverPort)).getText().toString()));
			mDatasource.updateServer(server);
		} else {
			String name = ((EditText) findViewById(R.id.serverName)).getText().toString();
			String adress = ((EditText) findViewById(R.id.serverAdress)).getText().toString();
			int port = Integer.parseInt(((EditText) findViewById(R.id.serverPort)).getText().toString());
			mDatasource.createServer(name, adress, port);
		}
	}

	private Server getServerById(long id) {
		List<Server> servers = mDatasource.getAllServers();
		for (Server s : servers) {
			if (s.getId() == id) {
				return s;
			}
		}
		return null;
	}

}

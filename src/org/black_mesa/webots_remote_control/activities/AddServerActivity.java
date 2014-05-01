package org.black_mesa.webots_remote_control.activities;

import java.util.List;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.database.DataSource;
import org.black_mesa.webots_remote_control.database.Server;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

public class AddServerActivity extends Activity {
	private DataSource mDatasource;
	private boolean mIsEdit;

	// Activity lifecycle

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
			setTitle(R.string.title_activity_edit_server);
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
			if (isInputValid()) {
				saveServer();
			}
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

	private boolean isInputValid() {
		String name = ((EditText) findViewById(R.id.serverName)).getText().toString();
		String address = ((EditText) findViewById(R.id.serverAdress)).getText().toString();
		String portText = ((EditText) findViewById(R.id.serverPort)).getText().toString();
		return !(name.matches("") || address.matches("") || portText.matches(""));
	}

	private void saveServer() {
		String name = ((EditText) findViewById(R.id.serverName)).getText().toString();
		String address = ((EditText) findViewById(R.id.serverAdress)).getText().toString();
		String portText = ((EditText) findViewById(R.id.serverPort)).getText().toString();
		if (mIsEdit) {
			Server server = getServerById(getIntent().getExtras().getLong("id"));
			server.setName(name);
			server.setAdress(address);
			server.setPort(Integer.parseInt(portText));
			mDatasource.updateServer(server);
		} else {
			mDatasource.createServer(name, address, Integer.parseInt(portText));
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

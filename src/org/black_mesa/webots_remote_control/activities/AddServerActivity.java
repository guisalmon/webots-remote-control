package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.database.DataSource;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class AddServerActivity extends Activity {
	private DataSource mDatasource;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_server);
		getActionBar().setHomeButtonEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		mDatasource = new DataSource(this);
		mDatasource.open();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
	
	private void saveServer (){
		String name = ((EditText)findViewById(R.id.serverName)).getText().toString();
		String adress = ((EditText)findViewById(R.id.serverAdress)).getText().toString();
		int port = Integer.parseInt(((EditText)findViewById(R.id.serverPort)).getText().toString());
		mDatasource.createServer(name, adress, port);
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
	
	

}

package org.black_mesa.webots_remote_control.activities;

import java.util.ArrayList;
import java.util.List;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.client.ConnectionManager;
import org.black_mesa.webots_remote_control.client.ConnectionState;
import org.black_mesa.webots_remote_control.listeners.ConnectionManagerListener;
import org.black_mesa.webots_remote_control.preferences.PreferencesFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements ConnectionManagerListener{
	
	public static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();
	
	public List<Server> mConnectedServers;
	
	private String[] mDrawerListItems;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mClosed;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Set application in fullscreen mode
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Set this as the connection manager listener 
		CONNECTION_MANAGER.addListener(this);
		mConnectedServers = new ArrayList<Server>();
		
		//Create and populate the left drawer
		setContentView(R.layout.activity_main);
        mDrawerListItems = getResources().getStringArray(R.array.drawer_list_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerListItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        //Enable toggling the drawer by the application bar
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.app_name) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mClosed = true;
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(R.string.app_name);
                mClosed = false;
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        selectItem(0);
        mClosed = true;

	}

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		if (!mClosed){
			menu.clear();
		}
        return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onPause() {
		CONNECTION_MANAGER.stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		CONNECTION_MANAGER.start();
		for (Server s : mConnectedServers){
			CONNECTION_MANAGER.addServer(s);
		}
		super.onResume();
	}
	
	public void disconnect(Server s){

		CONNECTION_MANAGER.removeServer(s);
		mConnectedServers.remove(s);
	}
	
	public void connect(Server s){
		if(!mConnectedServers.contains(s)){
			CONNECTION_MANAGER.addServer(s);
		}
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		FragmentManager fragmentManager;
		switch (position){
		case 0:
			//invalidateOptionsMenu();
			Fragment connexionFragment = new ConnectionFragment();
			fragmentManager = getFragmentManager();
		    fragmentManager.beginTransaction()
		                   .replace(R.id.content_frame, connexionFragment)
		                   .commit();
			break;
		case 1:
			for(Server s : mConnectedServers){
				Log.i(getClass().getName(), s.getId()+"\n"+s.getName()+"\n"+s.getAdress());
			}
			Log.i(getClass().getName(), mConnectedServers.get(0).getId()+"\n"+mConnectedServers.get(0).getName()+"\n"+mConnectedServers.get(0).getAdress());
			Fragment cameraFragment = new CameraFragment();
			Bundle b = new Bundle();
			b.putLong("ServerId", mConnectedServers.get(0).getId());
			cameraFragment.setArguments(b);
			fragmentManager = getFragmentManager();
		    fragmentManager.beginTransaction()
		                   .replace(R.id.content_frame, cameraFragment)
		                   .commit();
			break;
		case 2:
			Fragment preferencesFragment = new PreferencesFragment();
			fragmentManager = getFragmentManager();
		    fragmentManager.beginTransaction()
		                   .replace(R.id.content_frame, preferencesFragment)
		                   .commit();
			break;
		case 3:
			break;
		case 4:
			break;
		default:
				
		}
		
	    // Create a new fragment and specify the planet to show based on position
	    /*Fragment fragment = new PlanetFragment();
	    Bundle args = new Bundle();
	    args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
	    fragment.setArguments(args);*/

	    // Insert the fragment by replacing any existing fragment
	    /*FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, fragment)
	                   .commit();*/

	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(position, true);
	    setTitle(mDrawerListItems[position]);
	    
	    mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
	    getActionBar().setTitle(title);
	}


	@Override
	public void onStateChange(Server server, ConnectionState state) {
		switch (state) {
		case CONNECTED:
			if (!mConnectedServers.contains(server)){
				mConnectedServers.add(server);
			}
			Toast.makeText(this, "Connected ! ", Toast.LENGTH_SHORT).show();
			break;
		case COMMUNICATION_ERROR:
		case CONNECTION_ERROR:
			if (mConnectedServers.contains(server)){
				mConnectedServers.remove(server);
			}
			Toast.makeText(this, "Disconnected ! ", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}


}

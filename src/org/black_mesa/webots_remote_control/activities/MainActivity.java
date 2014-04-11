package org.black_mesa.webots_remote_control.activities;

import java.util.ArrayList;
import java.util.List;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.classes.Server;
import org.black_mesa.webots_remote_control.client.CamerasManager;
import org.black_mesa.webots_remote_control.client.ConnectionManager;
import org.black_mesa.webots_remote_control.client.ConnectionState;
import org.black_mesa.webots_remote_control.listeners.ConnectionManagerListener;

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
	public static final List<Server> CONNECTED_SERVERS = new ArrayList<Server>();
	public static CamerasManager CAMERAS_MANAGER = new CamerasManager(CONNECTION_MANAGER);
	public static final int CAMERA_INTERACTION_MODE = 1;
	
	private List<String> mDrawerListItems;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<String> mDrawerAdapter;
    private boolean mClosed;
    private Menu mMenu;
    private String mCurTitle;
    private List<Server> mServersToReconnect;
    
    
    
    //Activity Lifecycle
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Set application in fullscreen mode
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Set this as the connection manager listener 
		CONNECTION_MANAGER.addListener(this);
		
		mServersToReconnect = new ArrayList<Server>();
		
		
		//Create and populate the left drawer
		setContentView(R.layout.activity_main);
		mDrawerListItems = new ArrayList<String>();
        updateDrawer();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerAdapter = new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerListItems);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        //Enable toggling the drawer by the application bar
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.app_name) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mClosed = true;
                getActionBar().setTitle(mCurTitle);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(R.string.app_name);
                mClosed = false;
                mMenu.clear();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        selectItem(0);
        mClosed = true;

	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	
	@Override
	protected void onPause() {
		mServersToReconnect = new ArrayList<Server>(CONNECTED_SERVERS);
		CONNECTION_MANAGER.dispose();
		super.onPause();
	}

	@Override
	protected void onResume() {
		for(Server s : mServersToReconnect){
			Log.i(getClass().getName(), "Server "+s.getName()+" connecting");
			connect(s);
		}
		super.onResume();
	}

	@Override
	public void setTitle(CharSequence title) {
	    getActionBar().setTitle(title);
	}

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		if (!mClosed){
			menu.clear();
		}
		mMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
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
        return super.onOptionsItemSelected(item);
    }
	


    //ConnectionManagerListener


	@Override
	public void onStateChange(Server server, ConnectionState state) {
		switch (state) {
		case CONNECTED:
			if(!CONNECTED_SERVERS.contains(server)){
				CONNECTED_SERVERS.add(server);
			}
			Toast.makeText(this, "Connected ! ", Toast.LENGTH_SHORT).show();
			break;
		case COMMUNICATION_ERROR:
		case CONNECTION_ERROR:
			Toast.makeText(this, "Disconnected ! ", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		updateDrawer();
		mDrawerAdapter.notifyDataSetChanged();
	}

	
	
	//Public methods
	
	/**
	 * Disconnects the application from the server s and prevents drawer from showing it as connected
	 * @param s Server to disconnect
	 */
	public void disconnect(Server s){
		CONNECTION_MANAGER.removeServer(s);
		CONNECTED_SERVERS.remove(s);
		updateDrawer();
		mDrawerAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Connects the application to the server s
	 * @param s Server to connect
	 */
	public void connect(Server s){
		if(CONNECTION_MANAGER.getClient(s) == null){
			CONNECTION_MANAGER.addServer(s);
		}
	}
	
	
	
	//private methods and class
	
	
	private void updateDrawer() {
		mDrawerListItems.clear();
		String resTitles[] = getResources().getStringArray(R.array.drawer_list_array);
		for(int i = 0; i<resTitles.length; i++){
        	mDrawerListItems.add(resTitles[i]);
        }
		for(int i=0; i<CONNECTED_SERVERS.size(); i++){
			mDrawerListItems.add(i+1, CONNECTED_SERVERS.get(i).getName());
		}
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		FragmentManager fragmentManager;
		mCurTitle = mDrawerListItems.get(position);
		switch (position){
		case 0:
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			Fragment connexionFragment = new ConnectionFragment();
			fragmentManager = getFragmentManager();
		    fragmentManager.beginTransaction()
		                   .replace(R.id.content_frame, connexionFragment)
		                   .commit();
			break;
		default:
			if(position == mDrawerListItems.size()-1){
				Fragment aboutFragment = new AboutFragment();
				fragmentManager = getFragmentManager();
			    fragmentManager.beginTransaction()
			                   .replace(R.id.content_frame, aboutFragment)
			                   .commit();
			}else{
				if(CONNECTED_SERVERS.isEmpty()){
					Toast.makeText(this, "No server connected", Toast.LENGTH_SHORT).show();
				}else{
					mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
					Fragment cameraFragment = new CameraFragment();
					Bundle b = new Bundle();
					b.putLong("ServerId", CONNECTED_SERVERS.get(position-1).getId());
					cameraFragment.setArguments(b);
					fragmentManager = getFragmentManager();
				    fragmentManager.beginTransaction()
				                   .replace(R.id.content_frame, cameraFragment)
				                   .commit();
				}
			}
		}

	    mDrawerList.setItemChecked(position, true);
	    setTitle(mDrawerListItems.get(position));
	    
	    mDrawerLayout.closeDrawer(mDrawerList);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}

}

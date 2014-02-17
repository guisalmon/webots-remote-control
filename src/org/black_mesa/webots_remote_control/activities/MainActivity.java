package org.black_mesa.webots_remote_control.activities;

import org.black_mesa.webots_remote_control.R;
import org.black_mesa.webots_remote_control.R.array;
import org.black_mesa.webots_remote_control.R.id;
import org.black_mesa.webots_remote_control.R.layout;
import org.black_mesa.webots_remote_control.R.menu;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	private String[] mDrawerListItems;
    private ListView mDrawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
        mDrawerListItems = getResources().getStringArray(R.array.drawer_list_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerListItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		switch (position){
		case 0:
			break;
		case 1:
			break;
		case 2:
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
	}

	@Override
	public void setTitle(CharSequence title) {
	    getActionBar().setTitle(title);
	}

}

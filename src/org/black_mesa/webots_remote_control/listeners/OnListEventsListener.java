package org.black_mesa.webots_remote_control.listeners;

import org.black_mesa.webots_remote_control.classes.Server;

public interface OnListEventsListener {
	/**
	 * Is called when an item from the listView is checked or unchecked
	 */
	public void onCheckChanged();
	
	/**
	 * Is called when an item from the listView is clicked
	 */
	public void onItemClicked();
	
	/**
	 * Is called when an item from the listView is long clicked
	 */
	public void onItemLongClicked();
	
	/**
	 * Is called when the launch/stop button is clicked
	 * @param position is the index of the item which was launched/stopped in the listView
	 */
	public void onItemLaunchListener(Server s);
}
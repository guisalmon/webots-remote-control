package org.black_mesa.webots_remote_control.listeners;

import org.black_mesa.webots_remote_control.database.Server;

public interface OnListEventsListener {
	/**
	 * Is called when an item from the listView is checked or unchecked.
	 */
	void onCheckChanged();

	/**
	 * Is called when an item from the listView is clicked.
	 */
	void onItemClicked();

	/**
	 * Is called when an item from the listView is long clicked.
	 */
	void onItemLongClicked();

	/**
	 * Is called when the launch/stop button is clicked.
	 * 
	 * @param position
	 *            The index of the item which was launched/stopped in the listView.
	 */
	void onItemLaunchListener(Server s);
}
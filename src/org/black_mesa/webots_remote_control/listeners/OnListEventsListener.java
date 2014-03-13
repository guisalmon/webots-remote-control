package org.black_mesa.webots_remote_control.listeners;

public interface OnListEventsListener {
	/**
	 * Is called when an item from the listView is checked or unchecked
	 * @param isChecked is true if the item is checked, false otherwise
	 * @param position is the index of the item whose state changed in the listView
	 */
	public void onCheckChanged(boolean isChecked, int position);
	
	/**
	 * Is called when an item from the listView is clicked
	 * @param position is the index of the item which was clicked in the listView
	 */
	public void onItemClicked(int position);
	
	/**
	 * Is called when an item from the listView is long clicked
	 * @param position is the index of the item which was long clicked in the listView
	 */
	public void onItemLongClicked(int position);
	
	/**
	 * Is called when the launch/stop button is clicked
	 * @param position is the index of the item which was launched/stopped in the listView
	 */
	public void onItemLaunchListener(int position);
}

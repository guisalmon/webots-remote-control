package org.black_mesa.webots_remote_control.listeners;

public interface OnListEventsListener {
	public void onCheckChanged(boolean isChecked, int position);
	public void onItemClicked(int position);
	public void onItemLongClicked(int position);
}

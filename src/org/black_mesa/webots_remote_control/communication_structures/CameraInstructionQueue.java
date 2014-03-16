package org.black_mesa.webots_remote_control.communication_structures;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Stores a queue of actions to be sent to a server. The actions are then
 * executed on the server side.
 * 
 * @author Ilja Kroonen
 * 
 */
public final class CameraInstructionQueue extends CommunicationStructure {
	private static final long serialVersionUID = 228351533118850327L;
	private Queue<CameraInstruction> mQueue;

	private CameraInstructionQueue(final int id, final CameraInstructionQueue queue) {
		super(id);
		if (queue == null) {
			mQueue = new LinkedList<CameraInstruction>();
		} else {
			mQueue = new LinkedList<CameraInstruction>(queue.mQueue);
		}
	}

	/**
	 * Adds an instruction to this InstructionQueue.
	 * 
	 * @param i
	 *            Instruction that will be added.
	 */
	public void add(final CameraInstruction i) {
		mQueue.add(i);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommunicationStructure board(final CommunicationStructure previous) {
		CameraInstructionQueue castedPrevious = (CameraInstructionQueue) previous;
		CameraInstructionQueue newQueue = new CameraInstructionQueue(getId(), castedPrevious);

		while (!mQueue.isEmpty()) {
			newQueue.mQueue.add(mQueue.poll());
		}

		return newQueue;
	}

	@Override
	public boolean checkIntegrity() {
		if (mQueue != null && mQueue.isEmpty()) {
			return true;
		}
		return false;
	}
}

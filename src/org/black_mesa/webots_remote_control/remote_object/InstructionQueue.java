package org.black_mesa.webots_remote_control.remote_object;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Stores a queue of actions to be sent to a server. The actions are then
 * executed on the server side.
 * 
 * @author Ilja Kroonen
 * 
 */
public class InstructionQueue extends RemoteObject {
	private static final long serialVersionUID = 228351533118850327L;
	private Queue<Instruction> mQueue;

	/**
	 * Instantiates an InstructionQueue.
	 * 
	 * @param id
	 *            Unique identifier for this RemoteObject.
	 */
	public InstructionQueue(final int id) {
		super(id);
		mQueue = new LinkedList<Instruction>();
	}

	private InstructionQueue(final int id, final InstructionQueue queue) {
		super(id);
		if (queue == null) {
			this.mQueue = new LinkedList<Instruction>();
		} else {
			this.mQueue = new LinkedList<Instruction>(queue.mQueue);
		}
	}

	/**
	 * Adds an instruction to this InstructionQueue.
	 * 
	 * @param i
	 *            Instruction that will be added.
	 */
	public final void add(final Instruction i) {
		mQueue.add(i);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final RemoteObject board(final RemoteObject previous) {
		InstructionQueue castedPrevious = (InstructionQueue) previous;
		InstructionQueue newQueue = new InstructionQueue(getId(), castedPrevious);

		while (!mQueue.isEmpty()) {
			newQueue.mQueue.add(mQueue.poll());
		}

		return newQueue;
	}
}
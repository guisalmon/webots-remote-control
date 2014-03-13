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
	private Queue<Instruction> queue;

	/**
	 * Instantiates an InstructionQueue.
	 * 
	 * @param id
	 *            Unique identifier for this RemoteObject.
	 */
	public InstructionQueue(int id) {
		super(id);
		queue = new LinkedList<Instruction>();
	}

	private InstructionQueue(int id, InstructionQueue queue) {
		super(id);
		if (queue == null) {
			this.queue = new LinkedList<Instruction>();
		} else {
			this.queue = new LinkedList<Instruction>(queue.queue);
		}
	}

	/**
	 * Adds an instruction to this InstructionQueue.
	 * 
	 * @param i
	 *            Instruction that will be added.
	 */
	public void add(Instruction i) {
		queue.add(i);
	}

	/**
	 * Moves the instructions in the queue to the one in the boarding table.
	 */
	@Override
	public RemoteObject board(RemoteObject previous) {
		InstructionQueue castedPrevious = (InstructionQueue) previous;
		InstructionQueue newQueue = new InstructionQueue(getId(), castedPrevious);

		while (!queue.isEmpty()) {
			newQueue.queue.add(queue.poll());
		}

		return newQueue;
	}
}
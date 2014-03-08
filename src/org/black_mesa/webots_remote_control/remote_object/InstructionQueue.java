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
	private Queue<Instruction> queue = new LinkedList<Instruction>();

	/**
	 * Instantiates an InstructionQueue.
	 * 
	 * @param id
	 *            Unique identifier for this RemoteObject.
	 */
	public InstructionQueue(int id) {
		super(id);
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

	private void move(InstructionQueue queue) {
		while (!queue.queue.isEmpty()) {
			this.queue.add(queue.queue.poll());
		}
	}

	/**
	 * Moves the instructions in the queue to the one in the boarding table.
	 */
	@Override
	public RemoteObject board(RemoteObject previous) {
		if (previous == null) {
			previous = new InstructionQueue(getId());
		}
		((InstructionQueue) previous).move(this);
		return previous;
	}
}
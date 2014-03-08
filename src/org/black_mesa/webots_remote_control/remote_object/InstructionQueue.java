package org.black_mesa.webots_remote_control.remote_object;

import java.util.LinkedList;
import java.util.Queue;

public class InstructionQueue extends RemoteObject {
	private static final long serialVersionUID = 228351533118850327L;
	private Queue<Instruction> queue = new LinkedList<Instruction>();
	
	public InstructionQueue(int id) {
		super(id);
	}

	public void add(Instruction i) {
		queue.add(i);
	}

	private void add(InstructionQueue queue) {
		while (!queue.queue.isEmpty()) {
			this.queue.add(queue.queue.poll());
		}
	}

	@Override
	public RemoteObject board(RemoteObject previous) {
		if (previous == null) {
			previous = new InstructionQueue(getId());
		}
		((InstructionQueue) previous).add(this);
		queue.clear();
		return previous;
	}
}
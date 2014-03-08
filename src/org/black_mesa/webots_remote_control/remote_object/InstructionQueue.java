package org.black_mesa.webots_remote_control.remote_object;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;


public class InstructionQueue implements Serializable, RemoteObject {
	private static final long serialVersionUID = 228351533118850327L;
	private final int id;
	private Queue<Instruction> queue;

	public InstructionQueue(int id) {
		this.id = id;
		this.queue = new LinkedList<Instruction>();
	}

	private InstructionQueue(int id, Queue<Instruction> queue) {
		this.id = id;
		this.queue = queue;
	}

	public int getId() {
		return id;
	}

	public void add(Instruction i) {
		queue.add(i);
	}

	@Override
	public RemoteObject board() {
		InstructionQueue ret = new InstructionQueue(id, queue);
		queue = new LinkedList<Instruction>();
		return ret;
	}
}
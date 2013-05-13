package mango;

import java.io.DataOutputStream;
import java.io.IOException;

public enum Instruction {

	PRINT, PAUSE, PUT_DATA, REMOVE_DATA;

	public static Instruction forOrdinal(int ordinal) {
		if (ordinal < 0 || ordinal >= values().length) {
			throw new IllegalArgumentException("Ordinal does not exist");
		}
		return values()[ordinal];
	}

	public void write(DataOutputStream output) throws IOException {
		output.write(ordinal());
	}

	public static void write(Instruction instruction, DataOutputStream output)
			throws IOException {
		instruction.write(output);
	}

}
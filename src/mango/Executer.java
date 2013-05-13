package mango;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public final class Executer {

	private static final DecimalFormat numFormat = new DecimalFormat(
			"###.######");

	public static void main(String[] cmdArgs) {
		if (cmdArgs.length < 1) {
			System.err.println("You need to specify the file to execute!");
			return;
		}

		final String file = cmdArgs[0];

		if (!file.endsWith(".cheek")) {
			System.err.println("\"" + file + "\" is not a valid Cheek file!");
			return;
		}

		try (final DataInputStream input = new DataInputStream(
				new FileInputStream(file))) {
			final Map<String, Object> data = new HashMap<>();

			int operation = -1;
			while ((operation = input.read()) != -1) {
				switch (operation) {
				case 0: // print
					int notification = input.read();
					// System.out.println(notification);
					String output = input.readUTF();
					switch (notification) {
					case 1: // data lookup
						Object result = data.get(output);
						if (result instanceof Double)
							output = numFormat.format(result);
						else
							output = result.toString();
						break;
					}

					switch (input.read()) {
					case 0: // println
						System.out.println(output);
						break;
					case 1: // print
						System.out.print(output);
						break;
					}
					break;
				case 1: // pause
					try {
						Thread.sleep(input.readInt());
					} catch (InterruptedException e) {
						System.err.println("The thread was interrupted!");
					}
					break;
				case 2: // put data
					VariableType variableType = VariableType.forOrdinal(input
							.read());
					if (variableType == null) { // serious error if this
						// happens, bad compilation
						System.err
								.println("Invalid variable type was written!");
						return;
					}

					String name = input.readUTF();

					switch (variableType) {
					case NUM:
						double d = input.readDouble();
						data.put(name, d);
						break;
					case SWITCH:
						data.put(name, input.readBoolean());
						break;
					case TEXT:
						data.put(name, input.readUTF());
						break;
					default:
						System.err
								.println("Could not process variable type: \""
										+ variableType + "\"");
						break;
					}
					break;
				case 3: // remove data
					data.remove(input.readUTF());
					break;
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("The file was not found: \"" + file + "\"");
		} catch (IOException e) {
			System.err.println("An I/O exception occured!");
			e.printStackTrace();
		}
	}

}
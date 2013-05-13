package mango;

import static mango.Utils.strBetween;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public final class Compiler {

	public static void main(String[] cmdArgs) throws FileNotFoundException {
		if (cmdArgs.length < 1) {
			System.err.println("You need to specify the file to compile!");
			return;
		}

		final String file = cmdArgs[0];

		if (!file.endsWith(".mango")) {
			System.err.println("\"" + file
					+ "\" is not a valid Mango source file!");
			return;
		}

		try (final BufferedReader reader = new BufferedReader(new FileReader(
				file))) {
			final DataOutputStream output = new DataOutputStream(
					new FileOutputStream(file.replace(".mango", ".cheek")));

			String line;
			int lineNumber = 0;
			while ((line = reader.readLine()) != null) {
				lineNumber++;

				if (line.length() < 1 || line.startsWith("#")) {
					continue;
				} else if (line.startsWith("print")) {
					String[] args = line.split(" ");
					for (int i = 2; i < args.length; i++) {
						args[1] += " " + args[i];
					}

					Instruction.write(Instruction.PRINT, output);

					String text = strBetween(args[1]);
					if (text != null) {
						output.write(0); // regular notification
					} else {
						output.write(1); // lookup data notification
						text = args[1];
					}
					output.writeUTF(text);

					output.write(line.startsWith("println") ? 0 : 1);
				} else if (line.startsWith("pause")) {
					String[] args = line.split(" ");

					Instruction.write(Instruction.PAUSE, output);
					try {
						output.writeInt(Integer.parseInt(args[1]));
					} catch (NumberFormatException e) {
						throw new Error("Invalid integer specified!", file,
								lineNumber, args[1]);
					}
				} else if (line.startsWith("def")) {
					String[] args = line.split(" ");

					if (args.length != 4) {
						throw new Error("Invalid variable definition!", file,
								lineNumber, line);
					}

					Instruction.write(Instruction.PUT_DATA, output);

					try {
						VariableType variableType = VariableType
								.valueOf(args[1].toUpperCase());
						output.write(variableType.ordinal());

						// the name of the variable
						output.writeUTF(args[2]);

						switch (variableType) {
						case NUM:
							output.writeDouble(Double.parseDouble(args[3]));
							break;
						case SWITCH:
							output.writeBoolean(Boolean.parseBoolean(args[3]));
							break;
						case TEXT:
							output.writeUTF(args[3]);
							break;
						default:
							throw new Error(
									"The specified variable type is unhandled!",
									file, lineNumber, args[1]);
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						throw new Error("Invalid variable type!", file,
								lineNumber, args[1]);
					}
				} else if (line.startsWith("rem")) { // allows garbage
					// collection
					String[] args = line.split(" ");

					if (args.length != 2) {
						throw new Error("Invalid variable removal!", file,
								lineNumber, line);
					}

					Instruction.write(Instruction.REMOVE_DATA, output);

					// the name of the variable
					output.writeUTF(args[1]);
				} else {
					throw new Error("No matching instruction!", file,
							lineNumber, line);
				}
			}

			output.close();
		} catch (FileNotFoundException e) {
			System.err.println("The file was not found: \"" + file + "\"");
		} catch (IOException e) {
			System.err.println("An I/O exception occured!");
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
			return; // do not tell the user we successfully compiled, for we
			// didn't
		}

		System.out.println("\"" + file + "\" has been successfully compiled!");
	}

}
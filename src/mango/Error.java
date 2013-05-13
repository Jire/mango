package mango;

public final class Error extends Exception {
	private static final long serialVersionUID = -777484486048179732L;

	public Error(String message, String file, int lineNumber, String target) {
		super("Error: " + message + "\n\tat " + file + "(\"" + target + "\":" + lineNumber + ")");
	}

	@Override
	public void printStackTrace() {
		System.err.println(getMessage());
	}

}
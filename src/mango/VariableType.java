package mango;

public enum VariableType {

	NUM(Double.class),
	SWITCH(Boolean.class),
	TEXT(String.class);

	private final Class<?> backing;

	VariableType(Class<?> backing) {
		this.backing = backing;
	}

	public Class<?> getBacking() {
		return backing;
	}

	public static VariableType forBacking(Class<?> backing) {
		for (VariableType dataType : values()) {
			if (backing.equals(dataType.getBacking())) {
				return dataType;
			}
		}

		return null;
	}

	public static VariableType forOrdinal(int ordinal) {
		if (ordinal < 0 || ordinal >= values().length) {
			return null;
		}
		return values()[ordinal];
	}

}
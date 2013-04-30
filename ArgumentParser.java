import java.util.HashMap;

public class ArgumentParser {
	private String[] args;
	private HashMap<String, String> flagMap;
	
	public ArgumentParser(String[] args) {
		flagMap = new HashMap<String, String>();
		this.args = args;
		if (args.length == 0) {
			System.out.println("No arguments entered");
			System.exit(0);

		}
		// adds the arguments to the hash map when the argument parser gets
		// initialized.
		for (int i = 0; i < args.length; i++) {
			addToMap(args[i]);
		}
	}
	
	// adds the flag given and the associated value into a hash map.
	// if the flag has no value associated with it, a blank is set as its value.
	public void addToMap(String flag) {
		if (flag.startsWith("-")) {
			if (hasFlag(flag)) {
				if (hasValue(flag)) {
					this.flagMap.put(flag, getValue(flag));
				} else {
					this.flagMap.put(flag, "");
				}
			}
		}
	}

	// check if the flag given as a parameter can be found as an argument.
	public boolean hasFlag(String flag) {
		for (int i = 0; i < args.length; i++) {
			if (flag.equals(args[i])) {
				return true;
			}
		}
		return false;
	}

	// checks if there is a value associated with the given flag.
	public boolean hasValue(String flag) {
		for (int i = 0; i < args.length; i++) {
			if (flag.equals(args[i])) {
				if (i < args.length - 1) {
					if (!args[i + 1].startsWith("-")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// gets the value for the given flag
	public String getValue(String flag) {
		String value = "";
		if (hasValue(flag)) {
			for (int i = 0; i < args.length; i++) {
				if (flag.equals(args[i])) {
					value = args[i + 1];
				}
			}

		}
		return value;
	}

	// counts the number of flags given.
	public int numFlags() {
		int flagCount = 0;
		for (String s : this.args) {
			if (s.startsWith("-")) {
				flagCount++;
			}
		}

		return flagCount;
	}

	// counts the number of arguments given.
	public int numArguments() {
		int argCount = 0;
		for (String s : this.args) {
			if (s.startsWith("-")) {
				if (hasValue(s)) {
					argCount++;
				}
			}
		}

		return argCount;
	}

}

package application;

public class ValidateHandle {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	// How to check if a String is numeric in Java

	public static boolean isNumeric(String str) {
		try {
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static boolean isNumericInteger(String str) {
		try {
			@SuppressWarnings("unused")
			int d = Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static boolean isNumericFloat(String str) {
		try {
			@SuppressWarnings("unused")
			float d = Float.parseFloat(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static boolean isValidEmail(String s) {
		return s.matches(EMAIL_PATTERN);// returns true if input and regex
										// matches
		// otherwise false;
	}

	public boolean isValidName(String s) {
		String regex = "[A-Za-z\\s]+";
		return s.matches(regex);// returns true if input and regex matches
								// otherwise false;
	}

	public static boolean validatePhoneNumber(String phoneNo) {
		String pattern = "\\d{11}|(?:\\d{3}-){2}\\d{4}|\\d{3}-?\\d{4}-?\\d{4}";
		if (phoneNo.matches(pattern))
			return true;
		else
			return false;

	}
}

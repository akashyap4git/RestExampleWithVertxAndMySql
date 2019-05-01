package com.ak4.common.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains list of error messages sent to the client.
 * 
 * @author srir
 *
 */
public class Errors {

	private List<Error> errors;

	public List<Error> getErrors() {

		if (errors == null) {
			errors = new ArrayList<Error>();
		}
		return errors;
	}

	/**
	 * Error class holds the error code and message.
	 * 
	 * @author srir
	 *
	 */
	public static class Error {

		private int code;
		private String message;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}

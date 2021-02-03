package org.example;

public class ResultContainer {

	private String result;
	private boolean isSuccessful;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}

	public void setSuccessful(boolean successful) {
		isSuccessful = successful;
	}

	public ResultContainer(String result, boolean isSuccessful) {
		this.result = result;
		this.isSuccessful = isSuccessful;
	}
}

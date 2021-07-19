package com.example.demo;

import java.io.Serializable;

import org.springframework.http.HttpStatus;


public class ResponseDto implements Serializable {

	/**
	 * long serialVersionUID = 2623767476103419992L
	 */
	private static final long serialVersionUID = 2623767476103419992L;

	private String msg;
	private Object output;
	
	private HttpStatus httpStatus;
	
	
	
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}


	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}


	public String getMsg() {
		return msg;
	}


	public void setMsg(String msg) {
		this.msg = msg;
	}


	


	public Object getOutput() {
		return output;
	}


	public void setOutput(Object output) {
		this.output = output;
	}


	public ResponseDto() {
		super();
	}
}

package com.fns.grivet.model;

public enum Op {
	CREATE, UPDATE, DELETE;
	
	public static Op fromValue(String value) {
	    return Enum.valueOf(Op.class, value);
	}
}

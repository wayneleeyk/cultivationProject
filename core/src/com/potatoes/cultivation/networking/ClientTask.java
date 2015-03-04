package com.potatoes.cultivation.networking;

import java.lang.reflect.Method;

public class ClientTask {
	private Method toExecute;
	private Object[] myParams;
	
	public ClientTask(Method m, Object... params) {
		toExecute = m;
		myParams = params;
	}
	public Method getMethodToExecute() {
		return toExecute;
	}
	
	public Object[] getParameters() {
		return myParams;
	}
}
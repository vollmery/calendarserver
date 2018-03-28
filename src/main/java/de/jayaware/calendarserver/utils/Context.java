package de.jayaware.calendarserver.utils;

import com.google.gson.Gson;

import lombok.Value;

@Value
public class Context {

	private final Gson gson = new Gson();

	public static Context INSTANCE = new Context();

	private Context() {

	}

	public static Context getInstance() {
		return INSTANCE;
	}
}

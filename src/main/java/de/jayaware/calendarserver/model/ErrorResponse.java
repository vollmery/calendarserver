package de.jayaware.calendarserver.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jayaware.calendarserver.utils.Context;
import lombok.Builder;
import lombok.Value;
import spark.Response;

@Value
@Builder
public class ErrorResponse {

	private static final Logger LOG = LoggerFactory.getLogger(ErrorResponse.class);

	private int code;
	private String message;

	public String send(Response res) {
		res.status(getCode());
		String message = Context.getInstance().getGson().toJson(this);
		LOG.error(message);
		return message;
	}

}

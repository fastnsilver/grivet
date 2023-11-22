package com.fns.grivet.service;

import org.json.JSONObject;
import org.springframework.messaging.Message;

public interface Ingester {

	void ingest(Message<JSONObject> message);

}

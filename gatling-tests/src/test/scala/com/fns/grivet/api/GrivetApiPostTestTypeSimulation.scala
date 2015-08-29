package com.fns.grivet.api

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class GrivetApiPostTestTypeSimulation extends GrivetSimulationBase {

  /*
  val userSuppliedEndpointType = if (System.getProperty("endpoint.type") == null) "async" else System.getProperty("endpoint.type").toLowerCase
  val userSuppliedEnv = System.getProperty("env")
  val env = if (userSuppliedEnv != null && userSuppliedEnv.equalsIgnoreCase("test")) "us-west-2.test" else "us-east-1.stress"
  val endPointType = if (userSuppliedEndpointType.equalsIgnoreCase("sync")) "lodginguserinteraction" else "lodginguserinteractionasyncwrite"
  val url = "http://%s-service.%s.expedia.com/luis/%s/events/AmenityLog".format(endPointType, env, userSuppliedEndpointType)
  val headers = Map("Content-Type" -> "application/json", "Client-Id" -> "TestClient")
  val amenityLogPayloadTemplate = """{"hotelId": %d, "changeId": "%s", "requestDate": "%d", "requesterSystem": "EPC", "requester": "Lammy", "requesterType": "AMENITY", "objectId": "12345689112", "status": "pending", "changeType": "update room type", "userAction": "add", "details": "LUIS perf test", "changeDescription": "update the amenity fee to 100 from 50"}"""
  val feeder = Iterator.continually(Map("payload" -> amenityLogPayloadTemplate.format(random.nextInt(Integer.MAX_VALUE), UUID.randomUUID.toString, (new Date()).getTime)))

  val httpConf = http
    .baseURL(url)
    .headers(headers)

  object PostAmenityLog {
    val post =
      exec(http("PostAmenityLog")
             .post("/")
             .body(StringBody("${payload}"))
             .asJSON)
  }

  val postAmenityLogScenario = scenario("Post_Amenity_Log")
    .feed(feeder)
    .exec(PostAmenityLog.post)

  setUp(
      postAmenityLogScenario.inject(
        rampUsers(getUsersCount()) over(getRampUpInSeconds() seconds),
        constantUsersPerSec(getUsersCount()) during(getDurationInSeconds() seconds))
      ).protocols(httpConf)
  */  
}
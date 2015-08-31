package com.fns.grivet.api

import io.gatling.core.Predef._
import io.gatling.http.Predef._


class GrivetApiGetTestTypeSimulation extends GrivetSimulationBase {

  /*
  val headers = Map("Content-Type" -> "application/json", "Client-Id" -> "TestClient")
  val luisSyncUrl = "http://lodginguserinteraction-service.us-west-2.test.expedia.com/luis/events/AmenityLog/hotelId/"
  val feeder = csv("./src/test/resources/hotelids/HotelIds.csv").random  //this setup goes through entries in the csv file randomly.

  val httpConf = http
    .baseURL(luisSyncUrl)
    .headers(headers)

  object GetAmenityLog {
    val get =
      exec(http("GetAmenityLog")
             .get("${hotelId}")
             .check(status.is(200))
             .check(regex("changeId").exists))
  }

  val getAmenityLogScenario = scenario("Get_Amenity_Log")
    .feed(feeder)
    .exec(GetAmenityLog.get)

  setUp(
      getAmenityLogScenario.inject(
        rampUsers(getUsersCount()) over(getUsersCount() seconds),
        constantUsersPerSec(getUsersCount()) during(getDurationInSeconds() seconds))
      ).protocols(httpConf)
  
  */
    
}
package main

import base.SimulationBase
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import java.time.format.DateTimeFormatter
import java.time.LocalDate

import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

// Assumes that Student has been registered
// Stores random Student payloads, one at a time 
class StoreStudentSimulation extends SimulationBase {

  val host = System.getProperty("host")
  val port = if (System.getProperty("port") == null) 80 else Integer.valueOf(System.getProperty("port"))
  val registerUrl = "http://%s:%d/type/register".format(host, port)
  val storeUrl = "http://%s:%d/type/store/Contact".format(host, port)
  val headers = Map("Content-Type" -> "application/json", "Client-Id" -> "TestClient")
  val payloadTemplate = """{"firstName": "%s", "lastName": "%s", "from": "%s", "dateOfBirth": "%s" }"""
  
  object DataGenerator {
    def randomString(length: Int) = {
      val r = new scala.util.Random
      val sb = new StringBuilder
      for (i <- 1 to length) {
        sb.append(r.nextPrintableChar)
      }
      sb.toString
    }
    
    // TODO Add method to generate random date b/w 1900 and 2015
    
  }
  
  object StoreStudent {
      val post =
              exec(http("storeStudent")
                      .post("/")
                      .body(StringBody("${payload}"))
                      .asJSON)
  }
    
  val storeFeeder = Iterator.continually(Map("payload" -> payloadTemplate.format(DataGenerator.randomString(10), DataGenerator.randomString(10), DataGenerator.randomString(15), DateTimeFormatter.ISO_DATE.format(LocalDate.now()))))

  val storeStudentScenario = scenario("Store_Student")
    .feed(storeFeeder)
    .exec(StoreStudent.post)
  
  val storeHttpConf = http
    .baseURL(storeUrl)
    .headers(headers)
    
  setUp(
      storeStudentScenario.inject(
        rampUsers(getUsersCount()) over(getRampUpInSeconds() seconds),
        constantUsersPerSec(getUsersCount()) during(getDurationInSeconds() seconds)
      ).protocols(storeHttpConf)
  )
}

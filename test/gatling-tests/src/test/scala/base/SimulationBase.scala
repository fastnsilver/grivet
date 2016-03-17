package base

import java.util.Random

import io.gatling.core.scenario.Simulation

import java.io._
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import scala.collection.mutable.StringBuilder

class SimulationBase extends Simulation {

  val random = new Random()
  val usersCount = System.getProperty("users.count")
  val rampUpInSeconds = System.getProperty("rampup.seconds")
  val durationInSeconds = System.getProperty("duration.seconds")

  def getUsersCount(): Int = if (usersCount != null) Integer.valueOf(usersCount).intValue() else 10
  
  def getRampUpInSeconds(): Int = if (rampUpInSeconds != null) Integer.valueOf(rampUpInSeconds).intValue() else 10

  def getDurationInSeconds(): Int = if (durationInSeconds != null) Integer.valueOf(durationInSeconds).intValue() else 60

  /**
   * Returns the text content from a REST URL. Returns a blank String if there
   * is a problem.
   */
  def get(url:String): String = {
    val httpClient = HttpClientBuilder.create().build()
    val httpResponse = httpClient.execute(new HttpGet(url))
    val entity = httpResponse.getEntity()
    var content = ""
    if (entity != null) {
      val inputStream = entity.getContent()
      content = scala.io.Source.fromInputStream(inputStream).getLines.mkString
      inputStream.close
    }
    httpClient.close()
    return content
  }
  
  
}

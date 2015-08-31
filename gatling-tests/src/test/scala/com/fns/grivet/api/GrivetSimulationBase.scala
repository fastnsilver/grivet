package com.fns.grivet.api

import java.util.Random

import io.gatling.core.scenario.Simulation

class GrivetSimulationBase extends Simulation {

  val random = new Random()
  val baseUrl = System.getProperty("base.url");
  val usersCount = System.getProperty("users.count")
  val rampUpInSeconds = System.getProperty("rampup.seconds")
  val durationInSeconds = System.getProperty("duration.seconds")

  def getUsersCount(): Int = if (usersCount != null) Integer.parseInt(usersCount) else 20
  
  def getRampUpInSeconds(): Int = if (rampUpInSeconds != null) Integer.parseInt(rampUpInSeconds) else getUsersCount()

  def getDurationInSeconds(): Int = if (durationInSeconds != null) Integer.parseInt(durationInSeconds) else 60

  def getBaseUrl(): String = if (baseUrl !=null) baseUrl else "http://localhost:8080";
}

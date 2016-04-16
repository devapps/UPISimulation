package uat

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class YesBankSimulation extends Simulation {

  before {
    println("Simulation is about to start!")
  }

  private val httpProtocol = http.baseURL("https://upiuat.yesbank.in:8443/upi")

  //scenario
  val scnObc = scenario("YesBankScenario-atOnceUsers-500")
    .exec(
      BankTests.createYesBankRequest
    )

  // set up the scenario and threads (users) count:
  setUp(scnObc.inject(atOnceUsers(500)))
    .protocols(httpProtocol)

  after {
    println("Simulation is finished!")
  }
}

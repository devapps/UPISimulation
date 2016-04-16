package uat

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class OBCBankSimulation extends Simulation {

  before {
    println("Simulation is about to start!")
  }

  private val httpProtocol = http.baseURL("https://upi.obcindia.co.in/upi")

  //scenario
  val scnObc = scenario("OBCBankScenario-atOnceUsers-500")
    .exec(
      BankTests.createOBCBankRequest
    )

  // set up the scenario and threads (users) count:
  setUp(scnObc.inject(atOnceUsers(500)))
    .protocols(httpProtocol)

  after {
    println("Simulation is finished!")
  }
}

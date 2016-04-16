package local

import io.gatling.core.Predef._
import io.gatling.core.controller.inject.RampInjection
import io.gatling.http.Predef._
import uat.BankTests

import scala.concurrent.duration._

class LocalSimulation extends Simulation {

  before {
    println("Simulation is about to start!")
  }

  private val httpProtocol = http.baseURL("https://upi.obcindia.co.in/upi")

  //scenario
  val scnObc = scenario("OBCBankScenario")
    .exec(
      BankTests.createOBCBankRequest
    )

  // set up the scenario and threads (users) count:
  private val rampInjection: RampInjection = rampUsers(1) over (1 second)

  setUp(scnObc.inject(rampInjection))
    .protocols(httpProtocol)

  after {
    println("Simulation is finished!")
  }
}

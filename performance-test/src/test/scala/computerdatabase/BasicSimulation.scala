package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.CompositeByteArrayBody
import scala.concurrent.duration._
import scala.util.Random

class BasicSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:8090/rest/upi/1.0/") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml,application/json;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val headers = Map("Content-Type" -> """application/json""") // Note the headers specific to a given request

  private val fileBody: CompositeByteArrayBody = ELFileBody("fetchBalanceRequest.json")

  val scn = scenario("Fetch Balance") // A scenario is a chain of requests and pauses
    .exec(session => session.set("sequenceId", Random.nextInt(1000)))
    .exec(http("FetchBalance")
      .post("/fetchBalance")
      //.body(StringBody("""{ "uid": "1" }""")).asJSON)
      .body(fileBody).asJSON)
/*
      .resources(http("CallbackFetchBalance")
        .post("/callback/fetchBalance")
        .body(fileBody).asJSON))
*/
/*
    .exec(http("CallbackFetchBalance")
      .post("/callback/fetchBalance")
      .body(fileBody).asJSON)*/


  setUp(
    scn.inject(rampUsers(1) over(1 second))
    /*

    nothingFor(1 second), // 1
    atOnceUsers(10) // 2
    rampUsers(1) over(1 second) // 3

        constantUsersPerSec(20) during(15 seconds), // 4
    */
    //constantUsersPerSec(20) during(15 seconds) randomized // 5
    /*
        rampUsersPerSec(10) to(20) during(10 minutes), // 6
        rampUsersPerSec(10) to(20) during(10 minutes) randomized, // 7
        splitUsers(1000) into(rampUsers(10) over(10 seconds)) separatedBy(10 seconds), // 8
        splitUsers(1000) into(rampUsers(10) over(10 seconds)) separatedBy(atOnceUsers(30)), // 9
        heavisideUsers(1000) over(20 seconds) // 10
    */
  )
    .protocols(httpConf)
}

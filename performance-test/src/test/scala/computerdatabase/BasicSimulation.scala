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

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .exec(session => session.set("sequenceId", Random.nextInt(1000)))
    .exec(http("FetchBalance")
      .post("/fetchBalance")
      //.body(StringBody("""{ "uid": "1" }""")).asJSON)
      .body(fileBody).asJSON)
    .pause(2)

  setUp(scn.inject(atOnceUsers(1000)).protocols(httpConf))
}

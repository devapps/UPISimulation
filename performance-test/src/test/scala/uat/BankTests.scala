package uat

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.CompositeByteArrayBody

import scala.util.Random

object BankTests {
   private def randomString = Random.alphanumeric.take(10).mkString
   private val fileBody: CompositeByteArrayBody = ELFileBody("fetchBalanceRequest.json")
   private def newTask =  s"""{
     "txnId": "${randomString}",
     "refId": "${randomString}"
   }"""

   val createOBCBankRequest = exec(
     http("ListPSPRequest") // let's give proper names, as they are displayed in the reports
       .post("/listPSPService")
       .header("Content-Type", "application/json")
       .header("UserCred", "Basic bWdzdXBpOmFkbWluQDEyMw==")
       //.body(fileBody).asJSON
       .body(StringBody(newTask)).asJSON
       .check(status is 200)
       .check(bodyString.exists)
   )

   val createYesBankRequest = exec(
     http("ListPSPRequest") // let's give proper names, as they are displayed in the reports
       .post("/listPSPService")
       .header("Content-Type", "application/json")
       .header("UserCred", "Basic bWdzdXBpOmFkbWluQDEyMw==")
       //.body(fileBody).asJSON
       .body(StringBody(newTask)).asJSON
       .check(status is 200)
       .check(bodyString.exists)
   )
 }

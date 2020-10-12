/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.breathingspaceifstub.controller.stateless

import scala.io.Source

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers.await
import uk.gov.hmrc.breathingspaceifstub.Header
import uk.gov.hmrc.breathingspaceifstub.model.CorrelationId
import uk.gov.hmrc.breathingspaceifstub.repository.PeriodsRepository
import uk.gov.hmrc.breathingspaceifstub.support.BaseISpec

class PeriodsControllerISpec extends BaseISpec {

  val periodsRepo = app.injector.instanceOf[PeriodsRepository]
  val periodsStore = periodsRepo.store

  implicit val correlationHeaderValue: CorrelationId = CorrelationId(Some(correlationId))

  "GET /NINO/:nino/periods" should {
    "return 200(OK) with a single period (full population) when the Nino 'BS000001A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000001A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("singleBsPeriodFullPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with a single period (partial population) when the Nino 'BS000002A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000002A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("singleBsPeriodPartialPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with multiple periods (all full population) when the Nino 'BS000003A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000003A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("multipleBsPeriodsFullPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with multiple periods (all partial population) when the Nino 'BS000004A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000004A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("multipleBsPeriodsPartialPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) with multiple periods (mixed population) when the Nino 'BS000005A' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000005A"))
      response.status shouldBe Status.OK
      response.body shouldBe getExpectedResponseBody("multipleBsPeriodsMixedPopulation.json")
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the Nino 'BS000400B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000400B"))
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 404(NOT_FOUND) when the Nino 'BS000404B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000404B"))
      response.status shouldBe Status.NOT_FOUND
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino 'BS000500B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000500B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino 'BS0005R0B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS0005R0B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 502(BAD_GATEWAY) when the Nino 'BS000502B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000502B"))
      response.status shouldBe Status.BAD_GATEWAY
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 503(SERVICE_UNAVAILABLE) when the Nino 'BS000503B' is sent" in {
      val response = makeGetRequest(getConnectionUrl("BS000503B"))
      response.status shouldBe Status.SERVICE_UNAVAILABLE
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino specifies a non-existing HTTP status code" in {
      val response = makeGetRequest(getConnectionUrl("BS000700B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 200(OK) when the Nino specified is unknown " in {
      withClue("MA000700A") {
        val response = makeGetRequest(getConnectionUrl("MA000700A"))
        response.status shouldBe Status.OK
        response.body shouldBe """{"periods":[]}"""
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("MA000200B") {
        val response = makeGetRequest(getConnectionUrl("MA000200A"))
        response.status shouldBe Status.OK
        response.body shouldBe """{"periods":[]}"""
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }

      withClue("AB000500D") {
        val response = makeGetRequest(getConnectionUrl("AB000500D"))
        response.status shouldBe Status.OK
        response.body shouldBe """{"periods":[]}"""
        response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
      }
    }
  }

  "POST /NINO/:nino/periods" should {

    "return 201(CREATED) with the periods sent when any accepted Nino value is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000400A"),
        """{"periods":[{"startDate":"2020-06-25","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"},{"startDate":"2020-06-22","endDate":"2020-08-22","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"}]}""")
      response.status shouldBe Status.CREATED
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the request is sent without json body" in {
      val response = await(wsClient
        .url(getConnectionUrl("BS000400A"))
        .withHttpHeaders(Header.CorrelationId -> correlationHeaderValue.value.get)
        .post("")
      )
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the request is sent with invalid json body" in {
      val response = makePostRequest(getConnectionUrl("BS000400A"), """{"notWhatWeAreExpecting":"certainlyNot"}""")
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the Nino 'BS000400B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000400B"))
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 409(CONFLICT) when the Nino 'BS000409B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000409B"))
      response.status shouldBe Status.CONFLICT
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 428(PRECONDITION_REQUIRED) when the Nino 'BS000428B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000428B"))
      response.status shouldBe Status.PRECONDITION_REQUIRED
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino 'BS000500B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000500B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 502(BAD_GATEWAY) when the Nino 'BS000502B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000502B"))
      response.status shouldBe Status.BAD_GATEWAY
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 503(SERVICE_UNAVAILABLE) when the Nino 'BS000503B' is sent" in {
      val response = makePostRequest(getConnectionUrl("BS000503B"))
      response.status shouldBe Status.SERVICE_UNAVAILABLE
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }
  }

  "PUT /NINO/:nino/periods" should {

    "return 200(OK) with the periods sent when any accepted Nino value is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000400A"),
        """{"periods":[{"periodID": "4043d4b5-1f2a-4d10-8878-ef1ce9d97b32", "startDate":"2020-06-25","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"},{"periodID": "6aed4f02-f652-4bef-af14-49c79e968c2e", "startDate":"2020-06-22","endDate":"2020-08-22","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"}]}""")
      response.status shouldBe Status.OK
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the request is sent without json body" in {
      val response = await(wsClient
        .url(getConnectionUrl("BS000400A"))
        .withHttpHeaders(Header.CorrelationId -> correlationHeaderValue.value.get)
        .put("")
      )
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the request is sent with invalid json body" in {
      val response = makePutRequest(getConnectionUrl("BS000400A"), """{"notWhatWeAreExpecting":"certainlyNot"}""")
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 400(BAD_REQUEST) when the Nino 'BS000400B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000400B"))
      response.status shouldBe Status.BAD_REQUEST
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 409(CONFLICT) when the Nino 'BS000409B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000409B"))
      response.status shouldBe Status.CONFLICT
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 428(PRECONDITION_REQUIRED) when the Nino 'BS000428B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000428B"))
      response.status shouldBe Status.PRECONDITION_REQUIRED
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 500(SERVER_ERROR) when the Nino 'BS000500B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000500B"))
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 502(BAD_GATEWAY) when the Nino 'BS000502B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000502B"))
      response.status shouldBe Status.BAD_GATEWAY
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }

    "return 503(SERVICE_UNAVAILABLE) when the Nino 'BS000503B' is sent" in {
      val response = makePutRequest(getConnectionUrl("BS000503B"))
      response.status shouldBe Status.SERVICE_UNAVAILABLE
      response.header(Header.CorrelationId) shouldBe correlationHeaderValue.value
    }
  }

  private def makePutRequest(connectionUrl: String, bodyContents: String = "{}")(implicit correlationId: CorrelationId) =
    await(wsClient.url(connectionUrl)
      .withHttpHeaders(Header.CorrelationId -> correlationId.value.get)
      .put(Json.parse(bodyContents)))

  private def makePostRequest(connectionUrl: String, bodyContents: String = "{}")(implicit correlationId: CorrelationId) =
    await(wsClient.url(connectionUrl)
      .withHttpHeaders(Header.CorrelationId -> correlationId.value.get)
      .post(Json.parse(bodyContents)))

  private def makeGetRequest(connectionUrl: String)(implicit correlationId: CorrelationId) =
    await(wsClient.url(connectionUrl)
      .withHttpHeaders(Header.CorrelationId -> correlationId.value.get)
      .get())

  private def getConnectionUrl(nino: String): String =
    s"${testServerAddress}${statelessLocalContext}/NINO/${nino}/periods"

  private def getExpectedResponseBody(filename: String): String = {
    val in = getClass.getResourceAsStream(s"/data/$filename")
    Source.fromInputStream(in).getLines.mkString.replaceAll("\\s", "")
  }
}

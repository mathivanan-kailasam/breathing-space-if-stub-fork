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

import java.util.UUID
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.Try

import play.api.Logging
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.mvc._
import play.mvc.Http.MimeTypes
import uk.gov.hmrc.breathingspaceifstub.controller.RequestValidation
import uk.gov.hmrc.breathingspaceifstub.repository.{DebtorRepository, PeriodsRepository}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton()
class PeriodsController @Inject()(
  cc: ControllerComponents,
  debtorRepo: DebtorRepository,
  periodsRepo: PeriodsRepository
)(implicit val ec: ExecutionContext)
    extends BackendController(cc)
    with RequestValidation {

  import PeriodsController._

  // TODO: Add logging of incoming request

  def get(nino: String): Action[AnyContent] = Action.async { implicit request =>
    composeResponse(nino.toUpperCase, getAcceptedNinoHandler)
  }

  def post(nino: String): Action[AnyContent] = Action.async { implicit request =>
    composeResponse(nino.toUpperCase, postAcceptedNinoHandler)
  }
}

object PeriodsController extends Results with Logging {
  def getAcceptedNinoHandler(nino: String, request: Request[AnyContent]): Future[Result] =
    nino match {
      case "BS000001A" => sendResponse(200, Some(jsonDataFromFile("singleBsPeriodFullPopulation.json")))
      case "BS000002A" => sendResponse(200, Some(jsonDataFromFile("singleBsPeriodPartialPopulation.json")))
      case "BS000003A" => sendResponse(200, Some(jsonDataFromFile("multipleBsPeriodsFullPopulation.json")))
      case "BS000004A" => sendResponse(200, Some(jsonDataFromFile("multipleBsPeriodsPartialPopulation.json")))
      case "BS000005A" => sendResponse(200, Some(jsonDataFromFile("multipleBsPeriodsMixedPopulation.json")))
      case _ => sendResponse(200, Some(Json.parse("""{"periods" :[]}""")))
    }

  def postAcceptedNinoHandler(nino: String, request: Request[AnyContent]): Future[Result] =
    request.body.asJson match {
      case None =>
        sendResponse(400)

      case Some(jsValue) =>
        logger.info(s"BS-STUB >> REQUEST: = POST ${request.uri} BODY: = ${jsValue.toString()}")
        transformRequestJsonToResponseJson(jsValue) match {
          case JsError(_) => sendResponse(400)
          case JsSuccess(jsObject, _) => sendResponse(201, Some(jsObject))
        }
    }

  def transformRequestJsonToResponseJson(jsValue: JsValue): JsResult[JsObject] = {
    val attrTransformer = (__ \ "periods").json.update {
      __.read[JsArray].map {
        case JsArray(values) =>
          val updatedValues = values.map { period =>
            val retainedFields = period.as[JsObject].fields.filter(_._1 != "pegaRequestTimestamp")
            JsObject(Seq(("periodId", JsString(UUID.randomUUID().toString))) ++ retainedFields)
          }

          JsArray(updatedValues)
      }
    }

    jsValue.transform(attrTransformer)
  }

  def composeResponse(nino: String, acceptedHandler: (String, Request[AnyContent]) => Future[Result])(
    implicit request: Request[AnyContent]
  ): Future[Result] =
    (nino.take(2), nino.lastOption) match {
      case ("BS", Some('B')) => // a bad nino
        sendResponse(extractErrorStatusFromNino(nino))

      case _ => acceptedHandler(nino, request)
    }

  def sendResponse(httpCode: Int, responseBody: Option[JsValue] = None): Future[Result] = {
    val body = responseBody.getOrElse(Json.obj("response" -> s"MDTP IF Stub returning '${httpCode}' as requested"))
    Future.successful(Status(httpCode)(body).as(MimeTypes.JSON))
  }

  def extractErrorStatusFromNino(nino: String): Int = {
    val requestedResponseCode = Try(nino.substring(5, 8).toInt).getOrElse(500)
    if (requestedResponseCode < 200 || requestedResponseCode > 599) 500 else requestedResponseCode
  }

  def jsonDataFromFile(filename: String): JsValue = {
    val in = getClass.getResourceAsStream(s"/data/$filename")
    val raw = Source.fromInputStream(in).getLines.mkString
    Json.parse(raw)
  }
}

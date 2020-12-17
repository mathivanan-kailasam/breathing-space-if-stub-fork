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

package uk.gov.hmrc

package object breathingspaceifstub {

  val Details = "details(nino,dateOfBirth)"
  val NameList = "nameList(name(firstForename,secondForename,surname))"
  val AddressList =
    "addressList(address(addressLine1,addressLine2,addressLine3,addressLine4,addressLine5,addressPostcode,countryCode))"
  val Indicators = "indicators(welshOutputInd)"

  val fields = s"$Details,$NameList,$AddressList,$Indicators"

  object Header {
    lazy val CorrelationId = "CorrelationId"
    lazy val OriginatorId = "OriginatorId"
    lazy val UserId = "UserId"
  }
}

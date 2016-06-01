/*
 * Copyright 2016 Dennis Vriend
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

package com.github.dnvriend.validation

import utest._
import scalaz._
import Scalaz._

object ValidationTest extends TestSuite {
  val tests = this{
    'success - {
      "".successNel ==>
        Success("")
    }
    'twoSuccess - {
      ("".successNel[String] *> "".successNel) ==>
        Success("")
    }
    'threeSuccess - {
      ("".successNel[String] *> "".successNel *> "".successNel) ==>
        Success("")
    }
    'oneFailure - {
      "failure a".failureNel ==>
        Failure(NonEmptyList("failure a"))
    }
    'twoFailures - {
      ("failure a".failureNel *> "failure b".failureNel) ==>
        Failure(NonEmptyList("failure a", "failure b"))
    }
    'threeFailures - {
      ("failure a".failureNel *> "failure b".failureNel *> "failure c".failureNel) ==>
        Failure(NonEmptyList("failure a", "failure b", "failure c"))
    }
    'failSuccessFail1 - {
      ("".successNel[String] *> "failure b".failureNel *> "failure c".failureNel) ==>
        Failure(NonEmptyList("failure b", "failure c"))
    }
    'failSuccessFail2 - {
      ("failure a".failureNel *> "".successNel *> "failure c".failureNel) ==>
        Failure(NonEmptyList("failure a", "failure c"))
    }
    'failSuccessFail3 - {
      ("failure a".failureNel *> "failure b".failureNel *> "".successNel) ==>
        Failure(NonEmptyList("failure a", "failure b"))
    }
    'validatePaymentAccount - {
      val paymentAccount = PaymentAccount(0, "")
      (PaymentAccountValidation.validateIban(paymentAccount) *>
        PaymentAccountValidation.validateId(paymentAccount)) ==>
        Failure(NonEmptyList("iban is empty", "payment account id is invalid, should be > 0"))
    }
    'validatePaymentAccountToCSV - {
      val paymentAccount = PaymentAccount(0, "")
      (PaymentAccountValidation.validateIban(paymentAccount) *> PaymentAccountValidation.validateId(paymentAccount) match {
        case Failure(xs) ⇒ xs.toList.mkString(",")
        case _           ⇒ ""
      }) ==> "iban is empty,payment account id is invalid, should be > 0"
    }
  }

  object PaymentAccountValidation {
    def validateIban(paymentAccount: PaymentAccount): ValidationNel[String, PaymentAccount] =
      if (Option(paymentAccount.iban).exists(_.isEmpty)) "iban is empty".failureNel else paymentAccount.successNel[String]

    def validateId(paymentAccount: PaymentAccount): ValidationNel[String, PaymentAccount] =
      if (paymentAccount.id <= 0) "payment account id is invalid, should be > 0".failureNel else paymentAccount.successNel[String]
  }

  case class PaymentAccount(id: Long, iban: String)
}
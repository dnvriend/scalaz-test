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
  // see: http://eed3si9n.com/learning-scalaz/Validation.html
  // see: http://www.47deg.com/blog/fp-for-the-average-joe-part-1-scalaz-validation
  // see: http://bytes.codes/2015/04/10/a-skeptics-guide-to-scalaz-part-1-disjunctions/
  // see: http://bytes.codes/2015/04/13/a-skeptics-guide-to-scalaz-gateway-drugs-part-2-options-with-disjunction/
  // see: learning scalaz: https://www.youtube.com/watch?v=jyMIvcUxOJ0
  // see: scalaz - the good parts: https://www.youtube.com/watch?v=jPdHQZnF56A
  // see: scalaz - for the rest of us: https://www.youtube.com/watch?v=kcfIH3GYXMI
  // see: scalaz - state monad: https://www.youtube.com/watch?v=Jg3Uv_YWJqI
  // see: A Skeptic's Look at Scalaz Gateway Drugs: https://www.youtube.com/watch?v=lF9OVD0_Boc

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
    'fromTryCatchNonFatal - {
      (Validation.fromTryCatchNonFatal[Int](1 / 0).leftMap(t ⇒ t.getMessage).toValidationNel[String, Int] *>
        Validation.fromTryCatchNonFatal[Int](1).leftMap(t ⇒ t.getMessage).toValidationNel[String, Int]) ==>
        Failure(NonEmptyList("/ by zero"))
    }
    'FoldingListOfFailures - {
      NonEmptyList("failure a".failureNel[String], "failure b".failureNel[String], "failure c".failureNel[String]).foldLeft(List.empty[String].successNel[String]) {
        case (acc, v) ⇒ (acc |@| v)(_ :+ _)
      } ==> Failure(NonEmptyList("failure a", "failure b", "failure c"))
    }
    'accumulatingValidation - {
      def validate(msg: String): ValidationNel[String, String] = s"failure $msg".failureNel[String]
      val listToValidate = NonEmptyList("a", "b", "c")
      listToValidate.traverseU(validate) ==>
        Failure(NonEmptyList("failure a", "failure b", "failure c"))
    }
    "fromTryCatchNonFatal in for comprehension must be a disjunction as Validation is not a Monad but an Applicative" - {
      def throwException: Int = throw new RuntimeException("test")
      (for {
        a ← Validation.fromTryCatchNonFatal(1).disjunction.leftMap(_.getMessage)
        b ← Validation.fromTryCatchNonFatal[Int](throwException).disjunction.leftMap(_.getMessage)
      } yield a + b).validationNel ==> Failure(NonEmptyList("test"))
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
    "lifting functions into an applicative function using '|@|'" - {
      // see: http://stackoverflow.com/questions/17711895/scalaz-validation-with-applicative-functor-not-working

      // scalaz's applicative functor symbol '|@|' provides a way to lift a function into an applicative functor

      // suppose we have the following results:
      val xs: ValidationNel[String, List[Int]] = "Error!".failureNel
      val ys: ValidationNel[String, List[Int]] = List(1, 2, 3).successNel
      val zs: ValidationNel[String, List[Int]] = List(4, 5).successNel

      // we can lift the concatenation function (_: List[Int]) ++ (_: List[Int]) into the Validation like this:
      val f: (List[Int], List[Int]) ⇒ List[Int] = (_: List[Int]) ++ (_: List[Int])
      (ys |@| zs)(f) ==>
        Success(List(1, 2, 3, 4, 5))

      // because scala can infert the types we can inline the function
      (ys |@| zs)(_ ++ _) ==>
        Success(List(1, 2, 3, 4, 5))

      (xs |@| ys)(_ ++ _) ==>
        Failure(NonEmptyList("Error!"))

      (xs |@| xs)(_ ++ _) ==>
        Failure(NonEmptyList("Error!", "Error!"))
    }
    "Appending validations using the '+++' operator" - {
      ("Success 1".successNel +++ "Success 2".successNel) ==>
        Success("Success 1Success 2")

      ("Success 1".successNel[String] |+| "Success 2".successNel[String]) ==>
        Success("Success 1Success 2")

      ("Success 1".successNel[String] |@| "Success 2".successNel[String])(_ |+| _) ==>
        Success("Success 1Success 2")

      ("Success 1".successNel[String] |+| "Success 2".successNel[String]) ==>
        ("Success 1".successNel[String] |@| "Success 2".successNel[String])(_ |+| _)
    }
  }

}

object PaymentAccountValidation {
  def validateIban(paymentAccount: PaymentAccount): ValidationNel[String, PaymentAccount] =
    if (Option(paymentAccount.iban).exists(_.isEmpty)) "iban is empty".failureNel else paymentAccount.successNel[String]

  def validateId(paymentAccount: PaymentAccount): ValidationNel[String, PaymentAccount] =
    if (paymentAccount.id <= 0) "payment account id is invalid, should be > 0".failureNel else paymentAccount.successNel[String]
}

case class PaymentAccount(id: Long, iban: String)

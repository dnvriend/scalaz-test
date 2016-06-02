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

package com.github.dnvriend.disjunction

import utest._

import scalaz._
import Scalaz._

object DisjunctionTest extends TestSuite {
  val tests = this{
    "Disjunction (a scalaz Either) has methods to create the right/left projection of a disjunction" - {
      "Success!".right ==>
        \/-("Success!")

      "Failure!".left ==>
        -\/("Failure!")
    }
    "The Disjunction Singletion also has the right and left methods" - {
      \/.right("Success!") ==>
        \/-("Success!")

      \/.left("Failure!") ==>
        -\/("Failure!")
    }
    "Fully Symbolic" - {
      \/-("Success!") ==>
        \/-("Success!")

      -\/("Failure!") ==>
        -\/("Failure!")
    }
    "Converting Option to Disjunction" - {
      None.toRightDisjunction("No object found") ==>
        -\/("No object found")

      None \/> "No object found" ==>
        -\/("No object found")

      Some("My hovercraft is full of eels") \/> "No object found" ==>
        \/-("My hovercraft is full of eels")

      Some("My hovercraft is full of eels").toRightDisjunction("No object found") ==>
        \/-("My hovercraft is full of eels")
    }
    "Converting Disjunction to Option" - {
      \/-(1).toOption ==>
        1.some

      -\/("Book not found").toOption ==>
        None
    }
    "Disjunctions are monads, they are right associated so they fail with the first left, and return only that error message" - {
      (for {
        numOfBooks ← Option(10) \/> "Book not in inventory"
        prize ← Option(22.00) \/> "Book not in prize definition"
      } yield numOfBooks * prize) ==> \/-(220.00)

      (for {
        numOfBooks ← none[Int] \/> "Book not in inventory"
        prize ← Option(22.00) \/> "Book not in prize definition"
      } yield numOfBooks * prize) ==> -\/("Book not in inventory")

      (for {
        numOfBooks ← Option(10) \/> "Book not in inventory"
        prize ← none[Double] \/> "Book not in prize definition"
      } yield numOfBooks * prize) ==> -\/("Book not in prize definition")
    }
    "Converting Disjunction to Validation" - {
      \/-("Success!").validationNel[String] ==>
        Success("Success!")

      -\/("Failure!").validationNel[String] ==>
        Failure(NonEmptyList("Failure!"))
    }
    "Converted Validations can be folded" - {
      NonEmptyList(
        \/-("Success 1").validationNel[String],
        \/-("Success 2").validationNel[String],
        -\/("Failure 1").validationNel[String],
        -\/("Failure 2").validationNel[String],
        \/-("Success 3").validationNel[String],
        \/-("Success 4").validationNel[String]
      ).foldLeft(List.empty[String].successNel[String]) {
        case (acc, v) ⇒ (acc |@| v)(_ :+ _)
      } ==> Failure(NonEmptyList("Failure 1", "Failure 2"))
    }
  }
}

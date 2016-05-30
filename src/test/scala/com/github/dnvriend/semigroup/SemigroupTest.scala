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

package com.github.dnvriend.semigroup

import utest._

import scalaz.Semigroup

object MySemigroups {
  implicit val appendSemigroup = new Semigroup[Int] {
    override def append(f1: Int, f2: ⇒ Int): Int = f1 + f2
  }

  implicit val productSemigroup = new Semigroup[Int] {
    override def append(f1: Int, f2: ⇒ Int): Int = f1 * f2
  }
}

object SemigroupTest extends TestSuite {
  val tests = this{
    "Semigroup with explicitly imported append semigroup" - {
      import MySemigroups.appendSemigroup
      Semigroup[Int].append(1, 2) ==>
        3
    }
    "Semigroup with explicitly imported product semigroup " - {
      import MySemigroups.productSemigroup
      Semigroup[Int].append(2, 2) ==>
        4
    }
    "Semigroup with default implementation for append using import scalaz.Scalaz._" - {
      import scalaz.std.AllInstances._
      Semigroup[Int].append(1, 2) ==>
        3
    }
    "Semigroup using scalaz syntax classes" - {
      import scalaz._
      import Scalaz._
      (1 mappend 2) ==>
        3

      (1 |+| 2) ==>
        3
    }
    "Appending lists" - {
      import scalaz._
      import Scalaz._
      (List(1, 2, 3) |+| List(4, 5, 6)) ==>
        List(1, 2, 3, 4, 5, 6)
    }
    "Appending maps" - {
      import scalaz._
      import Scalaz._
      (Map(1 → "x") |+| Map.empty) ==>
        Map(1 → "x")

      (Map(1 → "x") |+| Map(2 → "y")) ==>
        Map(1 → "x", 2 → "y")

      (Map(1 → "x") |+| Map(1 → "x")) ==>
        Map(1 → "xx")

      (Map(2 → "y") |+| Map(2 → "y")) ==>
        Map(2 → "yy")

      (Map(1 → "x", 2 → "y") |+| Map(1 → "x", 2 → "y")) ==>
        Map(1 → "xx", 2 → "yy")

      (Map(1 → List(1)) |+| Map(1 → List(2))) ==>
        Map(1 → List(1, 2))
    }
    "Appending Map + Option[A]" - {
      import scalaz._
      import Scalaz._
      Map(1 → "x") ++ Some(2 → "y") ==>
        Map(1 → "x", 2 → "y")
      Map(1 → "x") ++ Some(1 → "y") ==>
        Map(1 → "y")
      Map(1 → "x") ++ None ==>
        Map(1 → "x")
      (Map(1 → "x") |+| Map(1 → "x")) ==>
        Map(1 → "xx")
    }
  }
}
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

package com.github.dnvriend.list

import utest._
import scalaz._
import Scalaz._

object ListTest extends TestSuite {

  // legend
  // The symbol '@' is an ApplicativeBuilder
  // (f1 |@| f2 |@| ... |@| fn)((v1, v2, ... vn) => ...)
  // (f1 |@| f2 |@| ... |@| fn).tupled`
  // The ApplicativeBuilder has the '.tupled' method
  // that returns the result as a tuple, of course, in the
  // given Higher Kinded Type (HKT) like eg. a List

  val tests = this{
    'IndexedList - {
      List(3, 4, 5).indexed ==>
        List((0, 3), (1, 4), (2, 5))
    }
    'allPairs - {
      List(1, 2, 3).allPairs ==>
        List((1, 2), (2, 3), (1, 3))
      List(1, 2, 3, 4).allPairs ==>
        List((1, 2), (2, 3), (3, 4), (1, 3), (2, 4), (1, 4))
    }
    'adjacentPairs - {
      List(1, 2, 3).adjacentPairs ==>
        List((1, 2), (2, 3))
      List(1, 2, 3, 4).adjacentPairs ==>
        List((1, 2), (2, 3), (3, 4))
    }
    'intersperse - {
      List(1, 2, 3).intersperse(0) ==>
        List(1, 0, 2, 0, 3)
    }
    'powerset - {
      List(1, 2).powerset ==>
        List(List(1, 2), List(1), List(2), List())
      List(1, 2, 3).powerset ==>
        List(List(1, 2, 3), List(1, 2), List(1, 3), List(1), List(2, 3), List(2), List(3), List())
    }
    'initz - {
      List(1, 2).initz ==>
        List(List(), List(1), List(1, 2))
      List(1, 2, 3).tailz ==>
        List(List(1, 2, 3), List(2, 3), List(3), List())
    }
    'tailz - {
      List(1, 2).tailz ==>
        List(List(1, 2), List(2), List())
      List(1, 2, 3).tailz ==>
        List(List(1, 2, 3), List(2, 3), List(3), List())
    }
    'lookup - {
      // returns the first tuple's value that matches the key '1'
      List((1, 2), (2, 3), (1, 3)).lookup[Int, Int](1) ==>
        Some(2)
      // returns the first tuple's value that matches the key '2'
      List((1, 2), (2, 3), (1, 3)).lookup[Int, Int](2) ==>
        Some(3)
      // returns the first tuple's value that matches the key '3'
      List((1, 2), (2, 3), (1, 3)).lookup[Int, Int](3) ==>
        None
    }
    'toNel - {
      List(1, 2, 3).toNel ==>
        NonEmptyList(1, 2, 3).some
    }
    'tailOption - {
      List(1, 2, 3).tailOption ==>
        Some(List(2, 3))
    }
    'crossProductTupledApplicative - {
      // The result of |@| is an
      (List(1, 2) |@| List(3, 4)).tupled ==>
        List((1, 3), (1, 4), (2, 3), (2, 4))
      (List(1, 2) |@| List(3, 4)).tupled.map { case (l, r) ⇒ l.toString |+| r.toString } ==>
        List("13", "14", "23", "24")
    }
    'crossProductWithInts - {
      (List(1, 2) |@| List(3, 4))((v1, v2) ⇒ v1.toString |+| v2.toString) ==>
        List("13", "14", "23", "24")
    }
    'crossProductWithChars - {
      val charGen = ('A' to 'B').toList
      (charGen |@| charGen)((l, r) ⇒ l.toString |+| r.toString) ==>
        List("AA", "AB", "BA", "BB")
      (charGen |@| charGen)(_.toString |+| _.toString).mkString(",") ==>
        "AA,AB,BA,BB"
    }
    'crossProductOption - {
      (1.some |@| 2.some).tupled ==>
        (1, 2).some
    }
    'ThreeListsApplicative - {
      (List(1, 2) |@| List(3, 4) |@| List(5, 6)).tupled ==>
        List((1, 3, 5), (1, 3, 6), (1, 4, 5), (1, 4, 6), (2, 3, 5), (2, 3, 6), (2, 4, 5), (2, 4, 6))
      (List(1, 2) |@| List(3, 4) |@| List(5, 6))((v1, v2, v3) ⇒ v1 + v2 + v3).sum ==>
        84
      (List(1, 2) |@| List(3, 4) |@| List(5, 6))(_ |+| _ |+| _).sum ==>
        84
    }
  }
}

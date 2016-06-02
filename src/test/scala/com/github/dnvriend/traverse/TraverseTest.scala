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

package com.github.dnvriend.traverse

import utest._

import scalaz._
import Scalaz._
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

object TraverseTest extends TestSuite {
  import scala.concurrent.ExecutionContext.Implicits.global
  // see: https://github.com/scalaz/scalaz/blob/series/7.2.x/example/src/main/scala/scalaz/example/TraverseUsage.scala
  val tests = this{
    "sequence with somes" - {
      // An easy to understand first step in using the Traverse typeclass
      // is the sequence operation, which given a Traverse[F] and
      // Applicative[G] turns F[G[A]] into G[F[A]].  This is like "turning
      // the structure 'inside-out'":
      val list1: List[Option[Int]] = List(Some(1), Some(2), Some(3), Some(4))
      list1.sequence ==>
        Some(List(1, 2, 3, 4))

      List(1, 2, 3).some.sequence ==>
        List(Some(1), Some(2), Some(3))

      List(1, 2, 3).some.sequence ==>
        List(1.some, 2.some, 3.some)

      NonEmptyList(1, 2, 3).some.sequence ==>
        NonEmptyList(1.some, 2.some, 3.some)

      // Future.sequence, although it is not using the scalaz.Traverse type class, also turns
      // the List[Future] structure inside-out Future[List
      Await.result(Future.sequence(List(Future.successful(1), Future.successful(2), Future.successful(3))), 1.second) ==>
        List(1, 2, 3)
    }
    "sequence with none" - {
      val list2: List[Option[Int]] = List(Some(1), Some(2), None, Some(4))
      list2.sequence ==>
        None
    }
    "double sequence" - {
      val list1: List[Option[Int]] = List(Some(1), Some(2), Some(3), Some(4))
      list1.sequence ==>
        Some(List(1, 2, 3, 4))

      list1.sequence.sequence ==>
        list1
    }
    'traverse - {
      // A next step in using the Traverse TypeClass is the traverse
      // method. The traverse method maps function over a structure
      // through the effects of the inner applicative. You can think of
      // this method as combining a map with a sequence, so when you find
      // yourself calling fa.map(f).sequence, it can be replaced with just
      // fa.traverse(f):v
    }
  }
}

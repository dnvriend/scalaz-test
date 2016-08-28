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

import com.github.dnvriend.TestSpec

import scalaz._
import Scalaz._
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

class TraverseTest extends TestSpec {

  it should "sequence with somes" in {
    // An easy to understand first step in using the Traverse typeclass
    // is the sequence operation, which given a Traverse[F] and
    // Applicative[G] turns F[G[A]] into G[F[A]].  This is like "turning
    // the structure 'inside-out'":
    val list1: List[Option[Int]] = List(Some(1), Some(2), Some(3), Some(4))
    list1.sequence shouldBe
      Some(List(1, 2, 3, 4))

    List(1, 2, 3).some.sequence shouldBe
      List(Some(1), Some(2), Some(3))

    List(1, 2, 3).some.sequence shouldBe
      List(1.some, 2.some, 3.some)

    NonEmptyList(1, 2, 3).some.sequence shouldBe
      NonEmptyList(1.some, 2.some, 3.some)

    // Future.sequence, although it is not using the scalaz.Traverse type class, also turns
    // the List[Future] structure inside-out Future[List
    Await.result(Future.sequence(List(Future.successful(1), Future.successful(2), Future.successful(3))), 1.second) shouldBe
      List(1, 2, 3)
  }

  it should "sequence with none" in {
    val list2: List[Option[Int]] = List(Some(1), Some(2), None, Some(4))
    list2.sequence shouldBe
      None
  }

  it should "double sequence" in {
    val list1: List[Option[Int]] = List(Some(1), Some(2), Some(3), Some(4))
    list1.sequence shouldBe
      Some(List(1, 2, 3, 4))

    list1.sequence.sequence shouldBe list1
  }

  it should 'traverse in {
    // A next step in using the Traverse TypeClass is the traverse
    // method. The traverse method maps function over a structure
    // through the effects of the inner applicative. You can think of
    // this method as combining a map with a sequence, so when you find
    // yourself calling fa.map(f).sequence, it can be replaced with just
    // fa.traverse(f):v
  }

}

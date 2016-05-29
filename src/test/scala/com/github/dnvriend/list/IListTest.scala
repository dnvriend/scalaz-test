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

object IListTest extends TestSuite {
  // legend:
  // '==>' comes from uTest and is an 'Arrow Assert'.
  // Arrow assert is a nice syntax for asserting things are equal.
  // 'a ==> b' is a shorthand for assert(a == b)

  val tests = this{
    "IList is invariant, the following should not compile" - {
      // IList is invariant; these won't compile
      compileError(""""abc" :: IList(1, 2, 3)""")
      compileError("""IList.empty[String] ++ IList(1, 2, 3)""")
    }
    "Construct using elements" - {
      IList(1, 2, 3) ==>
        IList(1, 2, 3)
    }
    "Construct using cons" - {
      1 :: 2 :: 3 :: INil() ==>
        IList(1, 2, 3)
    }
    "Construct from list" - {
      IList.fromList(List(1, 2, 3)) ==>
        IList(1, 2, 3)
    }
    "Construct from Option" - {
      IList.fromOption(Some(2)) ==>
        IList(2)
    }
    "Construct from fill" - {
      IList.fill(5)(1) ==>
        IList(1, 1, 1, 1, 1)
    }
    "Empty list" - {
      // Empty IList
      INil[String]() ==>
        IList()
      IList.empty[String] ==>
        IList()
    }
    "Widen the list type to eg. 'Any'" - {
      "abc" :: IList(1, 2, 3).widen[Any] ==>
        IList("abc", 1, 2, 3)
    }
    "List operations" - {
      IList(1, 2).reverse ==>
        IList(2, 1)
      IList(1, 2).foldLeft(0)(_ + _) ==>
        3
    }
    "IList are total" - {
      IList(1, 2).tailOption ==>
        IList(2).some
      IList.empty[Int].tailOption ==>
        None
      IList(1, 2).reduceLeftOption(_ + _) ==>
        3.some
    }
    'Deconstructing - {
      IList(1, 2).uncons("empty", (h, t) ⇒ "head is %s and tail is %s".format(h, t)) ==>
        "head is 1 and tail is [2]"
    }
    "Deconstructing with match" - {
      (IList(1, 2) match {
        case INil()      ⇒ "empty"
        case ICons(h, t) ⇒ "head is %s and tail is %s".format(h, t)
      }) ==>
        "head is 1 and tail is [2]"
    }
    'Products - {
      (IList(1, 2) |@| IList(true, false)).tupled ==>
        IList((1, true), (1, false), (2, true), (2, false))
    }
    'unit - {
      33.point[IList] ==>
        IList(33)
    }
    'less - {
      (IList(1, 2, 3) < IList(1, 2, 4)) ==>
        true
    }
    'traverse - {
      IList(1, 2, 3).traverse(_.some) ==>
        IList(1, 2, 3).some
    }
    'conversion - {
      IList(1, 2).toList ==>
        List(1, 2)
      IList(1, 2).toVector ==>
        Vector(1, 2)
      IList(1, 2).toStream ==>
        Stream(1, 2)
    }
    'ephemeralStream - {
      val estr = IList(1, 2).toEphemeralStream
    }
    'toMap - {
      // '==>>' is an immutable map of key/value pairs implemented as a balanced binary tree
      val zmap: ==>>[Int, String] = IList(1, 2).map(n ⇒ (n, "x" * n)).toMap
      zmap.lookup(1) ==>
        "x".some
      zmap.lookup(2) ==>
        "xx".some
    }
  }
}

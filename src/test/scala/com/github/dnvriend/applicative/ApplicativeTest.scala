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

package com.github.dnvriend.applicative

import utest._

import scalaz._
import Scalaz._
import scala.language.postfixOps

object ApplicativeTest extends TestSuite {
  // from: http://eed3si9n.com/learning-scalaz/Applicative.html

  val tests = this{
    // let's build up to Applicatives
    'TheProblem - {
      val functionThatTakesTwoParameters = (_: Int) * (_: Int)
      // notice that we have a function with 2 parameters, we cannot use that let's curry it:
      val functionThatTakesTwoParametersCurried = functionThatTakesTwoParameters.curried
      // notice, we now have a Function with 1 parameter
      val functionsInContext = List(1, 2, 3) map functionThatTakesTwoParametersCurried
      // we now have functions in a context that we can apply eg. with the value '9':
      functionsInContext.map(_(9)) ==>
        List(9, 18, 27)
    }
    "point - from the Applicative type class" - {
      // The Applicative type class defines the methods 'pure/point'
      // 'pure' takes a value and puts it in a minimal context that still yields that value
      // The 'context' constructor has been absracted
      1.point[List] ==>
        List(1)

      1.point[Option] ==>
        Some(1)

      1.point[Option].map(_ + 2) ==>
        Some(3)

      1.point[List].map(_ + 2) ==>
        List(3)
    }
    "<*> - from the Applicative type class" - {
      // <*> is a beefed-up map.
      // map takes a function and a functor, and applies the function inside the functor value
      // <*> takes a functor that has a function in it, and another functor with a value in it,
      //    1. extracts that function from the first functor,
      //    2. and then maps it over the second one

      // legend:
      // <* returns the lhs
      (None <* None) ==>
        None

      (1.some <* None) ==>
        None

      (None <* 2.some) ==>
        None

      (1.some <* 2.some) ==>
        1.some

      // legend:
      // *> returns the rhs
      (None *> None) ==>
        None

      (1.some *> None) ==>
        None

      (None *> 2.some) ==>
        None

      (1.some *> 2.some) ==>
        2.some

      // legend:
      // <*> combining their results by function application
      (1.some <*> Option((_: Int) + 3)) ==>
        Some(4)

      val curriedSumFunction: Int ⇒ Int ⇒ Int = ((_: Int) + (_: Int)).curried
      val appliedCurriedSumFunction: Option[Int ⇒ Int] = 9.some <*> curriedSumFunction.some
      (3.some <*> appliedCurriedSumFunction) ==>
        12.some
    }
    'ApplicativeStyle - {
      // legend:
      // '^' extracts values from containers and apply them to a single function
      // It is a symbol for apply2 from the 'Apply' type class
      // This is useful for the 1 function case that we don't need to put inside a container

      // The type signature is the following:
      // (Option[Int], Option[Int]) ((A, B) => C)
      ^(3.some, 5.some)(_ + _) ==>
        8.some

      ^(3.some, none[Int])(_ + _) ==>
        None

      // legend:
      // '|@|' is a symbol for constructing Applicative expressions.
      // (f1 |@| f2 |@| ... |@| fn)((v1, v2, ... vn) => ...)
      // (f1 |@| f2 |@| ... |@| fn).tupled

      (3.some |@| 5.some)(_ + _) ==>
        8.some

      (3.some |@| 4.some |@| 5.some)(_ |+| _ |+| _) ==>
        12.some

      (3.some |@| 4.some |@| 5.some |@| 6.some)(_ |+| _ |+| _ |+| _) ==>
        18.some

      (List(1, 2, 3) |@| List(4, 5, 6))(_ |+| _) ==>
        List(5, 6, 7, 6, 7, 8, 7, 8, 9)

      val xs = ('A' to 'Z').map(_.toString).toList
      val cartesianTwo = (xs |@| xs)(_ |+| _)

      cartesianTwo.size ==>
        26 * 26

      cartesianTwo.head ==>
        "AA"

      cartesianTwo(26 * 26 - 1) ==>
        "ZZ"

      val cartesianThree = (xs |@| xs |@| xs)(_ |+| _ |+| _)

      cartesianThree.size ==>
        26 * 26 * 26

      cartesianThree.head ==>
        "AAA"

      cartesianThree(26 * 26 * 26 - 1) ==>
        "ZZZ"
    }
    'tuple - {
      (Option(1) tuple Option(2)) ==>
        Option((1, 2))
      (List(1, 2) tuple List(3, 4)) ==>
        List((1, 3), (1, 4), (2, 3), (2, 4))
    }
  }
}

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

package com.github.dnvriend.monadtx

import com.github.dnvriend.TestSpec

import scalaz._
import Scalaz._

class OptionTTest extends TestSpec {
  // see: http://underscore.io/blog/posts/2013/12/20/scalaz-monad-transformers.html

  // as you are here, I am going to assume that you know what monad transformers are for,
  // but in case you don't:

  // The article above states that 'monad transformers' allow us to stack monads, but what does that mean?
  // Well, basically it means that you can wrap/nest one monad inside another for example, using the standard Scala library:
  // Option[Option[A]] would be a 'stack of monads', of a Future[Option[A]] or a String \/ Option[A], List[Option[A]], and so on.

  // The problem monad transformers fix for us is giving us a simple and easy way (convenient), to work with this
  // nested structure of monads without needing to inline-unwrap the nested monad.

  // To effectively use Monad Transformers, we need three things:
  // 1. Type Definitions
  // 2. A way to construct values of these new types we defined
  // 3. The removal of one layer of nesting when we use the monad

  // For example, to use the 'OptionT' monad transformer, it has the following type:
  // OptionT[M[_], A] which means that it is a monad transformer that constructs an Option[A] inside the monad 'M'.
  // So monad transformers are build from the inside-out.
  //

  // 1. defining types
  type Error[+A] = String \/ A
  type Result[A] = OptionT[Error, A]

  // We have just defined two types (aliases) for the monad we wrap around the Option. This is needed
  // because of type-inference. OptionT expects M to have (one) kind (type), which is M[_], and that is a single parameter,
  // but the Disjunction (\/) has two type parameters. By using a type-alias we can get from two parameters to one.

  // 2. construct values of the defined types
  val result: Result[Int] = 42.point[Result]

}

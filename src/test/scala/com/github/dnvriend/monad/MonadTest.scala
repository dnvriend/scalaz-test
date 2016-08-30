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

package com.github.dnvriend.monad

import com.github.dnvriend.TestSpec

class MonadTest extends TestSpec {
  // see: http://stackoverflow.com/questions/27750046/is-a-collection-with-flatmap-a-monad/27754385#27754385

  // - All Monads are Applicative and are Functors (see Typeclassopedia)

  // A Monad is not required to have any particular "value" or none,
  // only to compose with functions in particular ways.

  // A Monad is a Monoid (in the category of Endofunctors), meaning it's a set with certain operations.

  // Elements of things of type 'A => M[B]' (in scalaz this type is called Kleisli).

  // flatMap is the |+| operation of the monoid

  // the whole point of a monoid is that we can write f |+| g |+| h and not worry about which way we evaluate it.

  // (for Monads) the point is that we should be able to write
  //
  // for {
  //  a <- f("hi")
  //  b <- g(a)
  //  c <- h(b)
  // } yield c
  //
  // and not worry about which order the flatMaps are composed in.

}

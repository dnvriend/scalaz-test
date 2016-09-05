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

package com.github.dnvriend.taggedtypes

import com.github.dnvriend.TestSpec
import com.github.dnvriend.taggedtypes.TaggedTypesTest.{ AirDeliveryCost, Kilogram, PrizePerKilogram }

object TaggedTypesTest {
  // a value class may not be a member of another class
  class Kilogram(val x: Double) extends AnyVal
  class PrizePerKilogram(val x: Double) extends AnyVal
  class AirDeliveryCost(val x: Double) extends AnyVal
}

// http://docs.scala-lang.org/overviews/core/value-classes.html
// https://groups.google.com/forum/#!topic/scalaz/Py_IIfp9d2Q
// https://issues.scala-lang.org/browse/SI-5183
// https://issues.scala-lang.org/browse/SI-7088
// https://github.com/scalaz/scalaz/issues/338
class TaggedTypesTest extends TestSpec {
  //
  // see http://docs.scala-lang.org/overviews/core/value-classes.html
  //
  // Lets first look at the normal Scala Value Classes (SIP-15) and see what they bring to the table
  //

  /**
   * Value classes are a mechanism in Scala to avoid allocating runtime objects. This is
   * acomplished by the definition of a new `AnyVal` subclass.
   *
   * The value class (TaggedTypeTest.Kilogram) has a single, public val parameter that is the underlying runtime representation.
   * The type at compile time is `Kilogram`, but at runtime, the representation is an Double, which has the advantage when used
   * on methods, where a single Double should mean something like `Kilogram`
   */

  def calculatePrize(kg: Kilogram, ppkg: PrizePerKilogram): AirDeliveryCost = new AirDeliveryCost(kg.x * ppkg.x)

  "value classes" should "have primitive runtime representation but act as types in compile time" in {
    calculatePrize(new Kilogram(2.0), new PrizePerKilogram(25.0)).x shouldBe 50.0
  }

  //
  // advantage, in runtime the objects are in fact primitive types so the memory overhead is pretty low compared to
  // the object instance
  //

}

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

///*
// * Copyright 2016 Dennis Vriend
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.github.dnvriend.monadtx
//
//import com.github.dnvriend.PimpedFuture
//
//import scala.concurrent.Future
//import scalaz.EitherT._
//import scalaz.Scalaz._
//import scalaz._
//import utest._
//
//object FutureDisjunctionTest extends TestSuite {
//  val tests = this{
//    "compose AB that are disjunctions" - {
//      def fea: Future[String \/ Int] = Future.successful(\/-(1))
//      def feb(a: Int): Future[String \/ Int] = Future.successful(\/-(a + 2))
//
//      val composedAB: Future[String \/ Int] = (for {
//        a ← eitherT(fea)
//        ab ← eitherT(feb(a))
//      } yield ab).run
//
//      //      assertMatch(composedAB.futureValue) {
//      //        case \/-(2) ⇒
//      //      }
//    }
//
//    //
//    //    composedAB.map(_.toEither).futureValue should matchPattern {
//    //      case Right(3) ⇒
//    //    }
//  }
//}

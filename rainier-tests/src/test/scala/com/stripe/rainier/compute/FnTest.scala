package com.stripe.rainier.compute

import org.scalatest._

class FnTest extends FunSuite {
  def check[T](description: String, fn: Fn[T, Real], value: T): Unit = {
    test(description) {
      val eval = new Evaluator(Map.empty, Some(0))
      val withApply = eval.toDouble(fn(value))
      val withEncode = eval.toDouble(fn.encode(List(value)))
      assert(withApply == withEncode)
    }
  }

  val fDouble = Fn.numeric[Double]
  val fLong = Fn.numeric[Long]
  val fMap = Fn
    .numeric[Double]
    .keys(List("a", "b"))
    .map { m =>
      m("a") * 2 + m("b")
    }
  val fEnum = Fn
    .enum(List("a", "b", "c"))
    .map { ls =>
      val m = ls.toMap
      m("a") * 4 + m("b") * 5 + m("c") * 6
    }

  check("numeric[Double]", fDouble, 1.0)
  check("numeric[Long]", fLong, 1L)
  check("zip(Double, Long)",
        fDouble
          .zip(fLong)
          .map { case (a, b) => a * 2 + b },
        (2.0, 1L))
  check("Map[String,Double]", fMap, Map("a" -> 2.0, "b" -> 1.0))
  check("enum", fEnum, "b")
  check("enum.zip(map)",
        fEnum
          .zip(fMap)
          .map { case (a, b) => a * 10 + b },
        ("b", Map("a" -> 2.0, "b" -> 1.0)))
}
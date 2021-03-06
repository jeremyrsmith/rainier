package com.stripe.rainier
package core

import com.stripe.rainier.compute._

/**
  * A finite discrete distribution
  *
  * @param pmf A map with keys corresponding to the possible outcomes and values corresponding to the probabilities of those outcomes
  */
final case class Categorical[T](pmf: Map[T, Real]) extends Distribution[T] {
  self =>

  def logDensity(seq: Seq[T]) = {
    val keys = pmf.keys
    val enums = seq.map { t =>
      keys.map { k =>
        if (k == t) k -> 1.0 else k -> 0.0
      }.toMap
    }
    Vec
      .from(enums)
      .map { value =>
        Real
          .sum(value.map {
            case (t, r) =>
              (r * pmf.getOrElse(t, Real.zero))
          })
          .log
      }
      .columnize
  }

  def map[U](fn: T => U): Categorical[U] =
    Categorical(
      pmf.foldLeft(Map.empty[U, Real]) {
        case (acc, (t, p)) =>
          updateMap(acc, fn(t), p)(Real.zero)(_ + _)
      }
    )

  def flatMap[U](fn: T => Categorical[U]): Categorical[U] =
    Categorical(
      (for {
        (t, p) <- pmf.iterator
        (u, p2) <- fn(t).pmf.iterator
      } yield (u, p * p2)).foldLeft(Map.empty[U, Real]) {
        case (acc, (u, p)) =>
          updateMap(acc, u, p)(Real.zero)(_ + _)
      }
    )

  def zip[U](other: Categorical[U]): Categorical[(T, U)] =
    Categorical(
      for {
        (t, p) <- pmf
        (u, p2) <- other.pmf
      } yield ((t, u), p * p2)
    )

  def generator: Generator[T] = {
    val cdf =
      pmf.toList
        .scanLeft((Option.empty[T], Real.zero)) {
          case ((_, acc), (t, p)) => ((Some(t)), p + acc)
        }
        .collect { case (Some(t), p) => (t, p) }

    Generator.require(cdf.map(_._2).toSet) { (r, n) =>
      val v = r.standardUniform
      require(Math.abs(n.toDouble(cdf.last._2) - 1.0) < 1e-6)
      cdf.find { case (_, p) => n.toDouble(p) >= v }.getOrElse(cdf.last)._1
    }
  }

  def toMixture[U](implicit ev: T <:< Continuous): Mixture =
    Mixture(pmf.map { case (k, v) => (ev(k), v) })
}

object Categorical {
  def boolean(p: Real): Categorical[Boolean] =
    Categorical(Map(true -> p, false -> (Real.one - p)))

  def normalize[T](pmf: Map[T, Real]): Categorical[T] = {
    val total = Real.sum(pmf.values.toList)
    Categorical(pmf.map { case (t, p) => (t, p / total) })
  }

  def list[T](seq: Seq[T]): Categorical[T] =
    normalize(
      seq
        .groupBy(identity)
        .mapValues { l =>
          Real(l.size)
        }
        .toMap)

  def fromSet[T](ts: Set[T]): Categorical[T] = {
    val p = Real.one / ts.size
    Categorical(
      ts.foldLeft(Map.empty[T, Real])((m, t) => m.updated(t, p))
    )
  }
}

/**
  * A Multinomial distribution
  *
  * @param pmf A map with keys corresponding to the possible outcomes of a single multinomial trial and values corresponding to the probabilities of those outcomes
  * @param k The number of multinomial trials
  */
final case class Multinomial[T](pmf: Map[T, Real], k: Real)
    extends Distribution[Map[T, Long]] { self =>

  def logDensity(seq: Seq[Map[T, Long]]) =
    Vec.from(seq).map(logDensity).columnize

  def logDensity(v: Map[T, Real]): Real =
    Combinatorics.factorial(k) + Real.sum(v.map {
      case (t, i) =>
        val p = pmf.getOrElse(t, Real.zero)
        val pTerm =
          Real.eq(i, Real.zero, Real.zero, i * p.log)
        pTerm - Combinatorics.factorial(i)
    })

  def generator: Generator[Map[T, Long]] =
    Categorical(pmf).generator.repeat(k).map { seq =>
      seq.groupBy(identity).map { case (t, ts) => (t, ts.size.toLong) }
    }
}

object Multinomial {
  def optional[T](pmf: Map[T, Real], k: Real): Multinomial[Option[T]] = {
    val total = Real.sum(pmf.values)
    val newPMF = pmf.map { case (t, p) => Option(t) -> p } + (None -> (Real.one - total))
    Multinomial(newPMF, k)
  }
}

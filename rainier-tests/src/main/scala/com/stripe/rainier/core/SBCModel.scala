package com.stripe.rainier.core

import com.stripe.rainier.compute._
import com.stripe.rainier.sampler._

trait SBCModel {
  implicit val rng: RNG = ScalaRNG(1528673302081L)
  def sbc: SBC[_, _]
  val sampler: Sampler = HMC(1)
  val warmupIterations: Int = 10000
  val syntheticSamples: Int = 1000
  val nSamples: Int = 10
  def main(args: Array[String]): Unit = {
    sbc.animate(sampler, warmupIterations, syntheticSamples)
    println(s"\nnew goldset:")
    println(s"$samples")
    println(
      s"If this run looks good, please update the goldset in your SBCModel")
  }
  val samples: List[_] = sbc.posteriorSamples(goldset.size)
  def goldset: List[_]
  val description: String
}

/** Continuous **/
object SBCUniformNormal extends SBCModel {
  def sbc = SBC[Double, Continuous](Uniform(0, 1))((x: Real) => Normal(x, 1))
  def goldset =
    List(0.4420010033766605, 0.29708655359226616, 0.4580571185529902,
      0.29548495557180404, 0.29548495557180404, 0.4433346192079313,
      0.3126501103564071, 0.41049476489022946, 0.41049476489022946,
      0.3189191850501776, 0.42241578429988913, 0.32359383493955896,
      0.4297644243170597, 0.3213457359217549, 0.4319497928718991,
      0.4319497928718991, 0.31463335336666887, 0.4421708496242656,
      0.3144694494158902, 0.3144694494158902, 0.42955871996293515,
      0.42955871996293515, 0.3239644568087005, 0.4296749064087937,
      0.3227574487207308, 0.42710700553298697, 0.3256408604686255,
      0.4256992300936361, 0.3268278402651836, 0.41644368797157466)
  val description = "Normal(x, 1) with Uniform(0, 1) prior"
}

object SBCLogNormal extends SBCModel {
  def sbc =
    SBC[Double, Continuous](LogNormal(0, 1))((x: Real) => LogNormal(x, x))
  def goldset =
    List(0.13164619366907931, 0.12832026951774805, 0.1299114429648742,
      0.12861425267943213, 0.12959152781296665, 0.12959152781296665,
      0.12861477795126522, 0.12965153828871087, 0.1287034531054879,
      0.13044563905326742, 0.1288763337879329, 0.1297848917698844,
      0.12841777066049834, 0.13148749502319232, 0.12738507545241284,
      0.1311668674827308, 0.12856396550939797, 0.12856396550939797,
      0.12964078905006984, 0.1292281367496527, 0.12970729139485995,
      0.12884730753588944, 0.12884730753588944, 0.1300383642753817,
      0.12937988133859285, 0.1294593496275026, 0.1294593496275026,
      0.13018953613738607, 0.12968487409509566, 0.12963426586770474)
  val description = "LogNormal(x, x) with LogNormal(0, 1) prior"
}

/**
  * Note: SBCExponential and SBCLaplace are made-up goldsets. SBC on these is wildly slow.
  */
object SBCExponential extends SBCModel {
  def sbc =
    SBC[Double, Continuous](LogNormal(0, 1))((x: Real) => Exponential(x))
  def goldset =
    List(0.4265683630081846, 0.5189050953677488, 0.49924580068677044,
      0.3879796746979638, 0.4341114186909587, 0.4341114186909587,
      0.46249827359385365, 0.5153090873282923, 0.44657645973736837,
      0.4818619620463942, 0.43936322908013287, 0.4437800418959559,
      0.367162365055694, 0.367162365055694, 0.367162365055694,
      0.367162365055694, 0.367162365055694, 0.4330711704882621,
      0.4330711704882621, 0.5628095742189261, 0.45466790056406947)
  val description = "Exponential(x) with LogNormal(0, 1) prior"
}

object SBCLaplace extends SBCModel {
  def sbc = SBC[Double, Continuous](LogNormal(0, 1))((x: Real) => Laplace(x, x))
  def goldset =
    List(0.4265683630081846, 0.5189050953677488, 0.49924580068677044,
      0.3879796746979638, 0.4341114186909587, 0.4341114186909587,
      0.46249827359385365, 0.5153090873282923, 0.44657645973736837,
      0.4818619620463942, 0.43936322908013287, 0.4437800418959559,
      0.367162365055694, 0.367162365055694, 0.367162365055694,
      0.367162365055694, 0.367162365055694, 0.4330711704882621,
      0.4330711704882621, 0.5628095742189261, 0.45466790056406947)
  val description = "Laplace(x, x) with LogNormal(0, 1) prior"
}

/** Discrete **/
object SBCBernoulli extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 1))((x: Real) => Bernoulli(x))
  def goldset =
    List(0.40256721380254823, 0.3400965341517399, 0.40459435036307895,
      0.3400055576270386, 0.3400055576270386, 0.4045896900961845,
      0.3384461866270611, 0.40649320905675723, 0.3373170047689366,
      0.4046806976219107, 0.33976885243101096, 0.4011889019645508,
      0.34291289494050115, 0.40132913353138466, 0.34276467751719264,
      0.40049445941821965, 0.3424163112150781, 0.3995040179019226,
      0.3445361096234704, 0.3946276640935739, 0.3477675512962426,
      0.39541549150969885, 0.3467201197738379, 0.3971764088815146,
      0.34659226222017575, 0.3973280988503193, 0.34637226434734203,
      0.39682476542721257, 0.39682476542721257, 0.3468627031662517)
  val description = "Bernoulli(x) with Uniform(0, 1) prior"
}

object SBCBinomial extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 1))((x: Real) => Binomial(x, 10))
  def goldset =
    List(0.38352682737562777, 0.37713725719885144, 0.3843658203796727,
      0.37653319639916655, 0.3841649808758958, 0.37602917441422784,
      0.38513502891723805, 0.3760666425642423, 0.3835677389086927,
      0.3778511851623953, 0.3829102559436935, 0.3774954300063823,
      0.3839122752110885, 0.3753151170251144, 0.3856433836604303,
      0.37516846518250807, 0.3858139145947179, 0.3858139145947179,
      0.3755336646936599, 0.385620383769529, 0.37517806006318993,
      0.38448072259257265, 0.37547773176662064, 0.3841089573440733,
      0.37655753139831905, 0.38404048004869973, 0.3770419582524628,
      0.3837326488975881, 0.3770881125375111, 0.38452920539523916)
  val description = "Binomial(x, 10) with Uniform(0, 1) prior"
}

object SBCGeometric extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 1))((x: Real) => Geometric(x))
  def goldset =
    List(0.3946980877543321, 0.3704404816554237, 0.40124115910070896,
      0.3698005036308322, 0.3698005036308322, 0.39971516051410777,
      0.3768016583347249, 0.39418816180229854, 0.38135425923791133,
      0.38135425923791133, 0.38761920031214686, 0.3725933313958698,
      0.4006874633676174, 0.3701079240266579, 0.39911130889232105,
      0.3765130664519469, 0.3765130664519469, 0.38602615572668714,
      0.38671776214696385, 0.3912097113005318, 0.37504121339782354,
      0.39238474039229126, 0.39238474039229126, 0.3787836050938763,
      0.3940916383743474, 0.3776254193938832, 0.39562245660410106,
      0.3792749142372711, 0.38520924225433234, 0.3843719252678789)
  val description = "Geometric(x) with Uniform(0, 1) prior"
}

object SBCGeometricZeroInflated extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 1))((x: Real) =>
      Geometric(.3).zeroInflated(x))
  def goldset =
    List(0.3682741265983013, 0.3852529612682177, 0.3604238333388064,
      0.386596856795002, 0.3555033555350904, 0.39217253775032623,
      0.39217253775032623, 0.3550136947273093, 0.38182164324962603,
      0.38182164324962603, 0.36722421685006434, 0.3902898312064711,
      0.3531099114009195, 0.3942443064061138, 0.35565699293349323,
      0.38180128466000113, 0.38180128466000113, 0.37379355799257075,
      0.3692587358496806, 0.3588953235783851, 0.3931958168156864,
      0.3585549007172857, 0.39441682029588876, 0.353399981549561,
      0.38860439210841013, 0.35795264331182514, 0.38336146622540296,
      0.35691143054863744, 0.35691143054863744, 0.39069497264393727)
  val description = "Geometric(.3).zeroInflated(x) with Uniform(0, 1) prior"
}

object SBCNegativeBinomial extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 1))((x: Real) => NegativeBinomial(x, 10))
  def goldset =
    List(0.3800079533694811, 0.38732664187094984, 0.3778004530044885,
      0.3890778503553443, 0.3784000682929023, 0.38969681177327586,
      0.3768160378762959, 0.38934633977869687, 0.37964931855881123,
      0.38569495356274847, 0.3815274158067623, 0.3862507244501119,
      0.3792758115021018, 0.38999659702795936, 0.37704329770885253,
      0.39006541287453605, 0.3769314783767383, 0.3769314783767383,
      0.3888128903324072, 0.37771053278860184, 0.3894266904425174,
      0.3795579288631784, 0.3888781606413291, 0.38016358374948,
      0.38718627313504206, 0.3803992126326073, 0.3860688944721329,
      0.38115232781478925, 0.3859043133348471, 0.3775046989250789)
  val description = "NegativeBinomial(x, 10) with Uniform(0, 1) prior"
}

object SBCBinomialPoissonApproximation extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 0.04))((x: Real) => Binomial(x, 200))
  def goldset =
    List(0.0148800597137314, 0.015317170042773269, 0.01488550428092928,
      0.015307806726724743, 0.014888972429890924, 0.014888972429890924,
      0.014888972429890924, 0.015311272409960632, 0.014883390039407737,
      0.015308909218749077, 0.014896886333494985, 0.015311749362304759,
      0.014881203337364965, 0.015313597032790406, 0.014902315029838615,
      0.01529044551523465, 0.01493560566956868, 0.015259544181871997,
      0.014933877237751992, 0.015277193714902262, 0.01491605068610927,
      0.01529994020509738, 0.014922422881331353, 0.01527873165402044,
      0.014918410414603376, 0.015276597677704591, 0.014919186650737727,
      0.015273313908373103, 0.01492364046118513, 0.015269553280871173)
  val description =
    "Poisson approximation to Binomial: Binomial(x, 200) with Uniform(0, 0.04) prior"
}

object SBCBinomialNormalApproximation extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0.4, 0.6))((x: Real) => Binomial(x, 300))
  def goldset =
    List(1, 6, 10, 234, 10, 3, 4, 9, 8, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3)
  val description =
    "Normal approximation to Binomial: Binomial(x, 200) with Uniform(0.4, 0.6) prior"
}

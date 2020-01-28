package com.stripe.rainier.core

import com.stripe.rainier.compute._
import com.stripe.rainier.sampler._

trait SBCModel[T] {
  def sbc: SBC[T]
  val warmupIterations: Int = 10000
  val syntheticSamples: Int = 1000
  val nSamples: Int = 10
  def sampler(iterations: Int) = HMC(warmupIterations, iterations, 1)
  def main(args: Array[String]): Unit = {
    implicit val rng: RNG = ScalaRNG(1528673302081L)
    sbc.animate(syntheticSamples)(sampler)
    println(s"\nnew goldset:")
    println(s"$samples")
    println(s"\ngoldset true value: $trueValue")
    println(
      s"If this run looks good, please update the goldset in your SBCModel")
  }
  val (samples, trueValue) = {
    implicit val rng: RNG = ScalaRNG(1528673302081L)
    val (values, trueValue) = sbc.synthesize(syntheticSamples)
    val (model, real) = sbc.fit(values)
    val samples =
      model.sample(sampler(goldset.size), 1).predict(real)
    (samples, trueValue)
  }

  def goldset: List[Double]
  val description: String
}

/** Continuous **/
object SBCUniformNormal extends SBCModel[Double] {
  def sbc = SBC(Uniform(0, 1))((x: Real) => Normal(x, 1))
  def goldset =
    List(0.41017188683294314, 0.3616793215374072, 0.4588864663275664,
      0.3585934514598037, 0.3661954526232371, 0.3661954526232371,
      0.3661954526232371, 0.3661954526232371, 0.3661954526232371,
      0.422323689478411, 0.422323689478411, 0.43008442566497873,
      0.464762503135008, 0.464762503135008, 0.35272642970892654,
      0.37651429316785406, 0.383328568312589, 0.40833318579808103,
      0.3692362391280442, 0.3692362391280442, 0.41316724405169086,
      0.41316724405169086, 0.41316724405169086, 0.37981446026906956,
      0.37981446026906956, 0.37981446026906956, 0.37981446026906956,
      0.37981446026906956, 0.37088671931027306, 0.39414790170881636)

  val description = "Normal(x, 1) with Uniform(0, 1) prior"
}

object SBCLogNormal extends SBCModel[Double] {
  def sbc =
    SBC(LogNormal(0, 1))((x: Real) => LogNormal(x, x))
  def goldset =
    List(0.7757238193473187, 0.7757238193473187, 0.7757238193473187,
      0.7670051840731767, 0.7597197294676783, 0.7694051932593591,
      0.7771051152182766, 0.7609690436632277, 0.7609690436632277,
      0.7810647345048564, 0.7810647345048564, 0.7810647345048564,
      0.7651678534127364, 0.7651678534127364, 0.7651678534127364,
      0.7651678534127364, 0.7651678534127364, 0.7616720833764435,
      0.772446570940676, 0.7915875875845806, 0.7504079124519804,
      0.7658816790182051, 0.7910446571690907, 0.7910446571690907,
      0.788058749529882, 0.7360403158426907, 0.7360403158426907,
      0.8102654396175825, 0.7907309641864574, 0.7634911777736193)

  val description = "LogNormal(x, x) with LogNormal(0, 1) prior"
}

object SBCExponential extends SBCModel[Double] {
  def sbc =
    SBC(LogNormal(0, 1))((x: Real) => Exponential(x))
  def goldset =
    List(0.8400149057381384, 0.7476108818648785, 0.7571770837363935,
      0.7724264866831254, 0.793419552322468, 0.793419552322468,
      0.793419552322468, 0.793419552322468, 0.793419552322468,
      0.7967563942007311, 0.7967563942007311, 0.7629011197291682,
      0.7629011197291682, 0.7629011197291682, 0.7629011197291682,
      0.7496717784751415, 0.7543212094427262, 0.7585397956917006,
      0.7929470914470256, 0.7565127923769495, 0.7971490921932283)
  val description = "Exponential(x) with LogNormal(0, 1) prior"
}

object SBCLaplace extends SBCModel[Double] {
  def sbc = SBC(LogNormal(0, 1))((x: Real) => Laplace(x, x))
  def goldset =
    List(0.7642066336273645, 0.7642066336273645, 0.7642066336273645,
      0.7384590847168169, 0.7384590847168169, 0.7970881160074392,
      0.7970881160074392, 0.7484295728612199, 0.7780365322888609,
      0.7510726879718242, 0.7510726879718242, 0.7813430068522235,
      0.7813430068522235, 0.758882010251168, 0.7773469952874031,
      0.7773469952874031, 0.7773469952874031, 0.7773469952874031,
      0.7773469952874031, 0.7791986413736735, 0.7567447891995387)

  val description = "Laplace(x, x) with LogNormal(0, 1) prior"
}

object SBCGamma extends SBCModel[Double] {
  def sbc = SBC(LogNormal(0, 1))((x: Real) => Gamma(x, x))
  def goldset =
    List(0.7698406376565916, 0.7605749266362993, 0.7504906816469634,
      0.7504906816469634, 0.7520174166723872, 0.7510455646366141,
      0.7802499951158273, 0.7802499951158273, 0.7590173908499321,
      0.7700662697337262, 0.7700662697337262, 0.7700662697337262,
      0.7459043436966699, 0.7459043436966699, 0.7692996678948734,
      0.7461753567598703, 0.7467275902812742, 0.7467275902812742,
      0.7707184616358422, 0.7627155831357896, 0.7627155831357896)
  val description = "Gamma(x, x) with LogNormal(0, 1) prior"
}

/** Discrete **/
object SBCBernoulli extends SBCModel[Long] {
  def sbc =
    SBC(Uniform(0, 1))((x: Real) => Bernoulli(x))
  def goldset =
    List(0.45767498817423224, 0.45767498817423224, 0.4292350779037769,
      0.4292350779037769, 0.48391640746941944, 0.48391640746941944,
      0.4361760096539119, 0.4684522158046024, 0.441158910906239,
      0.441158910906239, 0.46954322149817834, 0.46954322149817834,
      0.44809610753466217, 0.4654210935263756, 0.4654210935263756,
      0.4654210935263756, 0.4654210935263756, 0.4654210935263756,
      0.46951235915507267, 0.446739274901168, 0.446739274901168,
      0.44141973737613327, 0.4751689915241449, 0.4751689915241449,
      0.4386599781628837, 0.4386599781628837, 0.45042907830237666,
      0.4539601973385092, 0.4539601973385092, 0.4539601973385092)
  val description = "Bernoulli(x) with Uniform(0, 1) prior"
}

object SBCBinomial extends SBCModel[Long] {
  def sbc =
    SBC(Uniform(0, 1))((x: Real) => Binomial(x, 10))
  def goldset =
    List(0.4535065256280634, 0.4626187458545526, 0.4626187458545526,
      0.4511858944937367, 0.4562103990243661, 0.4562103990243661,
      0.45053233902438183, 0.45053233902438183, 0.45774584251133293,
      0.45774584251133293, 0.45206304454153684, 0.45206304454153684,
      0.45347567314971393, 0.4642348892017204, 0.4494125264409434,
      0.4440650639105339, 0.4593119144263747, 0.4593119144263747,
      0.4593119144263747, 0.4593119144263747, 0.456473316354661,
      0.456473316354661, 0.456473316354661, 0.456473316354661,
      0.4714549587182768, 0.4473437842515287, 0.4473437842515287,
      0.44781510594768753, 0.4627226838014988, 0.4627226838014988)

  val description = "Binomial(x, 10) with Uniform(0, 1) prior"
}

object SBCGeometric extends SBCModel[Long] {
  def sbc =
    SBC(Uniform(0, 1))((x: Real) => Geometric(x))
  def goldset =
    List(0.4287105349438442, 0.4287105349438442, 0.41021786928520143,
      0.41021786928520143, 0.4457684544863336, 0.4457684544863336,
      0.41473827075503983, 0.43569867687309644, 0.41798837446375925,
      0.41798837446375925, 0.43640731763910384, 0.43640731763910384,
      0.42249904576797376, 0.43373145821055636, 0.43373145821055636,
      0.43373145821055636, 0.43373145821055636, 0.43373145821055636,
      0.4364213989386786, 0.4216010651625925, 0.4216010651625925,
      0.41813290104070167, 0.4400805356335908, 0.4400805356335908,
      0.4163576991493826, 0.4163576991493826, 0.4239788600387847,
      0.4262983521091081, 0.4262983521091081, 0.4262983521091081)
  val description = "Geometric(x) with Uniform(0, 1) prior"
}

object SBCGeometricZeroInflated extends SBCModel[Long] {
  def sbc =
    SBC(Uniform(0, 1))((x: Real) => Geometric(.3).zeroInflated(x))
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

object SBCNegativeBinomial extends SBCModel[Long] {
  def sbc =
    SBC(Uniform(0, 1))((x: Real) => NegativeBinomial(x, 10))
  def goldset =
    List(0.4553926770068776, 0.46209605583937796, 0.46209605583937796,
      0.45368544386574533, 0.4573797790430212, 0.4573797790430212,
      0.45320071372096127, 0.45320071372096127, 0.4585116596148313,
      0.4585116596148313, 0.45432788580046957, 0.45432788580046957,
      0.45536763126593194, 0.46328600301357886, 0.4523801519552559,
      0.44843331789401597, 0.45967056772799697, 0.45967056772799697,
      0.45967056772799697, 0.45967056772799697, 0.45757312703780834,
      0.45757312703780834, 0.45757312703780834, 0.45757312703780834,
      0.4685970355410904, 0.45086401829717165, 0.45086401829717165,
      0.4511952976711324, 0.46217710254268235, 0.46217710254268235)
  val description = "NegativeBinomial(x, 10) with Uniform(0, 1) prior"
}

object SBCBinomialPoissonApproximation extends SBCModel[Long] {
  def sbc =
    SBC(Uniform(0, 0.04))((x: Real) => Binomial(x, 200))
  def goldset =
    List(0.017440281648749462, 0.018156791522966585, 0.017832307148862615,
      0.017832307148862615, 0.01764184018418859, 0.01764184018418859,
      0.01764184018418859, 0.01764184018418859, 0.01761432778143003,
      0.01782506251422257, 0.017758021850243996, 0.017068941930440742,
      0.017439831294949575, 0.018069678815460746, 0.01712111076972621,
      0.01734398312689828, 0.01740659008696699, 0.01740659008696699,
      0.017223115975477873, 0.01725536978242484, 0.017796347431766203,
      0.017796347431766203, 0.017796347431766203, 0.017375453600467692,
      0.01744443200455081, 0.01744443200455081, 0.017711024393722616,
      0.017600800558462717, 0.01762758829511415, 0.01762758829511415)
  val description =
    "Poisson approximation to Binomial: Binomial(x, 200) with Uniform(0, 0.04) prior"
}

object SBCBinomialNormalApproximation extends SBCModel[Long] {
  def sbc =
    SBC(Uniform(0.4, 0.6))((x: Real) => Binomial(x, 300))
  def goldset =
    List(0.3849093699203164, 0.3822629591199987, 0.38374303903059914,
      0.3832324864849272, 0.38389219985327067, 0.3835866724851299,
      0.3830818707173923, 0.3835660923949862, 0.3841609845229018,
      0.3820783526940713, 0.3849827665275495, 0.38236510331364865,
      0.3838715727269887, 0.3840183371678221, 0.3828960655858176,
      0.3841785878664216, 0.3827156431825414, 0.38488715647596444,
      0.38157319616823965, 0.38511364643858215, 0.3819850513881394,
      0.3857373545069211, 0.3818792213826954, 0.38587080685826664,
      0.3813170524170525, 0.38588405883758004, 0.3809126103183984,
      0.38614580147352096, 0.38091354056091625, 0.3841577712144981)
  val description =
    "Normal approximation to Binomial: Binomial(x, 200) with Uniform(0.4, 0.6) prior"

}

object SBCLargePoisson extends SBCModel[Long] {
  def sbc =
    SBC(Uniform(0.8, 1))((x: Real) => Poisson(x * 1000))
  def goldset =
    List(0.8896349481612058, 0.8896349481612058, 0.8885818635805025,
      0.8877725908728087, 0.8896466850658817, 0.8896466850658817,
      0.8865420383245867, 0.8890007745543766, 0.8890007745543766,
      0.8890007745543766, 0.889214282921085, 0.889214282921085,
      0.889214282921085, 0.8884035373062821, 0.8884035373062821,
      0.8884035373062821, 0.8901419470474745, 0.8893965196352073,
      0.8904689666375939, 0.887781151309036, 0.887781151309036,
      0.8900459067906088, 0.8877582818307215, 0.890099748824956,
      0.8895989110588538, 0.8880595333887795, 0.8880595333887795,
      0.8894558924425451, 0.8879694546000153, 0.8891662635397245)

  val description =
    "Poisson(x*1000) with Uniform(0.8, 1) prior"
}

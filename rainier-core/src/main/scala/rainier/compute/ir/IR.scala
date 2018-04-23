package rainier.compute.ir

import rainier.compute
import rainier.compute._

import scala.collection.mutable

sealed trait IR

case class Variable(original: compute.Variable) extends IR
case class Const(value: Double) extends IR

case class BinaryIR(left: IR, right: IR, op: BinaryOp) extends IR
case class UnaryIR(original: IR, op: UnaryOp) extends IR

case class Sym private(id: Int)
object Sym {
  private var curIdx = 0
  def freshSym(): Sym = {
    val r = Sym(curIdx)
    curIdx += 1
    r
  }
}
case class VarDef(sym: Sym, rhs: IR) extends IR
case class VarRef(sym: Sym) extends IR

case class MethodDef(sym: Sym, rhs: IR) extends IR
case class MethodRef(sym: Sym) extends IR

object IR {
  private val alreadySeen: mutable.Map[Real, Sym] = mutable.Map.empty
  private val symVarDef: mutable.Map[Sym, VarDef] = mutable.Map.empty
  private val symMethodDef: mutable.Map[Sym, MethodDef] = mutable.Map.empty
  def toIR(r: Real): IR = {
    if (alreadySeen.contains(r))
      VarRef(alreadySeen(r))
    else r match {
      case compute.Constant(value) => Const(value)
        // variable access is treated like an atomic operation and is not stored in a VarDef
      case v: compute.Variable => Variable(v)
      case b: BinaryReal =>
        val bIR = BinaryIR(toIR(b.left), toIR(b.right), b.op)
        createVarDefFromOriginal(b, bIR)
      case u: UnaryReal =>
        val uIR = UnaryIR(toIR(u.original), u.op)
        createVarDefFromOriginal(u, uIR)
    }
  }
  def packIntoMethods(p: IR): (IR, Set[MethodDef]) = {
    val packingSizeLimit = 20
    def internalTraverse(p: IR): (IR, Int) = p match {
      case c: Const => (c, 1)
      case v: Variable => (v, 1)
      case vd: VarDef =>
        val (traversedRhs, rhsSize) = traverseAndMaybePack(vd.rhs, packingSizeLimit-1)
        (VarDef(vd.sym, traversedRhs), rhsSize+1)
      case vr: VarRef =>
        (vr, 1)
      case b: BinaryIR =>
        val (leftIR, leftSize) = traverseAndMaybePack(b.left, packingSizeLimit/2)
        val (rightIR, rightSize) = traverseAndMaybePack(b.right, packingSizeLimit/2)
        (BinaryIR(leftIR, rightIR, b.op), leftSize+rightSize+1)
      case u: UnaryIR =>
        val (traversedIR, irSize) = traverseAndMaybePack(u.original, packingSizeLimit-1)
        (traversedIR, irSize+1)
    }
    def traverseAndMaybePack(p: IR, localSizeLimit: Int): (IR, Int) = {
      val (pt, size) = internalTraverse(p)
      if (size >= localSizeLimit)
        (packIntoMethod(pt), 1)
      else
        (pt, size)
    }
    val (pPacked, _) = internalTraverse(p)
    (pPacked, symMethodDef.values.toSet)
  }

  private def createVarDefFromOriginal(original: Real, rhs: IR): VarDef = {
    val s = Sym.freshSym()
    val vd = VarDef(s, rhs)
    alreadySeen(original) = s
    symVarDef(s) = vd
    vd
  }
  private def packIntoMethod(rhs: IR): MethodRef = {
    val s = Sym.freshSym()
    val md = MethodDef(s, rhs)
    symMethodDef(s) = md
    MethodRef(s)
  }
}
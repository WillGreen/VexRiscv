package vexriscv

import java.util

import spinal.core._
import spinal.lib._

import scala.beans.BeanProperty

trait JumpService{
  def createJumpInterface(stage : Stage, priority : Int = 0) : Flow[UInt]
}

trait IBusFetcher{
  def haltIt() : Unit
  def flushIt() : Unit
  def incoming() : Bool
  def pcValid(stage : Stage) : Bool
  def getInjectionPort() : Stream[Bits]
}


trait DecoderService{
  def add(key : MaskedLiteral,values : Seq[(Stageable[_ <: BaseType],Any)])
  def add(encoding :Seq[(MaskedLiteral,Seq[(Stageable[_ <: BaseType],Any)])])
  def addDefault(key : Stageable[_ <: BaseType], value : Any)
}

case class ExceptionCause() extends Bundle{
  val code = UInt(4 bits)
  val badAddr = UInt(32 bits)
}

trait ExceptionService{
  def newExceptionPort(stage : Stage, priority : Int = 0) : Flow[ExceptionCause]
}

trait PrivilegeService{
  def isUser(stage : Stage) : Bool
}

trait InterruptionInhibitor{
  def inhibateInterrupts() : Unit
}

trait ExceptionInhibitor{
  def inhibateException() : Unit
}

case class MemoryTranslatorCmd() extends Bundle{
  val isValid = Bool
  val virtualAddress  = UInt(32 bits)
  val bypassTranslation = Bool
}
case class MemoryTranslatorRsp() extends Bundle{
  val physicalAddress = UInt(32 bits)
  val isIoAccess = Bool
  val allowRead, allowWrite, allowExecute, allowUser = Bool
  val miss = Bool
  val hit = Bool
}

case class MemoryTranslatorBus() extends Bundle with IMasterSlave{
  val cmd = MemoryTranslatorCmd()
  val rsp = MemoryTranslatorRsp()
  val end = Bool

  override def asMaster() : Unit = {
    out(cmd, end)
    in(rsp)
  }
}

trait MemoryTranslator{
  def newTranslationPort(priority : Int, args : Any) : MemoryTranslatorBus
}


trait ReportService{
  def add(that : (String,Object)) : Unit
}

class BusReport{
  @BeanProperty var kind = ""
  @BeanProperty var flushInstructions = new util.LinkedList[Int]()
  @BeanProperty var info : Object = null
}
class CacheReport {
  @BeanProperty var size = 0
  @BeanProperty var bytePerLine = 0
}
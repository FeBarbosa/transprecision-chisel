package transp

import chisel3._
import chisel3.util.Cat

class DoubleToFloat extends Module {
  val io = IO(new Bundle {
    val input = Input(UInt(64.W))
    val output = Output(UInt(32.W))
  })

  val doubleTemp = Wire(UInt(64.W))
  val sig = Wire(UInt(1.W))
  val exp = Wire(UInt(8.W))
  val frac = Wire(UInt(23.W))
  // val floatTemp = Wire(UInt(32.W))
  // val doubleTemp = VecInit(io.input.asBool())

  doubleTemp := io.input

  sig := doubleTemp(63)

  when(doubleTemp(62, 0).asUInt() === 0.asUInt(63.W)){ // ZERO
    exp := 0.asUInt(8.W)
    frac := 0.asUInt(23.W)
  } .elsewhen((doubleTemp(62, 52).andR() === 1.asUInt(1.W)) || (doubleTemp(62, 52).orR() === 0.asUInt(1.W))){ // infinity or NaN or denormalized

    when(doubleTemp(62,52).andR() === 1.asUInt(1.W)){
      exp := 255.asUInt(8.W)
    } .otherwise{
      exp := 0.asUInt(8.W)
    }

    frac := doubleTemp(51, 29)
  } .elsewhen(doubleTemp(62) === 1.asUInt(1.W) && doubleTemp(61, 59).orR() =/= 0.asUInt(1.W)) { // greater than the max exponent value

    exp := ((1 << 8) - 2).asUInt(8.W)
    frac := ((1 << 23) - 1).asUInt(23.W)

  } .elsewhen(doubleTemp(62) === 0.asUInt(1.W) && doubleTemp(61, 59).andR() =/= 1.asUInt(1.W)) { // less than the minimum exponent value

    exp := 1.asUInt(8.W)
    frac := 0.asUInt(23.W)

  } .otherwise{

    exp := Cat(doubleTemp(62), doubleTemp(58, 52))
    frac := doubleTemp(51, 29)

  }

  io.output := Cat(sig, exp, frac)
}

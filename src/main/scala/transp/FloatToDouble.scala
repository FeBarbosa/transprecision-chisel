package transp

import chisel3._
import chisel3.util.Cat

class FloatToDouble extends Module {
  val io = IO(new Bundle {
    val input = Input(UInt(32.W))
    val output = Output(UInt(64.W))
  })

  val floatTemp= Wire(UInt(32.W))
  val sig = Wire(UInt(1.W))
  val exp = Wire(UInt(11.W))
  val frac = Wire(UInt(52.W))
  // val floatTemp = Wire(UInt(32.W))
  // val doubleTemp = VecInit(io.input.asBool())

  floatTemp := io.input

  sig := floatTemp(31)

  when(floatTemp(30, 0).asUInt() === 0.asUInt(31.W)){ // ZERO
    exp := 0.asUInt(11.W)
    frac := 0.asUInt(52.W)
  } .elsewhen((floatTemp(30, 23).andR() === 1.asUInt(1.W)) || (floatTemp(30, 23).orR() === 0.asUInt(1.W))){ // infinity or NaN or denormalized

    when(floatTemp(30,23).andR() === 1.asUInt(1.W)){
      exp := ((1 << 11) - 1 ).asUInt(11.W)
    } .otherwise{
      exp := 0.asUInt(11.W)
    }

    frac := Cat(floatTemp(22, 0), 0.asUInt(29.W))
  } .otherwise{

    when(floatTemp(30) === 1.asUInt(1.W)) {
      exp := Cat(floatTemp(30), 0.asUInt(3.W), floatTemp(29, 23))
    } .otherwise {
      exp := Cat(floatTemp(30), 7.asUInt(3.W), floatTemp(29, 23))
    }

    frac := Cat(floatTemp(22, 0), 0.asUInt(29.W))
  }

  io.output := Cat(sig, exp, frac)
}

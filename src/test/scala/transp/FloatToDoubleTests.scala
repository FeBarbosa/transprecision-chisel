package transp

import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class FloatToDoubleTests(c: FloatToDouble) extends PeekPokeTester(c) {
  val filename = (sys.env("PWD") + "/input/b.txt")

  // println(s"\n${filename}\n")

  val bufferedSource = Source.fromFile(filename)
  val buf = ArrayBuffer.empty[String]

  for(line <- bufferedSource.getLines()) {
    buf += line
  }
  bufferedSource.close

  for (i <- 0 until buf.size by 2) {
    val input: UInt = ("b" + buf(i)).asUInt(64.W)
    val output: UInt = ("b" + buf(i+1)).asUInt(64.W)

    poke(c.io.input, input)
    step(1)
    expect(c.io.output, output)
  }
}

class FloatToDoubleTester extends ChiselFlatSpec {
  behavior of "FloatToDouble"
  backends foreach {backend =>
    it should s"Convert a 32-bit floating-point representation on a 64-bit $backend" in {
      Driver(() => new FloatToDouble, backend)((c) => new FloatToDoubleTests(c)) should be (true)
    }
  }
}

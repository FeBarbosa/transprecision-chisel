package transp

import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class transpNaiveDoubleAsFloatTests(c: transpNaiveDoublAsFloat) extends PeekPokeTester(c) {
  val filename = "/media/felipe/Arquivos1/hardware_descriptions/transprecision-chisel/input/naive64as32/fsd_in.txt"
  val bufferedSource = Source.fromFile(filename)
  val buf = ArrayBuffer.empty[String]

  for(line <- bufferedSource.getLines()){
    buf += line
  }
  bufferedSource.close

  // input
  val id_inst = ("b" + buf(0)).asUInt(32.W)
  val data_word_bypass = ("b" + buf(1)).asUInt(64.W)
  val dmem_resp_bits_typ = ("b" + buf(2)).asUInt(3.W)
  val store_data = ("b" + buf(3)).asUInt(64.W)
  val toint_data = ("b" + buf(4)).asUInt(64.W)
  val ex_rs0 = ("b" + buf(5)).asUInt(64.W)

  // output
  val inst = ("b" + buf(6)).asUInt(32.W)
  val dmem_resp_data = ("b" + buf(7)).asUInt(64.W)
  val dmem_resp_type= ("b" + buf(8)).asUInt(3.W)
  val resFPU = ("b" + buf(9)).asUInt(64.W)
  val res_toint_data = ("b" + buf(10)).asUInt(64.W)
  val res_fromint_data = ("b" + buf(11)).asUInt(64.W)


  poke(c.io.id_inst, id_inst)
  poke(c.io.data_word_bypass, data_word_bypass)
  poke(c.io.dmem_resp_bits_typ, dmem_resp_bits_typ)
  poke(c.io.store_data, store_data)
  poke(c.io.toint_data, toint_data)
  poke(c.io.ex_rs0, ex_rs0)
  step(1)
  // println(s"${Binary(inst)}")
  // println(s"${Binary(c.io.inst)}")
  // println(s"${Binary(dmem_resp_bits_typ)}")
  // println(s"${Binary(c.io.dmem_resp_bits_typ)}")
  expect(c.io.inst, inst)
  expect(c.io.dmem_resp_data, dmem_resp_data)
  expect(c.io.dmem_resp_type, dmem_resp_type)
  expect(c.io.resFPU, resFPU)
  expect(c.io.res_toint_data, res_toint_data)
  expect(c.io.res_fromint_data, res_fromint_data)
}

class transpNaiveDoubleAsFloatTester extends ChiselFlatSpec {
  behavior of "transpNaiveDoubleAsFloat"
  backends foreach {backend =>
    it should s"Convert Rocket chip's FPU signals to execute only 32-bit instruction (single precision) $backend" in {
      Driver(() => new transpNaiveDoublAsFloat, backend)((c) => new transpNaiveDoubleAsFloatTests(c)) should be (true)
    }
  }
}

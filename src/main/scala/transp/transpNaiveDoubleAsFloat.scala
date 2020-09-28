package transp

// import Chisel.{Cat, Fill, Module, Mux, UInt, Wire, when}
// import chisel3._

import chisel3._
import chisel3.util.Cat

class transpNaiveDoublAsFloat extends Module{
  val io = IO(new Bundle {
    val id_inst = Input(UInt(32.W))
    val data_word_bypass = Input(UInt(64.W))
    val dmem_resp_bits_typ = Input(UInt(3.W))
    val store_data = Input(UInt(64.W))
    val toint_data = Input(UInt(64.W))
    val ex_rs0 = Input (UInt(64.W))

    val inst = Output(UInt(32.W))
    val dmem_resp_data = Output(UInt(64.W))
    val dmem_resp_type = Output(UInt(3.W))
    val resFPU = Output(UInt(64.W))
    val res_toint_data = Output(UInt(64.W))
    val res_fromint_data = Output(UInt(64.W))
  })
  // Transprecision implementation
  // --------------------------------------------------------------------------------------------------
  // Convert instruction 64 -> 32 (fmt = 00)
  val inst_transp: UInt = Wire(UInt(32.W))

  val DtoF_dmem_resp_data = Module(new DoubleToFloat())
  val FtoD_store_data = Module(new FloatToDouble())

  val FtoD_toint_data = Module(new FloatToDouble())
  val DtoF_fromint_data= Module(new DoubleToFloat())

  inst_transp := io.id_inst

  val isFMV_XD = (inst_transp(31, 25) === 113.asUInt(7.W) && inst_transp(14, 12) === 0.asUInt(3.W) && inst_transp(6,0) === 83.asUInt(7.W))
  val isFMV_DX = (inst_transp(31, 25) === 121.asUInt(7.W) && inst_transp(6,0) === 83.asUInt(7.W))

  val isFCVT_SD = (inst_transp(31, 20) === 1025.asUInt(12.W) && inst_transp(6,0) === 83.asUInt(7.W))
  val isFCVT_DS = (inst_transp(31, 20) === 1056.asUInt(12.W) && inst_transp(6,0) === 83.asUInt(7.W))

  val isFLD= (inst_transp(14, 12) === 3.asUInt(3.W) && inst_transp(6,0) === 7.asUInt(7.W))
  val isFSD= (inst_transp(14, 12) === 3.asUInt(3.W) && inst_transp(6,0) === 39.asUInt(7.W))

  val isFLW= (inst_transp(14, 12) === 2.asUInt(3.W) && inst_transp(6,0) === 7.asUInt(7.W))
  val isFSW= (inst_transp(14, 12) === 2.asUInt(3.W) && inst_transp(6,0) === 39.asUInt(7.W))


  val fmt = inst_transp(26, 25)

  DtoF_dmem_resp_data.io.input := io.data_word_bypass
  FtoD_store_data.io.input := io.store_data
  FtoD_toint_data.io.input := io.toint_data
  DtoF_fromint_data.io.input := io.ex_rs0 // ex_rs(0)


  when((fmt =/= 0.asUInt(2.W) || isFCVT_SD || isFLD || isFSD) && (!isFLW && !isFSW))
  {
    // exclude FCVT.S.D, FCVT.D.S, FLD, FSD, FMV_XD and FMV_DX
    when(!isFLD && !isFSD && !isFCVT_SD && !isFCVT_DS && !isFMV_XD && !isFMV_DX) {
      io.inst := Cat(inst_transp(31, 27), 0.asUInt(2.W), inst_transp(24, 0))
    } .elsewhen(isFCVT_SD || isFCVT_DS){
      // FCVT.S.D and FCVT.D.S
      println("\n\nAqui\n")
      io.inst := Cat(16.asUInt(7.W), inst_transp(19, 15), inst_transp(19, 15), 0.asUInt(3.W), inst_transp(11, 0))
    }.otherwise{
      // FLD and FSD
      io.inst := io.id_inst
    }

    // io.dmem_resp_data := Mux(inst_transp(5) === 1.asUInt(1.W), DtoF_dmem_resp_data.io.output, io.data_word_bypass)

    // Convert FPU internal type interpretation
    when(isFLD) {
      io.dmem_resp_data := DtoF_dmem_resp_data.io.output
      io.dmem_resp_type := 2.asUInt(3.W)
    }.otherwise{
      io.dmem_resp_data := io.data_word_bypass
      io.dmem_resp_type := io.dmem_resp_bits_typ
    }

    when(isFSD) {
      io.resFPU := FtoD_store_data.io.output
    }.otherwise{
      io.resFPU := io.store_data
    }

    // FMV.XD
    when(isFMV_XD){
      io.res_toint_data := FtoD_toint_data.io.output // used in line 490
    }.otherwise{
      io.res_toint_data := io.toint_data
    }

    // FMV.DX
    when(isFMV_DX){
      io.res_fromint_data := DtoF_fromint_data.io.output
      // io.res_fromint_data := Cat(((1 << 32) - 1).asUInt(32.W), DtoF_fromint_data.io.output(31, 0))
    }.otherwise{
      io.res_fromint_data := io.ex_rs0 // ex_rs(0)
    }

  }.otherwise{
    io.inst := io.id_inst
    io.dmem_resp_data := io.data_word_bypass
    io.dmem_resp_type := io.dmem_resp_bits_typ
    io.res_toint_data := io.toint_data
    io.resFPU := io.store_data
    io.res_fromint_data := io.ex_rs0
  }

  // --------------------------------------------------------------------------------------------------
}

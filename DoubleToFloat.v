module DoubleToFloat(
  input         clock,
  input         reset,
  input  [63:0] io_input,
  output [31:0] io_output
);
  wire  sig; // @[DoubleToFloat.scala 27:20]
  wire  _T_2; // @[DoubleToFloat.scala 29:35]
  wire  _T_4; // @[DoubleToFloat.scala 32:39]
  wire  _T_7; // @[DoubleToFloat.scala 32:87]
  wire  _T_8; // @[DoubleToFloat.scala 32:90]
  wire  _T_9; // @[DoubleToFloat.scala 32:61]
  wire [7:0] _GEN_0; // @[DoubleToFloat.scala 34:53]
  wire  _T_17; // @[DoubleToFloat.scala 41:73]
  wire  _T_19; // @[DoubleToFloat.scala 41:48]
  wire  _T_21; // @[DoubleToFloat.scala 46:30]
  wire  _T_23; // @[DoubleToFloat.scala 46:74]
  wire  _T_24; // @[DoubleToFloat.scala 46:77]
  wire  _T_25; // @[DoubleToFloat.scala 46:48]
  wire [7:0] _T_28; // @[Cat.scala 29:58]
  wire [7:0] _GEN_1; // @[DoubleToFloat.scala 46:96]
  wire [22:0] _GEN_2; // @[DoubleToFloat.scala 46:96]
  wire [7:0] _GEN_3; // @[DoubleToFloat.scala 41:95]
  wire [22:0] _GEN_4; // @[DoubleToFloat.scala 41:95]
  wire [7:0] _GEN_5; // @[DoubleToFloat.scala 32:109]
  wire [22:0] _GEN_6; // @[DoubleToFloat.scala 32:109]
  wire [7:0] exp; // @[DoubleToFloat.scala 29:54]
  wire [22:0] frac; // @[DoubleToFloat.scala 29:54]
  wire [8:0] _T_30; // @[Cat.scala 29:58]
  assign sig = io_input[63]; // @[DoubleToFloat.scala 27:20]
  assign _T_2 = io_input[62:0] == 63'h0; // @[DoubleToFloat.scala 29:35]
  assign _T_4 = io_input[62:52] == 11'h7ff; // @[DoubleToFloat.scala 32:39]
  assign _T_7 = io_input[62:52] != 11'h0; // @[DoubleToFloat.scala 32:87]
  assign _T_8 = ~_T_7; // @[DoubleToFloat.scala 32:90]
  assign _T_9 = _T_4 | _T_8; // @[DoubleToFloat.scala 32:61]
  assign _GEN_0 = _T_4 ? 8'h7f : 8'h0; // @[DoubleToFloat.scala 34:53]
  assign _T_17 = io_input[61:59] != 3'h0; // @[DoubleToFloat.scala 41:73]
  assign _T_19 = io_input[62] & _T_17; // @[DoubleToFloat.scala 41:48]
  assign _T_21 = ~io_input[62]; // @[DoubleToFloat.scala 46:30]
  assign _T_23 = io_input[61:59] == 3'h7; // @[DoubleToFloat.scala 46:74]
  assign _T_24 = ~_T_23; // @[DoubleToFloat.scala 46:77]
  assign _T_25 = _T_21 & _T_24; // @[DoubleToFloat.scala 46:48]
  assign _T_28 = {io_input[62],io_input[58:52]}; // @[Cat.scala 29:58]
  assign _GEN_1 = _T_25 ? 8'h1 : _T_28; // @[DoubleToFloat.scala 46:96]
  assign _GEN_2 = _T_25 ? 23'h0 : io_input[51:29]; // @[DoubleToFloat.scala 46:96]
  assign _GEN_3 = _T_19 ? 8'hfe : _GEN_1; // @[DoubleToFloat.scala 41:95]
  assign _GEN_4 = _T_19 ? 23'h7fffff : _GEN_2; // @[DoubleToFloat.scala 41:95]
  assign _GEN_5 = _T_9 ? _GEN_0 : _GEN_3; // @[DoubleToFloat.scala 32:109]
  assign _GEN_6 = _T_9 ? io_input[51:29] : _GEN_4; // @[DoubleToFloat.scala 32:109]
  assign exp = _T_2 ? 8'h0 : _GEN_5; // @[DoubleToFloat.scala 29:54]
  assign frac = _T_2 ? 23'h0 : _GEN_6; // @[DoubleToFloat.scala 29:54]
  assign _T_30 = {sig,exp}; // @[Cat.scala 29:58]
  assign io_output = {_T_30,frac}; // @[DoubleToFloat.scala 58:13]
endmodule

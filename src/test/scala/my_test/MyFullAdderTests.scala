package examples

import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class MyFullAdderTests(c: MyFullAdder) extends PeekPokeTester(c) {
  for (t <- 0 until 4) {
    val a    = rnd.nextInt(2)
    val b    = rnd.nextInt(2)
    val cin  = rnd.nextInt(2)
    val res  = a + b + cin
    val sum  = res & 1
    val cout = (res >> 1) & 1
    poke(c.io.a, a)
    poke(c.io.b, b)
    poke(c.io.cin, cin)
    step(1)
    expect(c.io.sum, sum)
    expect(c.io.cout, cout)
  }
}

class MyFullAdderTester extends ChiselFlatSpec {
  behavior of "MyFullAdder"
  backends foreach {backend =>
    it should s"correctly add randomly generated numbers and show carry in $backend" in {
      Driver(() => new MyFullAdder, backend)((c) => new MyFullAdderTests(c)) should be (true)
    }
  }
}

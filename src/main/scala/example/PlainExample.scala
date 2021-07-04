package example

import de.sciss.lucre.DoubleObj
import de.sciss.lucre.Txn.peer
import de.sciss.numbers.Implicits._
import de.sciss.{proc, synth}

import scala.concurrent.stm.Ref

/** Scheduling on the most basic level using a `Scheduler` object directly. */
class PlainExample(args: Array[String]) extends Example(args) {
  override def run()(implicit tx: T): Unit = {
    val instr = proc.Proc[T]()
    instr.graph() = synth.SynthGraph {
      import synth.proc.graph._
      import Ops._
      import synth.ugen._

      val dur   = "dur" .ir
      val freq  = "freq".ar
      freq.poll(0, "freq")
      dur .poll(0, "dur" )
      val sig   = SinOsc.ar(freq) * Line.ar(1.0, 0.0, dur = dur)
      Out.ar(0, Pan2.ar(sig) * AmpCompA.ir(freq))
    }
    // technically, when using an `InMemory` system, you do not
    // need to wrap an object into a handler to use it in later
    // transactions, but it is good practice to do so.
    val instrH    = tx.newHandle(instr)

    val sch       = proc.Scheduler[T]()
    val pitchSeq  = Seq(70, 72, 74, 76, 75, 73, 71)
    val divSeq    = Seq( 4,  4,  8,  8,  4,  2,  1)
    val posRef    = Ref(0)

    val r = proc.Runner(instr)

    def next()(implicit tx: T): Unit = {
      r.stop()  // stop the previous note
      val pos = posRef.getAndTransform(_ + 1) // increment the step counter
      if (pos < pitchSeq.size) {
        val pitch = pitchSeq(pos)
        val div   = divSeq  (pos)
        val dur   = 2.0 / div   // 120 BPM
        val instr = instrH()
        // pass the parameters through the attribute map
        instr.attr.put("dur"  , DoubleObj.newConst(dur * 0.8))
        instr.attr.put("freq" , DoubleObj.newConst(pitch.midiCps))
        r.run()

        // the scheduler takes a logical time in frames which
        // are based on a virtual sample-rate `TimeRef.SampleRate` (14112000 cps).
        // to convert seconds into frames, multipy by the sample-rate.
        // to use a relative time, at the scheduler's current `time`.
        val dt = (proc.TimeRef.SampleRate * dur).toLong
        sch.schedule(sch.time + dt) { implicit tx =>
          next()
        }

      } else {
        tx.afterCommit {
          println("Done.")
          sys.exit()
        }
      }
    }

    next()
  }
}
object PlainExample extends App {
  new PlainExample(args)
}

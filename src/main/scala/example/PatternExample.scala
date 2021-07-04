package example

import de.sciss.patterns
import de.sciss.proc
import de.sciss.synth

/** Demonstrates scheduling via a pattern player.
  *
  * Notes:
  *
  * - the Patterns library is still a bit experimental and little tested
  * - it does not do automatic conversions from midi-notes and scales to frequencies,
  *   so explicit conversions must be given
  * - it does not have a notion of tempo, so durations are always in seconds
  * - there is no "default instrument", so you have to provide a `"play"` key
  *   for the `Bind`, and register a `Proc` to be used as instrument.
  */
class PatternExample(args: Array[String]) extends Example(args) {
  /*
    SuperCollider example:

    (
    // changing duration
    Pbind(
        \dur, Pseq([ Pgeom(0.05, 1.1, 24), Pgeom(0.5, 0.909, 24) ], inf),
        \midinote, Pseq(#[60, 58], inf)
    ).play
    )

   */
  override def run()(implicit tx: T): Unit = {
    val pat = proc.Pattern[T]()
    pat() = patterns.Graph {
      import patterns._
      import patterns.lucre.PatImport._
      import graph._

      val dur   = (GeomSeq(0.05, 1.1).take(24) ++ GeomSeq(0.5, 0.909).take(24)).loop()
      val note  = Pat(60, 58).loop()
      Bind(
        Event.keyDelta  -> dur,
        Event.keyLegato -> 0.8,
        Event.keyFreq   -> note.midiCps,
        Event.keyPlay   -> "instr"
      )
    }

    val instr = proc.Proc[T]()
    instr.graph() = synth.SynthGraph {
      import synth.ugen._
      import synth.proc.graph._
      import Ops._

      val dur   = Duration()
      val freq  = "freq".ar
      val sig   = SinOsc.ar(freq) * Line.ar(1.0, 0.0, dur = dur)
      Out.ar(0, Pan2.ar(sig) * AmpCompA.ir(freq))
    }

    pat.attr.put("instr", instr)
    val r = proc.Runner(pat)
    r.run()
  }
}
object PatternExample extends App {
  new PatternExample(args)
}

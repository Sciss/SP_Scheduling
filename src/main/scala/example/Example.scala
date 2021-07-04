package example

import de.sciss.lucre.synth.InMemory
import de.sciss.proc.{Pattern, SoundProcesses, Universe}
import de.sciss.synth.Server
import org.rogach.scallop.{ScallopConf, ScallopOption}

abstract class Example(args: Array[String]) {
  type T = InMemory.Txn
  type S = InMemory

//  type T = Durable.Txn
//  type S = Durable

  def run()(implicit tx: T): Unit

  SoundProcesses.init()
  Pattern       .init()

  implicit val cursor   : S           = InMemory()
//  implicit val cursor   : S           = Durable(InMemoryDB())
  implicit val universe : Universe[T] = cursor.step { implicit tx => Universe.dummy }


  object cli extends ScallopConf(args) {
    val program : ScallopOption[String] = opt(descr = "Full path of 'scsynth' or 'scsynth.exe'")
    val device  : ScallopOption[String] = opt(descr = "Audio device or Jack client name")
    verify()
  }

  cursor.step { implicit tx =>
    val as = universe.auralSystem
    as.whenStarted { _ =>
      cursor.step { implicit tx =>
        run()
      }
    }
    val cfg = Server.Config()
    cfg.program     = cli.program.getOrElse(Server.defaultProgram)
    cfg.deviceName  = cli.device.toOption
    as.start(cfg)
  }
}

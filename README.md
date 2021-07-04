# SP\_Scheduling

[![Build Status](https://github.com/Sciss/SP_Scheduling/workflows/Scala%20CI/badge.svg?branch=main)](https://github.com/Sciss/SP_Scheduling/actions?query=workflow%3A%22Scala+CI%22)

## statement

This is a project with examples of temporal scheduling in [SoundProcesses](https://github.com/Sciss/SoundProcesses).
It is covered by the [GNU Affero General Public License](https://github.com/Sciss/Patterns/raw/main/LICENSE) v3+ and
comes with absolutely no warranties. To contact the author, send an e-mail to `contact at sciss.de`.

## running

You need have installed the [sbt build tool](https://www.scala-sbt.org/). Then you can launch the examples
via `sbt run`. A choice is presented:

```
Multiple main classes detected. Select one to run:
[1] example.PatternExample
[2] example.PlainExample

Enter number: 
```

Note that the examples boot the SuperCollider server, and `scsynth` must be found in a default location (or set
in environment variable `SC_HOME`). Alternatively you can use `sbt 'run --program /path/to/scsynth'`. You can also
specify the audio device or Jack client name as `sbt 'run --device audio-device-name'`. On Linux, you need to make
sure the Jack audio connections are made, for example using the QjackCtl patch bay.

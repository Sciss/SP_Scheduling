lazy val root = project.in(file("."))
  .settings(
    name := "SP_Scheduling",
    description := "Scheduling Examples for SoundProcesses",
    scalaVersion := "2.13.6",
    libraryDependencies ++= Seq(
      "de.sciss"    %% "soundprocesses-core"  % "4.7.7",
      "de.sciss"    %% "patterns-lucre"       % "1.4.2",
      "org.rogach"  %% "scallop"              % "4.0.3",    // command line option parsing
    ),
  )

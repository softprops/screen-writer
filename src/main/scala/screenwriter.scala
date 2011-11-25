package screenwriter

object TestFile {
  def defaultContent =
    """# write your tests below
    |# commands start with $ cmd
    |# tasks start with > task
    |# see http://eed3si9n.com/testing-sbt-plugins for a thorough intro""".stripMargin
}

object PluginsFile {
  def apply(org: String, name: String, version: String) =
    """
    |addSbtPlugin("%s" %% "%s" %% "%s")
    |
    |# addSbtPlugin("me.lessis" %% "screen-writer" %% "0.1.0-SNAPSHOT")
    |
    |# resolvers += "less is" at "http://repo.lessis.me" """.stripMargin.format(
      org, name, version
    )
}

object BuildFile {
  def defaultContent =
    """# seq(yourPluginSettings:_*)
    |
    |# seq(scriptedTestSettings:_*)""".stripMargin
}

object Plugin extends sbt.Plugin {
  import sbt._
  import sbt.Keys._
  import ScreenWriterKeys._
  import ScriptKeys._
  import java.io.File

  object ScreenWriterKeys {
    // shell tasks
    val script = InputKey[Unit]("script", "Generates a new sbt test script")
    val scrap = InputKey[Unit]("scrap", "Deletes a sbt test script by name")
  }

  // provides some convenient reusable validation
  // tasks to be used in test scripts
  // to use these un comment the relevant lines 
  // in the generated plugin and build def
  object ScriptKeys {
     val contents = InputKey[Boolean]("contents", "Verifies the contents of two files match. Throws an exception if they don't")
  }

  case class DifferingContents(given: File, expected: File, msg: String)
       extends RuntimeException(msg)

  def scriptedTestSettings: Seq[Setting[_]] = Seq(
    contents <<= inputTask { (argsTask: TaskKey[Seq[String]]) =>
      (argsTask, streams) map {
        (args, out) =>
          args match {
            case Seq(given, expected) =>
              val (gf, ef) = (file(given), file(expected))
              if(IO.read(gf).trim.equals(IO.read(ef).trim)) {
                out.log.debug("Contents match")
                true
              } else throw DifferingContents(
                gf, ef,
                "Contents of (%s)\n%s does not match (%s)\n%s" format(
                  given, IO.read(gf), expected, IO.read(ef)
                )
              )
          }
      }
    }
  )

  // these are _not_ wrapped in a config by default
  // because we want to create scripts in the
  // global source directory
  def screenWriterSettings: Seq[Setting[_]] = Seq(
    scrap <<= inputTask { (argsTask: TaskKey[Seq[String]]) =>
      (argsTask, sourceDirectory, name, streams) map {
        (args, src, name, out) =>
          args match {
            case Seq(scrpt) =>
              val sroot = new File(src, "sbt-test/%s/%s".format(name, scrpt))
              IO.delete(sroot)
              out.log.info("scraped script %s" format sroot)
            case _ => sys.error("usage screenwriter:scrap script-name")
          }
      }
    },
    script <<= inputTask { (argsTask: TaskKey[Seq[String]]) =>
      (argsTask, streams, version, name, organization, sourceDirectory) map {
        (args, out, vers, name, org, src) =>
          args match {
            case Seq(scrpt) =>
              val sroot = new File(src, "sbt-test/%s/%s" format(name, scrpt))
              if(sroot.exists) sys.error("%s script already exists" format scrpt)
              else {
                IO.createDirectory(sroot)
                val Seq(testFile, pluginsFile, buildFile) =
                  Seq("test", "project/plugins.sbt", "build.sbt") map {
                    new File(sroot, _)
                  }

                IO.touch(testFile)
                IO.write(testFile, TestFile.defaultContent)

                IO.touch(pluginsFile)
                IO.write(pluginsFile, PluginsFile(org, name, vers))

                IO.touch(buildFile)
                IO.write(buildFile, BuildFile.defaultContent)

                out.log.info("Generated script %s" format scrpt)
              }
            case _ => sys.error("usage: screen-writer:script script-name")
          }
      }
    }
  )
}

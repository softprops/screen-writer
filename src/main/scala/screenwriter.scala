package screenwriter

object TestFile {
  def defaultContent =
    """# write your tests below
    |# commands start with $ cmd
    |# tasks start with > task""".stripMargin
}

object PluginsFile {
  def apply(org: String, name: String, version: String) =
    """addSbtPlugin("%s" %% "%s" %% "%s")""".format(
      org, name, version
    )
}

object BuildFile {
  def defaultContent =
    "# seq(yourPluginSettings:_*)"
}

object Plugin extends sbt.Plugin {
  import sbt._
  import sbt.Keys._
  import ScreenWriterKeys._
  import java.io.File

  object ScreenWriterKeys {
    val script = InputKey[Unit]("script", "Generates a new sbt test script")
    val scrap = InputKey[Unit]("scrap", "Deletes a sbt test script by name")
  }

  // this are not wrapped in a config by default
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
            case _ => error("usage screenwriter:scrap script-name")
          }
      }
    },
    script <<= inputTask { (argsTask: TaskKey[Seq[String]]) =>
      (argsTask, streams, version, name, organization, sourceDirectory) map {
        (args, out, vers, name, org, src) =>
          args match {
            case Seq(scrpt) =>
              val sroot = new File(src, "sbt-test/%s/%s" format(name, scrpt))
              if(sroot.exists) error("%s script already exists" format scrpt)
              else {
                IO.createDirectory(sroot)
                val Seq(testFile, pluginsFile, buildFile) =
                  Seq("test", "project/plugins/build.sbt", "build.sbt") map {
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
            case _ => error("usage: screen-writer:script script-name")
          }
      }
    }
  )
}

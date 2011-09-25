sbtPlugin := true

organization := "me.lessis"

name := "screen-writer"

version <<= sbtVersion { v =>
  if(v.startsWith("0.10")) "0.1.0-%s-SNAPSHOT".format(v)
  else if(v.startsWith("0.11")) "0.1.0-SNAPSHOT"
  else error("unsupportd sbt version %s" format v)
}

seq(ScriptedPlugin.scriptedSettings:_*)

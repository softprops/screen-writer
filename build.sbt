sbtPlugin := true

organization := "me.lessis"

name := "screen-writer"

version <<= sbtVersion(v => "0.1.0-%s-SNAPSHOT".format(v))

seq(ScriptedPlugin.scriptedSettings:_*)

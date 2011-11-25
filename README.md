# screen writer

Write stories for your sbt plugins for your children to tell their grandchildren about

## Install

### globally

Special note: This plugin assumes you already have the scripted plugin installed. To add it simply add it to your plugin def

    libraryDependencies <+= sbtVersion("org.scala-tools.sbt" %% "scripted-plugin" % _)
    
And build def

    seq(ScriptedPlugin.scriptedSettings: _*)

The recommended use is to to use a global plugin   

Add the following to your `~/.sbt/project/plugins.sbt` file

   addSbtPlugin("me.lessis" % "screen-writer" % "0.1.0-SNAPSHOT")

   resolvers += "less is" at "http://repo.lessis.me"

Then create a global build file `~/.sbt/sw.sbt` or call it what you want

    seq(screenWritterSettings:_*)

### locally

Add the following to your `project/plugins.sbt` file

    addSbtPlugin("me.lessis" % "screen-writer" % "0.1.0-SNAPSHOT")

    resolvers += "less is" at "http://repo.lessis.me"
    
Then in your `build.sbt` file

    seq(screenWritterSettings:_*)
    
## usage

This is pretty simple if you've read [this awesome post](http://eed3si9n.com/testing-sbt-plugins) on writing tests for your sbt plugins. screen writer simply generates stubs of the directory and test structures you'd have to create by hand otherwise

    sw-script <name>  # creates a new scripted test structure under src/sbt-test/<name>/...
    sw-scrap <name>   # undoes what script <name> does

Doug Tangren (softprops) 2011

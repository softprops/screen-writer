libraryDependencies <+= sbtVersion(
  "org.scala-tools.sbt" %% "scripted-plugin" % _
)

resolvers += Resolver.url("Typesafe repository", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-releases/"))(Resolver.defaultIvyPatterns)


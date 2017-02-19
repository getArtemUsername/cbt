package cbt
import java.io.File

// would love to call this just `Package` but that conflicts with scala package objects.
trait PackageJars extends BaseBuild with ArtifactInfo{
  def name: String
  def artifactId = name
  def version: String
  def `package`: Seq[File] = lib.concurrently( enableConcurrency )(
    Seq(() => jar, () => docJar, () => srcJar)
  )( _() ).flatten

  def jar: Option[File] = taskCache[PackageJars]("jar").memoize{
    lib.jar( artifactId, scalaMajorVersion, version, exportedClasspath.files, jarTarget )
  }

  def srcJar: Option[File] = taskCache[PackageJars]("srcJar").memoize{
    lib.srcJar( sources, artifactId, scalaMajorVersion, version, scalaTarget, sourceFileFilter, projectDirectory )
  }

  def docJar: Option[File] = taskCache[PackageJars]("docJar").memoize{
    lib.docJar(
      context.cbtLastModified,
      scalaVersion, sourceFiles, compileClasspath, docTarget,
      jarTarget, artifactId, scalaMajorVersion, version,
      scalacOptions, context.paths.mavenCache
    )
  }
}

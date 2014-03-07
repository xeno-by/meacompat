```
00:22 ~/Projects/meacompat (master)$ cat Test.scala
import scala.tools.nsc.Global

class Cake[G <: Global](val global: G) {
  object MacroExpansionOf {
    def unapply(tree: global.Tree): Option[global.Tree] = {
      // MEA compatibility for 2.8.x and 2.9.x
      object Compat { class MacroExpansionAttachment(val original: global.Tree) }
      import Compat._

      locally {
        import global._ // this is where MEA lives in 2.10.x
        implicit def withExpandee(att: MacroExpansionAttachment): WithExpandee = new WithExpandee(att)
        class WithExpandee(att: MacroExpansionAttachment) {
          def expandee: Tree = att.original
        }

        locally {
          implicit def withAttachments(tree: Tree): WithAttachments = new WithAttachments(tree)
          class WithAttachments(val tree: Tree) {
            object EmptyAttachments {
              def all = Set.empty[Any]
            }
            val attachments = EmptyAttachments
          }

          import analyzer._ // this is where MEA lives in 2.11.x
          tree.attachments.all.collect {
            case att: MacroExpansionAttachment => att.expandee
          } headOption
        }
      }
    }
  }
}
00:22 ~/Projects/meacompat (master)$ sbt "+ compile"
[info] Set current project to meacompat (in build file:/Users/xeno_by/Projects/meacompat/)
[info] Setting version to 2.8.2
[info] Set current project to meacompat (in build file:/Users/xeno_by/Projects/meacompat/)
[info] Updating {file:/Users/xeno_by/Projects/meacompat/}meacompat...
[info] Resolving junit#junit;3.8.1 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/xeno_by/Projects/meacompat/target/scala-2.8.2/classes...
[success] Total time: 4 s, completed Mar 8, 2014 12:22:21 AM
[info] Setting version to 2.9.2
[info] Set current project to meacompat (in build file:/Users/xeno_by/Projects/meacompat/)
[info] Updating {file:/Users/xeno_by/Projects/meacompat/}meacompat...
[info] Resolving org.fusesource.jansi#jansi;1.4 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/xeno_by/Projects/meacompat/target/scala-2.9.2/classes...
[success] Total time: 4 s, completed Mar 8, 2014 12:22:25 AM
[info] Setting version to 2.10.0
[info] Set current project to meacompat (in build file:/Users/xeno_by/Projects/meacompat/)
[info] Updating {file:/Users/xeno_by/Projects/meacompat/}meacompat...
[info] Resolving org.fusesource.jansi#jansi;1.4 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/xeno_by/Projects/meacompat/target/scala-2.10/classes...
[warn] there were 3 feature warnings; re-run with -feature for details
[warn] one warning found
[success] Total time: 5 s, completed Mar 8, 2014 12:22:31 AM
[info] Setting version to 2.11.0-RC1
[info] Set current project to meacompat (in build file:/Users/xeno_by/Projects/meacompat/)
[info] Updating {file:/Users/xeno_by/Projects/meacompat/}meacompat...
[info] Resolving jline#jline;2.11 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/xeno_by/Projects/meacompat/target/scala-2.11.0-RC1/classes...
[warn] there were 3 feature warning(s); re-run with -feature for details
[warn] one warning found
[success] Total time: 6 s, completed Mar 8, 2014 12:22:38 AM
[info] Setting version to 2.10.2
[info] Set current project to meacompat (in build file:/Users/xeno_by/Projects/meacompat/)
```
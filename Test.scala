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

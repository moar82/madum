//
// Generated by JTB 1.2.2
//

package util.parser.java.v14.nodes;

/**
 * Grammar production:
 * f0 -> [ PackageDeclaration() ]
 * f1 -> ( ImportDeclaration() )*
 * f2 -> ( TypeDeclaration() )*
 * f3 -> <EOF>
 */
public class CompilationUnit implements Node {
   public NodeOptional f0;
   public NodeListOptional f1;
   public NodeListOptional f2;
   public NodeToken f3;

   public CompilationUnit(NodeOptional n0, NodeListOptional n1, NodeListOptional n2, NodeToken n3) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
   }

   public CompilationUnit(NodeOptional n0, NodeListOptional n1, NodeListOptional n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = new NodeToken("");
   }

   public void accept(util.parser.java.v14.visitors.Visitor v) {
      v.visit(this);
   }
   public Object accept(util.parser.java.v14.visitors.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

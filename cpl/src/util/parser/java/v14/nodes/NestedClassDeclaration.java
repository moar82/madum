//
// Generated by JTB 1.2.2
//

package util.parser.java.v14.nodes;

/**
 * Grammar production:
 * f0 -> ( "static" | "abstract" | "final" | "public" | "protected" | "private" | "strictfp" )*
 * f1 -> UnmodifiedClassDeclaration()
 */
public class NestedClassDeclaration implements Node {
   public NodeListOptional f0;
   public UnmodifiedClassDeclaration f1;

   public NestedClassDeclaration(NodeListOptional n0, UnmodifiedClassDeclaration n1) {
      f0 = n0;
      f1 = n1;
   }

   public void accept(util.parser.java.v14.visitors.Visitor v) {
      v.visit(this);
   }
   public Object accept(util.parser.java.v14.visitors.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

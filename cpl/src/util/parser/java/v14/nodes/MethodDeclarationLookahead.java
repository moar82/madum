//
// Generated by JTB 1.2.2
//

package util.parser.java.v14.nodes;

/**
 * Grammar production:
 * f0 -> ( "public" | "protected" | "private" | "static" | "abstract" | "final" | "native" | "synchronized" | "strictfp" )*
 * f1 -> ResultType()
 * f2 -> <IDENTIFIER>
 * f3 -> "("
 */
public class MethodDeclarationLookahead implements Node {
   public NodeListOptional f0;
   public ResultType f1;
   public NodeToken f2;
   public NodeToken f3;

   public MethodDeclarationLookahead(NodeListOptional n0, ResultType n1, NodeToken n2, NodeToken n3) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
   }

   public MethodDeclarationLookahead(NodeListOptional n0, ResultType n1, NodeToken n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = new NodeToken("(");
   }

   public void accept(util.parser.java.v14.visitors.Visitor v) {
      v.visit(this);
   }
   public Object accept(util.parser.java.v14.visitors.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}


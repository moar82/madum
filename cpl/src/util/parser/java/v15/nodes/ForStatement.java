//
// Generated by JTB 1.2.2
//

package util.parser.java.v15.nodes;

/**
 * Grammar production:
 * f0 -> "for"
 * f1 -> "("
 * f2 -> ( Modifiers() Type() <IDENTIFIER> ":" Expression() | [ ForInit() ] ";" [ Expression() ] ";" [ ForUpdate() ] )
 * f3 -> ")"
 * f4 -> Statement()
 */
public class ForStatement implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public NodeChoice f2;
   public NodeToken f3;
   public Statement f4;

   public ForStatement(NodeToken n0, NodeToken n1, NodeChoice n2, NodeToken n3, Statement n4) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
   }

   public ForStatement(NodeChoice n0, Statement n1) {
      f0 = new NodeToken("for");
      f1 = new NodeToken("(");
      f2 = n0;
      f3 = new NodeToken(")");
      f4 = n1;
   }

   public void accept(util.parser.java.v15.visitors.Visitor v) {
      v.visit(this);
   }
   public Object accept(util.parser.java.v15.visitors.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

//
// Generated by JTB 1.2.2
//

package util.parser.java.v14.nodes;

/**
 * Grammar production:
 * f0 -> "break"
 * f1 -> [ <IDENTIFIER> ]
 * f2 -> ";"
 */
public class BreakStatement implements Node {
   public NodeToken f0;
   public NodeOptional f1;
   public NodeToken f2;

   public BreakStatement(NodeToken n0, NodeOptional n1, NodeToken n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public BreakStatement(NodeOptional n0) {
      f0 = new NodeToken("break");
      f1 = n0;
      f2 = new NodeToken(";");
   }

   public void accept(util.parser.java.v14.visitors.Visitor v) {
      v.visit(this);
   }
   public Object accept(util.parser.java.v14.visitors.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

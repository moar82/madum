//
// Generated by JTB 1.2.2
//

package util.parser.java.v14.nodes;

/**
 * Grammar production:
 * f0 -> ";"
 */
public class EmptyStatement implements Node {
   public NodeToken f0;

   public EmptyStatement(NodeToken n0) {
      f0 = n0;
   }

   public EmptyStatement() {
      f0 = new NodeToken(";");
   }

   public void accept(util.parser.java.v14.visitors.Visitor v) {
      v.visit(this);
   }
   public Object accept(util.parser.java.v14.visitors.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}


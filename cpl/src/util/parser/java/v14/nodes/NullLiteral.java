//
// Generated by JTB 1.2.2
//

package util.parser.java.v14.nodes;

/**
 * Grammar production:
 * f0 -> "null"
 */
public class NullLiteral implements Node {
   public NodeToken f0;

   public NullLiteral(NodeToken n0) {
      f0 = n0;
   }

   public NullLiteral() {
      f0 = new NodeToken("null");
   }

   public void accept(util.parser.java.v14.visitors.Visitor v) {
      v.visit(this);
   }
   public Object accept(util.parser.java.v14.visitors.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

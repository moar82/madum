//
// Generated by JTB 1.2.2
//

package util.parser.java.v14.nodes;

/**
 * Grammar production:
 * f0 -> <IDENTIFIER>
 * f1 -> ":"
 * f2 -> Statement()
 */
public class LabeledStatement implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public Statement f2;

   public LabeledStatement(NodeToken n0, NodeToken n1, Statement n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public LabeledStatement(NodeToken n0, Statement n1) {
      f0 = n0;
      f1 = new NodeToken(":");
      f2 = n1;
   }

   public void accept(util.parser.java.v14.visitors.Visitor v) {
      v.visit(this);
   }
   public Object accept(util.parser.java.v14.visitors.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}


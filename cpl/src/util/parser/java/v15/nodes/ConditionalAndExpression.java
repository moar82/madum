//
// Generated by JTB 1.2.2
//

package util.parser.java.v15.nodes;

/**
 * Grammar production:
 * f0 -> InclusiveOrExpression()
 * f1 -> ( "&&" InclusiveOrExpression() )*
 */
public class ConditionalAndExpression implements Node {
   public InclusiveOrExpression f0;
   public NodeListOptional f1;

   public ConditionalAndExpression(InclusiveOrExpression n0, NodeListOptional n1) {
      f0 = n0;
      f1 = n1;
   }

   public void accept(util.parser.java.v15.visitors.Visitor v) {
      v.visit(this);
   }
   public Object accept(util.parser.java.v15.visitors.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

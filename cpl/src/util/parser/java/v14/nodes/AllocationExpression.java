//
// Generated by JTB 1.2.2
//

package util.parser.java.v14.nodes;

/**
 * Grammar production:
 * f0 -> "new" PrimitiveType() ArrayDimsAndInits()
 *       | "new" Name() ( ArrayDimsAndInits() | Arguments() [ ClassBody() ] )
 */
public class AllocationExpression implements Node {
   public NodeChoice f0;

   public AllocationExpression(NodeChoice n0) {
      f0 = n0;
   }

   public void accept(util.parser.java.v14.visitors.Visitor v) {
      v.visit(this);
   }
   public Object accept(util.parser.java.v14.visitors.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}


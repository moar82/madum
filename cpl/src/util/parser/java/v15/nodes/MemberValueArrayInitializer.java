//
// Generated by JTB 1.2.2
//

package util.parser.java.v15.nodes;

/**
 * Grammar production:
 * f0 -> "{"
 * f1 -> MemberValue()
 * f2 -> ( "," MemberValue() )*
 * f3 -> [ "," ]
 * f4 -> "}"
 */
public class MemberValueArrayInitializer implements Node {
   public NodeToken f0;
   public MemberValue f1;
   public NodeListOptional f2;
   public NodeOptional f3;
   public NodeToken f4;

   public MemberValueArrayInitializer(NodeToken n0, MemberValue n1, NodeListOptional n2, NodeOptional n3, NodeToken n4) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
   }

   public MemberValueArrayInitializer(MemberValue n0, NodeListOptional n1, NodeOptional n2) {
      f0 = new NodeToken("{");
      f1 = n0;
      f2 = n1;
      f3 = n2;
      f4 = new NodeToken("}");
   }

   public void accept(util.parser.java.v15.visitors.Visitor v) {
      v.visit(this);
   }
   public Object accept(util.parser.java.v15.visitors.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

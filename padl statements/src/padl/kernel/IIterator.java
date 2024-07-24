package padl.kernel;

/**
 * @author tagmouty
 */
public interface IIterator extends IStatement {
	String LOGO = "\"II\"";
	char[] getCondition();
	IMethod getDeclaringMethod();
	char[] getReturnTypeName();
	void setReturnTypeName(final char[] aTypeName);
}

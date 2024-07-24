/* (c) Copyright 2001 and following years, Yann-Gaël Guéhéneuc,
 * University of Montreal.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package madum.generator.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import madum.generator.MadumClass;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;
import padl.creator.javafile.util.MethodInvocationUtils;
import padl.creator.javafile.util.PadlParserUtil;
import padl.kernel.IClass;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IOperation;
import padl.kernel.IPackage;
import parser.wrapper.ExtendedASTVisitor;
import parser.wrapper.NamedCompilationUnit;

public class DirectFieldAccessVisitor extends ExtendedASTVisitor {

	private ICodeLevelModel model;
	private List<MadumClass> listOfMadums;

	private Map<String, Set<String>> mapOfAllDirectFieldAccess;
	private Map<String, Set<String>> mapOfTransformersDirectFieldAccess;

	// Buffer Attributes
	private IPackage myCurrentPackage;
	private IFirstClassEntity myCurrentClass;
	private IOperation myCurrentOperation;

	private boolean insideMethod = false;
	private MadumClass currentMadumClass;

	public DirectFieldAccessVisitor(ICodeLevelModel aModel,
			List<MadumClass> aListOfMadums) {
		super();
		this.model = aModel;
		this.listOfMadums = aListOfMadums;

	}

	@Override
	public void endVisit(final AnnotationTypeDeclaration node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final AnnotationTypeMemberDeclaration node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final AnonymousClassDeclaration node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ArrayAccess node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ArrayCreation node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ArrayInitializer node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ArrayType node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final AssertStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final Assignment node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final Block node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final BlockComment node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final BooleanLiteral node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final BreakStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final CastExpression node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final CatchClause node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final CharacterLiteral node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ClassInstanceCreation node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final CompilationUnit node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ConditionalExpression node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ConstructorInvocation node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ContinueStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final DoStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final EmptyStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final EnhancedForStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final EnumConstantDeclaration node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final EnumDeclaration node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ExpressionStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final FieldAccess node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final FieldDeclaration node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ForStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final IfStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ImportDeclaration node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final InfixExpression node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final Initializer node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final InstanceofExpression node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final Javadoc node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final LabeledStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final LineComment node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final MarkerAnnotation node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final MemberRef node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final MemberValuePair node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final MethodDeclaration node) {
		// if (this.insideMethod) {//why? because, if i don't visit a method,
		// the variable will be false so no need to do something here!!!
		// if node is static, don't visit it :-)
		if (node.getParent().getNodeType() == ASTNode.TYPE_DECLARATION
				&& this.myCurrentClass != null) {
			if (!node.resolveBinding().getDeclaringClass().getQualifiedName()
					.equals(this.myCurrentClass.getDisplayID())) {
				System.out.println("Warning endVisit MethodDeclaration "
						+ node.getName());
			}
			this.insideMethod = false;

		}
		// }
		super.endVisit(node);
	}

	@Override
	public void endVisit(final MethodInvocation node) {
		super.endVisit(node);
	}

	@Override
	public void endVisit(final MethodRef node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final MethodRefParameter node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final Modifier node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final NamedCompilationUnit aNamedCompilationUnit) {

		super.endVisit(aNamedCompilationUnit);
	}

	@Override
	public void endVisit(final NormalAnnotation node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final NullLiteral node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final NumberLiteral node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final PackageDeclaration node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ParameterizedType node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ParenthesizedExpression node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final PostfixExpression node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final PrefixExpression node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final PrimitiveType node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final QualifiedName node) {
		if (this.insideMethod && this.myCurrentClass != null) {
			IBinding binding = node.resolveBinding();
			checkFieldAccess("QualifiedName", binding);
		}
		super.endVisit(node);
	}

	@Override
	public void endVisit(final QualifiedType node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ReturnStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final SimpleName node) {
		if (this.insideMethod && this.myCurrentClass != null) {
			IBinding binding = node.resolveBinding();
			checkFieldAccess("simpleName", binding);
		}
		super.endVisit(node);
	}

	/**
	 * 
	 * @param nodeName
	 * @param binding
	 */
	private void checkFieldAccess(String nodeName, IBinding binding) {

		if (binding != null && binding instanceof IVariableBinding) {
			final IVariableBinding varBinding = (IVariableBinding) binding;

			if (varBinding.isField()) {
				// if(varBinding.getModifiers())===if field is constant, don't
				// take it into account
				// if it is only static I don't know :-)
				// System.out.println("It is a field access by  " + nodeName);
				String fieldName = varBinding.getName();

				int fieldVisibility = varBinding.getModifiers();
				if (!(Modifier.isFinal(fieldVisibility) && Modifier
						.isStatic(fieldVisibility))) {
					// it is not a constant
					String fieldType = varBinding.getType().getQualifiedName();
					// System.out.println(" Name= " + fieldName + " Type="
					// + fieldType);

					if (varBinding.getDeclaringClass() != null) {
						ITypeBinding typeBinding = varBinding
								.getDeclaringClass();
						// to handle parameterized type LinkedList<E>
						if (typeBinding.isParameterizedType()) {
							typeBinding = typeBinding.getErasure();
						}

						String declaringClassId = typeBinding
								.getQualifiedName();

						// varBinding.
						// System.out.println("Declaring class= "
						// + declaringClassId);
						if (declaringClassId.equals(this.myCurrentClass
								.getDisplayID())) {
							if (!this.myCurrentOperation.isStatic()) {
								// the method is not static
								if (nodeName.equals("Assignment")) {
									if (!this.mapOfTransformersDirectFieldAccess
											.containsKey(fieldName)) {
										this.mapOfTransformersDirectFieldAccess
												.put(fieldName,
														new HashSet<String>());
									}

									this.mapOfTransformersDirectFieldAccess
											.get(fieldName).add(
													this.myCurrentOperation
															.getDisplayID());
								}

								if (!this.mapOfAllDirectFieldAccess
										.containsKey(fieldName)) {
									this.mapOfAllDirectFieldAccess.put(
											fieldName, new HashSet<String>());
								}

								this.mapOfAllDirectFieldAccess.get(fieldName)
										.add(this.myCurrentOperation
												.getDisplayID());

							}

						}
					}

				}
			}

		}

	}

	@Override
	public void endVisit(final SimpleType node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final SingleMemberAnnotation node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final SingleVariableDeclaration node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final StringLiteral node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final SuperConstructorInvocation node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final SuperFieldAccess node) {
		super.endVisit(node);
	}

	@Override
	public void endVisit(final SuperMethodInvocation node) {
		super.endVisit(node);
	}

	@Override
	public void endVisit(final SwitchCase node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final SwitchStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final SynchronizedStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final TagElement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final TextElement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ThisExpression node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final ThrowStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final TryStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final TypeDeclaration node) {

		if (node.resolveBinding().isClass()
				&& !node.resolveBinding().isMember()) {
			if (this.myCurrentClass == null) {
				
				System.out
						.println("Warning in endVisit(final TypeDeclaration node) --- end of class"
								+ node.resolveBinding().getQualifiedName());

			} else if (!this.myCurrentClass.getDisplayID().equals(
					node.resolveBinding().getQualifiedName())) {
				System.out
						.println("Warning in endVisit(final TypeDeclaration node) 2 --- end of class"
								+ node.resolveBinding().getQualifiedName());
			} else {
				// reset working variables
				this.mapOfAllDirectFieldAccess = null;
				this.mapOfTransformersDirectFieldAccess = null;
				this.myCurrentClass = null;
				this.currentMadumClass = null;
			}
		}

		super.endVisit(node);
	}

	@Override
	public void endVisit(final TypeDeclarationStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final TypeLiteral node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final TypeParameter node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final VariableDeclarationExpression node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final VariableDeclarationFragment node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final VariableDeclarationStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final WhileStatement node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final WildcardType node) {

		super.endVisit(node);
	}

	@Override
	public void endVisitJavaFilePath(final String javaFilePath) {

		super.endVisitJavaFilePath(javaFilePath);
	}

	@Override
	public void postVisit(final ASTNode node) {

		super.postVisit(node);
	}

	@Override
	public void preVisit(final ASTNode node) {

		super.preVisit(node);
	}

	@Override
	public boolean preVisit2(final ASTNode node) {

		return super.preVisit2(node);
	}

	@Override
	public boolean visit(final AnnotationTypeDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(final AnnotationTypeMemberDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(final AnonymousClassDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(final ArrayAccess node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ArrayCreation node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ArrayInitializer node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ArrayType node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final AssertStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final Assignment node) {
		if (this.insideMethod && this.myCurrentClass != null) {
			// System.out.print(" Assignement " + node.toString());
			// System.out.print("node.getRightHandSide()"
			// + node.getRightHandSide().toString() + " operator"
			// + node.getOperator().toString());
			// System.out.println("node.getLeftHandSide()"
			// + node.getLeftHandSide());
			this.checkFieldAccess("Assignment", MethodInvocationUtils
					.getExpressionTypeBinding(node.getLeftHandSide()));
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(final Block node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final BlockComment node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final BooleanLiteral node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final BreakStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final CastExpression node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final CatchClause node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final CharacterLiteral node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ClassInstanceCreation node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final CompilationUnit node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ConditionalExpression node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ConstructorInvocation node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ContinueStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final DoStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final EmptyStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final EnhancedForStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final EnumConstantDeclaration node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final EnumDeclaration node) {

		return false;
	}

	@Override
	public boolean visit(final ExpressionStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final FieldAccess node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final FieldDeclaration node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ForStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final IfStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ImportDeclaration node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final InfixExpression node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final Initializer node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final InstanceofExpression node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final Javadoc node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final LabeledStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final LineComment node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final MarkerAnnotation node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final MemberRef node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final MemberValuePair node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final MethodDeclaration node) {
		// check qu'on est bien dans la classe
		if (node.getParent().getNodeType() == ASTNode.TYPE_DECLARATION
				&& this.myCurrentClass != null) {
			if (!node.resolveBinding().getDeclaringClass().getQualifiedName()
					.equals(this.myCurrentClass.getDisplayID())) {
				
				System.out
						.println("Warning in visit(final MethodDeclaration node) about parent node "
								+ node.resolveBinding().getDeclaringClass()
										.getQualifiedName()
								+ " vs "
								+ this.myCurrentClass);
			} else {

				final char[] operationID = PadlParserUtil
						.computeMethodNodeSignature(node, this.model,
								this.myCurrentPackage);
				this.myCurrentOperation = (IOperation) this.myCurrentClass
						.getConstituentFromID(operationID);

				this.insideMethod = true;
			}
		} else {
			this.insideMethod = false;
			System.out
					.println("Warning in visit(final MethodDeclaration node) about parent node");
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(final MethodInvocation node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final MethodRef node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final MethodRefParameter node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final Modifier node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final NamedCompilationUnit aNamedCompilationUnit) {

		return super.visit(aNamedCompilationUnit);
	}

	@Override
	public boolean visit(final NormalAnnotation node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final NullLiteral node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final NumberLiteral node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final PackageDeclaration node) {
		this.myCurrentPackage = PadlParserUtil.getPackage(node.getName()
				.toString(), this.model);

		return super.visit(node);
	}

	@Override
	public boolean visit(final ParameterizedType node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ParenthesizedExpression node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final PostfixExpression node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final PrefixExpression node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final PrimitiveType node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final QualifiedName node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final QualifiedType node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ReturnStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final SimpleName node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final SimpleType node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final SingleMemberAnnotation node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final SingleVariableDeclaration node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final StringLiteral node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final SuperConstructorInvocation node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final SuperFieldAccess node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final SuperMethodInvocation node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final SwitchCase node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final SwitchStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final SynchronizedStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final TagElement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final TextElement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ThisExpression node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final ThrowStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final TryStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final TypeDeclaration node) {

		if (node.resolveBinding().isClass()
				&& !node.resolveBinding().isMember()) {
			this.myCurrentClass = model.getTopLevelEntityFromID(node
					.resolveBinding().getQualifiedName());
			if (this.myCurrentClass != null) {
				this.insideMethod = false;

				this.mapOfAllDirectFieldAccess = new HashMap<String, Set<String>>();
				this.mapOfTransformersDirectFieldAccess = new HashMap<String, Set<String>>();

				this.currentMadumClass = new MadumClass(
						(IClass) this.myCurrentClass);
				this.listOfMadums.add(this.currentMadumClass);

				this.currentMadumClass
						.setNumberOfFields(node.getFields().length);

				this.currentMadumClass
						.setNumberOfMethods(node.getMethods().length);

				this.currentMadumClass
						.setMapOfAllDirectFieldAccess(this.mapOfAllDirectFieldAccess);

				this.currentMadumClass
						.setMapOfTransformersDirectFieldAccess(this.mapOfTransformersDirectFieldAccess);

				

				if (node.getFields().length == 0
						|| node.getMethods().length == 0) {
					System.out
							.println("==================> Nb fileds=0 or nbMethods=0");
					return false;
				}
				return super.visit(node);
			} else {
				System.out
						.println("Warning TypeDeclaration visit --- Top level class not found in the model"
								+ node.resolveBinding().getQualifiedName());
				return false;
			}

		} else {
			return false;
		}

	}

	@Override
	public boolean visit(final TypeDeclarationStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final TypeLiteral node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final TypeParameter node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final VariableDeclarationExpression node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final VariableDeclarationFragment node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final VariableDeclarationStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final WhileStatement node) {

		return super.visit(node);
	}

	@Override
	public boolean visit(final WildcardType node) {

		return super.visit(node);
	}

	@Override
	public boolean visitJavaFilePath(final String javaFilePath) {

		return super.visitJavaFilePath(javaFilePath);
	}

}

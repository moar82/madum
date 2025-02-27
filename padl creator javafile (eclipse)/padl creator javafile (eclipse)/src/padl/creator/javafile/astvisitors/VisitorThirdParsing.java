package padl.creator.javafile.astVisitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
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
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
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
import padl.kernel.Constants;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IEntity;
import padl.kernel.IField;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IMethodInvocation;
import padl.kernel.IOperation;
import padl.kernel.IPackage;
import padl.kernel.IPrimitiveEntity;
import padl.kernel.exception.ModelDeclarationException;
import parser.wrapper.ExtendedASTVisitor;
import parser.wrapper.NamedCompilationUnit;
import util.io.Output;

public class VisitorThirdParsing extends ExtendedASTVisitor {

	private long entityNb = 0;

	// Padl model attribute
	private final ICodeLevelModel padlModel;

	// Buffer Attributes
	private IPackage myCurrentPackage;
	private IFirstClassEntity myCurrentEntity;
	private IOperation myCurrentOperation;
	private final List<String> listOfVisitedEntities;
	private final List<String> listOfVisitedMemberEntities;

	// Stack for managing member entities
	private final Stack<IFirstClassEntity> entitiesStack =
		new Stack<IFirstClassEntity>();

	public VisitorThirdParsing(final ICodeLevelModel aCodeLevelModel) {

		System.out.println("Third parsing starting...");
		this.listOfVisitedEntities = new ArrayList<String>();
		this.listOfVisitedMemberEntities = new ArrayList<String>();
		this.padlModel = aCodeLevelModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .AnnotationTypeDeclaration)
	 */
	@Override
	public void endVisit(final AnnotationTypeDeclaration node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .AnnotationTypeMemberDeclaration)
	 */
	@Override
	public void endVisit(final AnnotationTypeMemberDeclaration node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .AnonymousClassDeclaration)
	 */
	@Override
	public void endVisit(final AnonymousClassDeclaration node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ArrayAccess)
	 */
	@Override
	public void endVisit(final ArrayAccess node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ArrayCreation)
	 */
	@Override
	public void endVisit(final ArrayCreation node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ArrayInitializer)
	 */
	@Override
	public void endVisit(final ArrayInitializer node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ArrayType)
	 */
	@Override
	public void endVisit(final ArrayType node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .AssertStatement)
	 */
	@Override
	public void endVisit(final AssertStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .Assignment)
	 */
	@Override
	public void endVisit(final Assignment node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .Block)
	 */
	@Override
	public void endVisit(final Block node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .BlockComment)
	 */
	@Override
	public void endVisit(final BlockComment node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .BooleanLiteral)
	 */
	@Override
	public void endVisit(final BooleanLiteral node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .BreakStatement)
	 */
	@Override
	public void endVisit(final BreakStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .CastExpression)
	 */
	@Override
	public void endVisit(final CastExpression node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .CatchClause)
	 */
	@Override
	public void endVisit(final CatchClause node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .CharacterLiteral)
	 */
	@Override
	public void endVisit(final CharacterLiteral node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ClassInstanceCreation)
	 */
	@Override
	public void endVisit(final ClassInstanceCreation node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .CompilationUnit)
	 */
	@Override
	public void endVisit(final CompilationUnit node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ConditionalExpression)
	 */
	@Override
	public void endVisit(final ConditionalExpression node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ConstructorInvocation)
	 */
	@Override
	public void endVisit(final ConstructorInvocation node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ContinueStatement)
	 */
	@Override
	public void endVisit(final ContinueStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .DoStatement)
	 */
	@Override
	public void endVisit(final DoStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .EmptyStatement)
	 */
	@Override
	public void endVisit(final EmptyStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .EnhancedForStatement)
	 */
	@Override
	public void endVisit(final EnhancedForStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .EnumConstantDeclaration)
	 */
	@Override
	public void endVisit(final EnumConstantDeclaration node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .EnumDeclaration)
	 */
	@Override
	public void endVisit(final EnumDeclaration node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ExpressionStatement)
	 */
	@Override
	public void endVisit(final ExpressionStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .FieldAccess)
	 */
	@Override
	public void endVisit(final FieldAccess node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .FieldDeclaration)
	 */
	@Override
	public void endVisit(final FieldDeclaration node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ForStatement)
	 */
	@Override
	public void endVisit(final ForStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .IfStatement)
	 */
	@Override
	public void endVisit(final IfStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ImportDeclaration)
	 */
	@Override
	public void endVisit(final ImportDeclaration node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .InfixExpression)
	 */
	@Override
	public void endVisit(final InfixExpression node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .Initializer)
	 */
	@Override
	public void endVisit(final Initializer node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .InstanceofExpression)
	 */
	@Override
	public void endVisit(final InstanceofExpression node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .Javadoc)
	 */
	@Override
	public void endVisit(final Javadoc node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .LabeledStatement)
	 */
	@Override
	public void endVisit(final LabeledStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .LineComment)
	 */
	@Override
	public void endVisit(final LineComment node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .MarkerAnnotation)
	 */
	@Override
	public void endVisit(final MarkerAnnotation node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .MemberRef)
	 */
	@Override
	public void endVisit(final MemberRef node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .MemberValuePair)
	 */
	@Override
	public void endVisit(final MemberValuePair node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .MethodDeclaration)
	 */
	@Override
	public void endVisit(final MethodDeclaration node) {

		if (this.myCurrentOperation != null) {
			final char[] operationID =
				PadlParserUtil.computeMethodNodeSignature(
					node,
					this.padlModel,
					this.myCurrentPackage);
			if (Arrays.equals(this.myCurrentOperation.getID(), operationID)) {
				this.myCurrentOperation = null;
			}

		}
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .MethodInvocation)
	 */
	@Override
	public void endVisit(final MethodInvocation node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .MethodRef)
	 */
	@Override
	public void endVisit(final MethodRef node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .MethodRefParameter)
	 */
	@Override
	public void endVisit(final MethodRefParameter node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .Modifier)
	 */
	@Override
	public void endVisit(final Modifier node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeparser.wrapper.ExtendedASTVisitor#endVisit(parser.wrapper.
	 * NamedCompilationUnit)
	 */
	@Override
	public void endVisit(final NamedCompilationUnit aNamedCompilationUnit) {
		super.endVisit(aNamedCompilationUnit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .NormalAnnotation)
	 */
	@Override
	public void endVisit(final NormalAnnotation node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .NullLiteral)
	 */
	@Override
	public void endVisit(final NullLiteral node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .NumberLiteral)
	 */
	@Override
	public void endVisit(final NumberLiteral node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .PackageDeclaration)
	 */
	@Override
	public void endVisit(final PackageDeclaration node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ParameterizedType)
	 */
	@Override
	public void endVisit(final ParameterizedType node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ParenthesizedExpression)
	 */
	@Override
	public void endVisit(final ParenthesizedExpression node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .PostfixExpression)
	 */
	@Override
	public void endVisit(final PostfixExpression node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .PrefixExpression)
	 */
	@Override
	public void endVisit(final PrefixExpression node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .PrimitiveType)
	 */
	@Override
	public void endVisit(final PrimitiveType node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .QualifiedName)
	 */
	@Override
	public void endVisit(final QualifiedName node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .QualifiedType)
	 */
	@Override
	public void endVisit(final QualifiedType node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ReturnStatement)
	 */
	@Override
	public void endVisit(final ReturnStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .SimpleName)
	 */
	@Override
	public void endVisit(final SimpleName node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .SimpleType)
	 */
	@Override
	public void endVisit(final SimpleType node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .SingleMemberAnnotation)
	 */
	@Override
	public void endVisit(final SingleMemberAnnotation node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .SingleVariableDeclaration)
	 */
	@Override
	public void endVisit(final SingleVariableDeclaration node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .StringLiteral)
	 */
	@Override
	public void endVisit(final StringLiteral node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .SuperConstructorInvocation)
	 */
	@Override
	public void endVisit(final SuperConstructorInvocation node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .SuperFieldAccess)
	 */
	@Override
	public void endVisit(final SuperFieldAccess node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .SuperMethodInvocation)
	 */
	@Override
	public void endVisit(final SuperMethodInvocation node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .SwitchCase)
	 */
	@Override
	public void endVisit(final SwitchCase node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .SwitchStatement)
	 */
	@Override
	public void endVisit(final SwitchStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .SynchronizedStatement)
	 */
	@Override
	public void endVisit(final SynchronizedStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .TagElement)
	 */
	@Override
	public void endVisit(final TagElement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .TextElement)
	 */
	@Override
	public void endVisit(final TextElement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ThisExpression)
	 */
	@Override
	public void endVisit(final ThisExpression node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .ThrowStatement)
	 */
	@Override
	public void endVisit(final ThrowStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .TryStatement)
	 */
	@Override
	public void endVisit(final TryStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .TypeDeclaration)
	 */
	@Override
	public void endVisit(final TypeDeclaration node) {
		if (this.myCurrentEntity != null && node.resolveBinding() != null) {
			final String nodeId =
				PadlParserUtil.renameWith$(node
					.resolveBinding()
					.getQualifiedName(), node
					.resolveBinding()
					.getPackage()
					.getName());
			// check if it is really the end of the current entity

			if (this.myCurrentEntity.getDisplayID().equals(nodeId)) {

				if (!this.entitiesStack.empty()) {
					// this means that it is a memberInterface
					// or memberClass take the top of the stack as a current
					// entity

					this.myCurrentEntity = this.entitiesStack.pop();

				}
				else {
					// For the case where there is one public class and many
					// others classes in the same file but those classes are not
					// members of the first
					this.myCurrentEntity = null;

				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .TypeDeclarationStatement)
	 */
	@Override
	public void endVisit(final TypeDeclarationStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .TypeLiteral)
	 */
	@Override
	public void endVisit(final TypeLiteral node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .TypeParameter)
	 */
	@Override
	public void endVisit(final TypeParameter node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .VariableDeclarationExpression)
	 */
	@Override
	public void endVisit(final VariableDeclarationExpression node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .VariableDeclarationFragment)
	 */
	@Override
	public void endVisit(final VariableDeclarationFragment node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .VariableDeclarationStatement)
	 */
	@Override
	public void endVisit(final VariableDeclarationStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .WhileStatement)
	 */
	@Override
	public void endVisit(final WhileStatement node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom
	 * .WildcardType)
	 */
	@Override
	public void endVisit(final WildcardType node) {
		super.endVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * parser.wrapper.ExtendedASTVisitor#endVisitJavaFilePath(java.lang.String)
	 */
	@Override
	public void endVisitJavaFilePath(final String javaFilePath) {
		// System.out.println("javaFilePath " + javaFilePath);
		super.endVisitJavaFilePath(javaFilePath);
	}

	/**
	 * 
	 * @return
	 */
	public ICodeLevelModel getPadlModel() {
		return this.padlModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#postVisit(org.eclipse.jdt.core.dom
	 * .ASTNode)
	 */
	@Override
	public void postVisit(final ASTNode node) {
		super.postVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#preVisit(org.eclipse.jdt.core.dom
	 * .ASTNode)
	 */
	@Override
	public void preVisit(final ASTNode node) {
		super.preVisit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#preVisit2(org.eclipse.jdt.core.dom
	 * .ASTNode)
	 */
	@Override
	public boolean preVisit2(final ASTNode node) {
		return super.preVisit2(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * AnnotationTypeDeclaration)
	 */
	@Override
	public boolean visit(final AnnotationTypeDeclaration node) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * AnnotationTypeMemberDeclaration)
	 */
	@Override
	public boolean visit(final AnnotationTypeMemberDeclaration node) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * AnonymousClassDeclaration)
	 */
	@Override
	public boolean visit(final AnonymousClassDeclaration node) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ArrayAccess)
	 */
	@Override
	public boolean visit(final ArrayAccess node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ArrayCreation)
	 */
	@Override
	public boolean visit(final ArrayCreation node) {
		if (this.myCurrentOperation == null) {
			return false;
		}

		final ITypeBinding typeBinding = node.resolveTypeBinding();

		final int cardinality = Constants.CARDINALITY_MANY;
		final int visibility = this.myCurrentOperation.getVisibility();

		final IEntity targetEntity =
			PadlParserUtil.getEntityOrGetOrCreateGhost(
				false,
				typeBinding,
				this.padlModel,
				this.myCurrentPackage.getDisplayID());
		if (!(targetEntity instanceof IPrimitiveEntity)) {
			try {

				final IMethodInvocation methodInvocation =
					this.padlModel.getFactory().createMethodInvocation(
						IMethodInvocation.INSTANCE_CREATION,
						cardinality,
						visibility,
						(IFirstClassEntity) targetEntity,
						null);

				methodInvocation.setComment(node.toString());

				this.myCurrentOperation.addConstituent(methodInvocation);

			}
			catch (final ModelDeclarationException e) {
				e.printStackTrace(Output.getInstance().errorOutput());
			}
		}
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ArrayInitializer)
	 */
	@Override
	public boolean visit(final ArrayInitializer node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ArrayType
	 * )
	 */
	@Override
	public boolean visit(final ArrayType node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * AssertStatement)
	 */
	@Override
	public boolean visit(final AssertStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Assignment
	 * )
	 */
	@Override
	public boolean visit(final Assignment node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Block)
	 */
	@Override
	public boolean visit(final Block node) {

		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * BlockComment)
	 */
	@Override
	public boolean visit(final BlockComment node) {

		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * BooleanLiteral)
	 */
	@Override
	public boolean visit(final BooleanLiteral node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * BreakStatement)
	 */
	@Override
	public boolean visit(final BreakStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * CastExpression)
	 */
	@Override
	public boolean visit(final CastExpression node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * CatchClause)
	 */
	@Override
	public boolean visit(final CatchClause node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * CharacterLiteral)
	 */
	@Override
	public boolean visit(final CharacterLiteral node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ClassInstanceCreation)
	 */
	@Override
	public boolean visit(final ClassInstanceCreation node) {
		if (this.myCurrentOperation == null) {
			return false;
		}

		final ITypeBinding typeBinding = node.resolveTypeBinding();
		if (typeBinding != null) {
			final IFirstClassEntity targetEntity =
				(IFirstClassEntity) PadlParserUtil.getEntityOrGetOrCreateGhost(
					false,
					typeBinding,
					this.padlModel,
					this.myCurrentPackage.getDisplayID());

			final int cardinality = PadlParserUtil.getDim(typeBinding);
			final int visibility = this.myCurrentOperation.getVisibility();

			java.lang.reflect.Modifier.toString(9);

			try {

				final IMethodInvocation methodInvocation =
					this.padlModel.getFactory().createMethodInvocation(
						IMethodInvocation.INSTANCE_CREATION,
						cardinality,
						visibility,
						targetEntity,
						null);

				methodInvocation.setComment(node.toString());

				this.myCurrentOperation.addConstituent(methodInvocation);

			}
			catch (final ModelDeclarationException e) {
				e.printStackTrace(Output.getInstance().errorOutput());
			}
		}
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * CompilationUnit)
	 */
	@Override
	public boolean visit(final CompilationUnit node) {
		// Initialization of buffer variables
		this.myCurrentPackage = null;
		this.myCurrentEntity = null;
		this.myCurrentOperation = null;
		return super.visit(node);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ConditionalExpression)
	 */
	@Override
	public boolean visit(final ConditionalExpression node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ConstructorInvocation)
	 */
	@Override
	public boolean visit(final ConstructorInvocation node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ContinueStatement)
	 */
	@Override
	public boolean visit(final ContinueStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * DoStatement)
	 */
	@Override
	public boolean visit(final DoStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * EmptyStatement)
	 */
	@Override
	public boolean visit(final EmptyStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * EnhancedForStatement)
	 */
	@Override
	public boolean visit(final EnhancedForStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * EnumConstantDeclaration)
	 */
	@Override
	public boolean visit(final EnumConstantDeclaration node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * EnumDeclaration)
	 */
	@Override
	public boolean visit(final EnumDeclaration node) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ExpressionStatement)
	 */
	@Override
	public boolean visit(final ExpressionStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * FieldAccess)
	 */
	@Override
	public boolean visit(final FieldAccess node) {
		if (this.myCurrentOperation == null) {
			return false;
		}

		// final ITypeBinding typeBinding = node.resolveTypeBinding();
		// System.out.println(typeBinding);
		//
		// final int cardinality = PadlParserUtil.getDim(typeBinding);
		// final int visibility = this.myCurrentOperation.getVisibility();
		//
		// final IFirstClassEntity targetEntity = (IFirstClassEntity)
		// PadlParserUtil
		// .getEntityOrGetOrCreateGhost(false, typeBinding,
		// this.padlModel, this.myCurrentPackage.getDisplayID());
		// final int type = -1;
		// System.out.println(node.getExpression().toString());
		// // if(this.myCurrentOperation.isStatic()){
		// // if(node.getExpression())
		// // }
		// if (!(targetEntity instanceof IPrimitiveEntity)) {
		// try {
		//
		// final IMethodInvocation methodInvocation = this.padlModel
		// .getFactory().createMethodInvocation(
		// IMethodInvocation.INSTANCE_CREATION,
		// cardinality, visibility, targetEntity, null);
		//
		// methodInvocation.setComment(node.toString());
		//
		// this.myCurrentOperation.addConstituent(methodInvocation);
		//
		// } catch (final ModelDeclarationException e) {
		// e.printStackTrace(Output.getInstance().errorOutput());
		// }
		// }
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * FieldDeclaration)
	 */
	@Override
	public boolean visit(final FieldDeclaration node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ForStatement)
	 */
	@Override
	public boolean visit(final ForStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * IfStatement)
	 */
	@Override
	public boolean visit(final IfStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ImportDeclaration)
	 */
	@Override
	public boolean visit(final ImportDeclaration node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * InfixExpression)
	 */
	@Override
	public boolean visit(final InfixExpression node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * Initializer)
	 */
	@Override
	public boolean visit(final Initializer node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * InstanceofExpression)
	 */
	@Override
	public boolean visit(final InstanceofExpression node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Javadoc
	 * )
	 */
	@Override
	public boolean visit(final Javadoc node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * LabeledStatement)
	 */
	@Override
	public boolean visit(final LabeledStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * LineComment)
	 */
	@Override
	public boolean visit(final LineComment node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * MarkerAnnotation)
	 */
	@Override
	public boolean visit(final MarkerAnnotation node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MemberRef
	 * )
	 */
	@Override
	public boolean visit(final MemberRef node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * MemberValuePair)
	 */
	@Override
	public boolean visit(final MemberValuePair node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * MethodDeclaration)
	 */
	@Override
	public boolean visit(final MethodDeclaration node) {
		if (node.getParent().getNodeType() == ASTNode.TYPE_DECLARATION
				&& this.myCurrentEntity != null) {

			final char[] operationID =
				PadlParserUtil.computeMethodNodeSignature(
					node,
					this.padlModel,
					this.myCurrentPackage);
			this.myCurrentOperation =
				(IOperation) this.myCurrentEntity
					.getConstituentFromID(operationID);

			return true;
		}
		else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * MethodInvocation)
	 */
	@Override
	public boolean visit(final MethodInvocation node) {
		// Other conditions???!!!
		// How to be sure that we are in the current method and not in a method
		// of an anonymous class that is in this method
		if (this.myCurrentOperation == null) {
			return false;
		}

		final IMethodBinding methodBinding = node.resolveMethodBinding();
		final Expression expression = node.getExpression();

		// visibility of the method invocation corresponds to the visibility of
		// the calling method

		if (methodBinding != null) {
			try {
				IMethodInvocation methodInvocation = null;

				final boolean invocationThroughField =
					MethodInvocationUtils.isInvocationThroughField(expression);
				final int type =
					MethodInvocationUtils.getMethodInvocationType(
						this.myCurrentOperation.getVisibility(),
						methodBinding.getModifiers(),
						invocationThroughField);

				final int visibility = this.myCurrentOperation.getVisibility();
				int cardinality = Constants.CARDINALITY_ONE;

				if (expression != null) {
					// if the expression on what the invocation is performed is
					// an
					// array
					// or a list, the cardinality is MANY 
					if (expression instanceof ArrayAccess) {
						cardinality = Constants.CARDINALITY_MANY;
					}
					else if (expression.resolveTypeBinding() != null) {
						cardinality =
							PadlParserUtil.getDim(expression
								.resolveTypeBinding());
					}
					// targetEntity is the type of the expression on what the
					// invocation
					// is performed and not the class that declare the called
					// method
					final IFirstClassEntity targetEntity =
						(IFirstClassEntity) PadlParserUtil
							.getEntityOrGetOrCreateGhost(
								false,
								expression.resolveTypeBinding(),
								this.padlModel,
								this.myCurrentPackage.getDisplayID());
					if (targetEntity == null) {
						//This is the case for example of a method m2 defined in a class C defined in a method m1
						//and this method m2 is invoked somewhere else in the method m1 (out of the class)
						//we will not take into account this kind of method invocation  because we don't have information about this class C
						// and it is not relevant for our purpose
						return false;
					}
					IFirstClassEntity fieldDeclaringEntity = null;
					//if (invocationThroughField ) {
					//02022012- Aminata S
					//changed to take into account the fact that for called static method, the field is not required in the byte code
					if (invocationThroughField
							&& type != IMethodInvocation.CLASS_CLASS && type != IMethodInvocation.INSTANCE_CLASS) {
						// get declaring class if possible
						// if the type is field, add the class where this
						// field is declared, take
						// declaring class and search in padl, if found ok if
						// not create it as a ghost
						fieldDeclaringEntity =
							MethodInvocationUtils.getFieldDeclaringEntity(
								this.padlModel,
								expression,
								this.myCurrentEntity,
								this.myCurrentPackage.getDisplayID());

					}
					methodInvocation =
						this.padlModel.getFactory().createMethodInvocation(
							type,
							cardinality,
							visibility,
							targetEntity,
							fieldDeclaringEntity);

					//if (invocationThroughField ) {
					//02022012- Aminata S
					//changed to take into account the fact that for called static method, the field is not required in the byte code
					if (invocationThroughField
							&& type != IMethodInvocation.CLASS_CLASS && type != IMethodInvocation.INSTANCE_CLASS) {
						final IField invocationField =
							MethodInvocationUtils.getFieldInvocation(
								this.padlModel,
								expression,
								fieldDeclaringEntity);
						final List<IField> listCallingFields =
							new ArrayList<IField>();
						listCallingFields.add(invocationField);
						methodInvocation.setCallingField(listCallingFields);
					}

				}
				else {
					// if the expression is null, the target entity is the
					// current entity
					methodInvocation =
						this.padlModel.getFactory().createMethodInvocation(
							type,
							cardinality,
							visibility,
							this.myCurrentEntity,
							null);
				}

				methodInvocation.setComment(node.toString());

				// search in the target entity this method if it does not
				// exist, search in its superclasses, if not add it into the
				// last superclasses as add it in the ghost as we did for field
				final IOperation calledMethod =
					MethodInvocationUtils.getCalledMethod(
						methodBinding,
						this.padlModel,
						this.myCurrentPackage.getDisplayPath());
				//if (calledMethod != null) {
				methodInvocation.setCalledMethod(calledMethod);
				//}

				this.myCurrentOperation.addConstituent(methodInvocation);
			}
			catch (final ModelDeclarationException e) {

				e.printStackTrace();
			}

		}
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodRef
	 * )
	 */
	@Override
	public boolean visit(final MethodRef node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * MethodRefParameter)
	 */
	@Override
	public boolean visit(final MethodRefParameter node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Modifier
	 * )
	 */
	@Override
	public boolean visit(final Modifier node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * parser.wrapper.ExtendedASTVisitor#visit(parser.wrapper.NamedCompilationUnit
	 * )
	 */
	@Override
	public boolean visit(final NamedCompilationUnit aNamedCompilationUnit) {
		return super.visit(aNamedCompilationUnit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * NormalAnnotation)
	 */
	@Override
	public boolean visit(final NormalAnnotation node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * NullLiteral)
	 */
	@Override
	public boolean visit(final NullLiteral node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * NumberLiteral)
	 */
	@Override
	public boolean visit(final NumberLiteral node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * PackageDeclaration)
	 */
	@Override
	public boolean visit(final PackageDeclaration node) {
		this.myCurrentPackage =
			PadlParserUtil
				.getPackage(node.getName().toString(), this.padlModel);

		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ParameterizedType)
	 */
	@Override
	public boolean visit(final ParameterizedType node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ParenthesizedExpression)
	 */
	@Override
	public boolean visit(final ParenthesizedExpression node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * PostfixExpression)
	 */
	@Override
	public boolean visit(final PostfixExpression node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * PrefixExpression)
	 */
	@Override
	public boolean visit(final PrefixExpression node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * PrimitiveType)
	 */
	@Override
	public boolean visit(final PrimitiveType node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * QualifiedName)
	 */
	@Override
	public boolean visit(final QualifiedName node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * QualifiedType)
	 */
	@Override
	public boolean visit(final QualifiedType node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ReturnStatement)
	 */
	@Override
	public boolean visit(final ReturnStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SimpleName
	 * )
	 */
	@Override
	public boolean visit(final SimpleName node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SimpleType
	 * )
	 */
	@Override
	public boolean visit(final SimpleType node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * SingleMemberAnnotation)
	 */
	@Override
	public boolean visit(final SingleMemberAnnotation node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * SingleVariableDeclaration)
	 */
	@Override
	public boolean visit(final SingleVariableDeclaration node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * StringLiteral)
	 */
	@Override
	public boolean visit(final StringLiteral node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * SuperConstructorInvocation)
	 */
	@Override
	public boolean visit(final SuperConstructorInvocation node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * SuperFieldAccess)
	 */
	@Override
	public boolean visit(final SuperFieldAccess node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * SuperMethodInvocation)
	 */
	@Override
	public boolean visit(final SuperMethodInvocation node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SwitchCase
	 * )
	 */
	@Override
	public boolean visit(final SwitchCase node) {

		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * SwitchStatement)
	 */
	@Override
	public boolean visit(final SwitchStatement node) {

		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * SynchronizedStatement)
	 */
	@Override
	public boolean visit(final SynchronizedStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TagElement
	 * )
	 */
	@Override
	public boolean visit(final TagElement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * TextElement)
	 */
	@Override
	public boolean visit(final TextElement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ThisExpression)
	 */
	@Override
	public boolean visit(final ThisExpression node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * ThrowStatement)
	 */
	@Override
	public boolean visit(final ThrowStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * TryStatement)
	 */
	@Override
	public boolean visit(final TryStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * TypeDeclaration)
	 */
	@Override
	public boolean visit(final TypeDeclaration node) {
		if (node.resolveBinding() == null) {
			// Fix for eclipse_12-15-2009 (fixed) ok see in
			// VisitorFirstParsing
			return false;
		}
		final boolean b1 =
			node.getParent().getNodeType() != ASTNode.TYPE_DECLARATION;
		final boolean b2 =
			node.getParent().getNodeType() != ASTNode.COMPILATION_UNIT;

		if (node.getParent().getNodeType() != ASTNode.TYPE_DECLARATION
				&& node.getParent().getNodeType() != ASTNode.COMPILATION_UNIT

		) {

			return false;
		}

		this.entityNb++;
//		if (this.entityNb % 1000 == 0) {
//			System.out.println("visit(final CompilationUnit node) entit้ "
//					+ this.entityNb + " "
//					+ node.resolveBinding().getQualifiedName());
//
//		}
		String qualifiedName = node.resolveBinding().getQualifiedName();
		final String simpleName = node.getName().toString();

		if (this.myCurrentEntity == null) {

			if (this.listOfVisitedEntities.contains(qualifiedName)) {
				// another class with the same id has already been visited
				return false;
			}

			if (this.myCurrentPackage == null) {
				this.myCurrentPackage =
					(IPackage) this.padlModel
						.getConstituentFromID(Constants.DEFAULT_PACKAGE_ID);
			}
			this.listOfVisitedEntities.add(qualifiedName);
			this.myCurrentEntity =
				(IFirstClassEntity) this.myCurrentPackage
					.getConstituentFromID(qualifiedName.toCharArray());
			if (this.myCurrentEntity == null) {
				return false;
			}

			this.listOfVisitedMemberEntities.clear();
		}
		else {
			// class member
			// id of a class member - replace the . by $
			qualifiedName =
				this.myCurrentEntity.getDisplayID() + "$" + simpleName;

			if (this.listOfVisitedMemberEntities.contains(qualifiedName)) {
				// another memberClass with the same id has already been
				// visited
				return false;
			}

			final IFirstClassEntity memberEntity =
				(IFirstClassEntity) this.myCurrentEntity
					.getConstituentFromID(qualifiedName.toCharArray());
			this.listOfVisitedMemberEntities.add(qualifiedName);
			if (memberEntity == null) {// this should not be happened
				return false;
			}

			this.entitiesStack.addElement(this.myCurrentEntity);
			this.myCurrentEntity = memberEntity;
		}

		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * TypeDeclarationStatement)
	 */
	@Override
	public boolean visit(final TypeDeclarationStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * TypeLiteral)
	 */
	@Override
	public boolean visit(final TypeLiteral node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * TypeParameter)
	 */
	@Override
	public boolean visit(final TypeParameter node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * VariableDeclarationExpression)
	 */
	@Override
	public boolean visit(final VariableDeclarationExpression node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * VariableDeclarationFragment)
	 */
	@Override
	public boolean visit(final VariableDeclarationFragment node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * VariableDeclarationStatement)
	 */
	@Override
	public boolean visit(final VariableDeclarationStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * WhileStatement)
	 */
	@Override
	public boolean visit(final WhileStatement node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * WildcardType)
	 */
	@Override
	public boolean visit(final WildcardType node) {
		return super.visit(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * parser.wrapper.ExtendedASTVisitor#visitJavaFilePath(java.lang.String)
	 */
	@Override
	public boolean visitJavaFilePath(final String javaFilePath) {
		return super.visitJavaFilePath(javaFilePath);
	}

}

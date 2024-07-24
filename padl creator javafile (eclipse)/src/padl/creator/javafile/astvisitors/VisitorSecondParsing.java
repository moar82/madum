/* (c) Copyright 2011 and following years, Aminata SABAN�,
 * �Cole Polytechnique de Montr�al.
 * 
 * @author: Aminata SABAN�
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

package padl.creator.javafile.astVisitors;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import padl.creator.javafile.util.PadlParserUtil;
import padl.kernel.Constants;
import padl.kernel.IClass;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IConstructor;
import padl.kernel.IField;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IInterfaceActor;
import padl.kernel.IMemberClass;
import padl.kernel.IMethod;
import padl.kernel.IOperation;
import padl.kernel.IPackage;
import padl.kernel.IParameter;
import padl.kernel.exception.ModelDeclarationException;
import parser.wrapper.ExtendedASTVisitor;
import util.io.Output;

public class VisitorSecondParsing extends ExtendedASTVisitor {

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

	public VisitorSecondParsing(final ICodeLevelModel aCodeLevelModel) {
		System.out.println("Second parsing starting...");
		this.listOfVisitedEntities = new ArrayList<String>();
		this.listOfVisitedMemberEntities = new ArrayList<String>();
		this.padlModel = aCodeLevelModel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public void endVisit(final CompilationUnit node) {

		super.endVisit(node);
	}

	@Override
	public void endVisit(final IfStatement node) {

		super.endVisit(node);
	}

	/**
	 * class or interface methods
	 */
	@Override
	public void endVisit(final MethodDeclaration node) {

		super.endVisit(node);
	}

	/**
	 * Package end visit
	 */
	@Override
	public void endVisit(final PackageDeclaration node) {
		super.endVisit(node);
	}

	/**
	 * Class, Interface, ClassMember or InterfaceMember Visit
	 */
	@Override
	public void endVisit(final TypeDeclaration node) {

		// Fix for eclipse_12-15-2009 (fixed)
		// happen that muCurrentEntity ==null when the visit of a class is
		// canceled for any reason
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
				// Check if the class has at least one constructor, if not add a
				// default constructor to it
				if (this.myCurrentEntity instanceof IClass
						|| this.myCurrentEntity instanceof IMemberClass) {
					int numberOfConstructors = -5;
					try {

						try {
							numberOfConstructors =
								this.myCurrentEntity
									.getNumberOfConstituents(Class
										.forName("padl.kernel.impl.Constructor"))
										- this.myCurrentEntity
											.getNumberOfConstituents(Class
												.forName("padl.kernel.impl.Method"));
						}
						catch (final ClassNotFoundException e) {

							e.printStackTrace();
						}

						if (numberOfConstructors == 0) {
							final char[] operationID =
								PadlParserUtil.computeSignature(
									"<init>",
									new ArrayList<IParameter>(),
									null,
									Modifier.PUBLIC);
							final IConstructor constructor =
								this.padlModel.getFactory().createConstructor(
									operationID,
									this.myCurrentEntity
										.getDisplayName()
										.toCharArray());

							this.myCurrentEntity.addConstituent(constructor);
						}
					}
					catch (final ModelDeclarationException e) {
						e.printStackTrace(Output.getInstance().errorOutput());
					}
				}

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
		super.endVisit(node);
	}

	/**
	 * 
	 * @return
	 */
	public ICodeLevelModel getPadlModel() {
		return this.padlModel;
	}

	private void manageInheritance(final TypeDeclaration node) {
		// Inheritance management
		// When an inheritance is not authorized like extends String, the
		// ast remplace this String by Object
		// If there is no import to resolve the type of Ghost, the AST gives
		// it as package the current package !!! must correct it?
		IFirstClassEntity superClass = null;
		if (!node.isInterface()) {
			if (node.getSuperclassType() != null) {

				// node.getSuperclassType().resolveBinding();==>
				// getQualifiedName()=D for D.M (D and M are ghosts
				// final ITypeBinding type =
				// node.resolveBinding().getSuperclass(); is retained
				// But when the D exists and M is a ghost,
				// node.resolveBinding().getSuperclass() takes as a
				// superclass
				// java.lang.Object; so in this case, we will use
				// node.getSuperclassType().resolveBinding() to have the
				// good
				// information

				ITypeBinding type = node.resolveBinding().getSuperclass();
				if (type.getQualifiedName().equals("java.lang.Object")) {
					type = node.getSuperclassType().resolveBinding();
				}
				superClass =
					(IFirstClassEntity) PadlParserUtil
						.getEntityOrGetOrCreateGhost(
							true,
							type,
							this.padlModel,
							this.myCurrentPackage.getDisplayPath());

			}
			else {
				// Add Object as a superclass
				superClass =
					this.padlModel.getTopLevelEntityFromID("java.lang.Object");

				if (superClass == null) {
					superClass =
						PadlParserUtil.createGhost(
							this.padlModel,
							"java.lang.Object".toCharArray());
				}

				if (superClass.getNumberOfConstituents(IConstructor.class)
						- superClass.getNumberOfConstituents(IMethod.class) == 0) {

					try {

						final char[] operationID =
							PadlParserUtil.computeSignature(
								"<init>",
								new ArrayList<IParameter>(),
								null,
								Modifier.PUBLIC);
						final IConstructor constructor =
							this.padlModel.getFactory().createConstructor(
								operationID,
								superClass.getDisplayName().toCharArray());
						superClass.addConstituent(constructor);
					}
					catch (final ModelDeclarationException e) {
						e.printStackTrace(Output.getInstance().errorOutput());
					}
				}

			}
			try {
				this.myCurrentEntity.addInheritedEntity(superClass);
			}
			catch (final ModelDeclarationException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * manage super interfaces
	 * 
	 * @param node
	 */
	private void manageSuperInterfaces(final TypeDeclaration node) {

		final ITypeBinding[] interfaces = node.resolveBinding().getInterfaces();

		final List interfacesList = node.superInterfaceTypes();

		try {
			if (interfaces.length != 0) {
				final int nbInterfaces = interfaces.length;

				for (int i = 0; i < nbInterfaces; i++) {
					final IFirstClassEntity interfaz =
						(IFirstClassEntity) PadlParserUtil
							.getEntityOrGetOrCreateGhost(
								false,
								interfaces[i],
								this.padlModel,
								this.myCurrentPackage.getDisplayPath());

					if (!(interfaz instanceof IInterfaceActor)) {
						// source not well done and class seems be
						// implemented by a class

						continue;// don't add this interfaz
					}
					if (this.myCurrentEntity instanceof IClass
							|| this.myCurrentEntity instanceof IMemberClass) {

						((IClass) this.myCurrentEntity)
							.addImplementedInterface((IInterfaceActor) interfaz);

					}
					else {
						this.myCurrentEntity.addInheritedEntity(interfaz);
					}

				}
			}
			else if (interfacesList.size() != 0) {
				final Iterator iter = interfacesList.iterator();
				while (iter.hasNext()) {

					final IFirstClassEntity interfaz =
						(IFirstClassEntity) PadlParserUtil
							.getEntityOrGetOrCreateGhost(
								true,
								((Type) iter.next()).resolveBinding(),
								this.padlModel,
								this.myCurrentPackage.getDisplayPath());
					if (!(interfaz instanceof IInterfaceActor)) {
						// source not well done and class seems be
						// implemented by a class

						continue;// don't add this interfaz
					}
					if (this.myCurrentEntity instanceof IClass
							|| this.myCurrentEntity instanceof IMemberClass) {

						((IClass) this.myCurrentEntity)
							.addImplementedInterface((IInterfaceActor) interfaz);

					}
					else {
						this.myCurrentEntity.addInheritedEntity(interfaz);
					}

				}

			}
		}
		catch (final ModelDeclarationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean visit(final AnnotationTypeDeclaration node) {
		return false;
	}

	public boolean visit(final AnnotationTypeMemberDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(final AnonymousClassDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(final CompilationUnit node) {
		// Initialization of buffer variables
		this.myCurrentPackage = null;
		this.myCurrentEntity = null;
		this.myCurrentOperation = null;

		return super.visit(node);
	}

	@Override
	public boolean visit(final EnumDeclaration node) {

		return false;
	}

	// Methods for limiting the visit

	@Override
	public boolean visit(final MethodDeclaration node) {
		if (node.getParent().getNodeType() != ASTNode.TYPE_DECLARATION
				|| this.myCurrentEntity == null) {
			return false;
		}
		

		final String name = node.getName().toString();
		String returnTypeName = null;
		final int modifier = node.getModifiers();

		// Yann: May be null when parsing Foutse's Eclipse data!
		// TODO: Understand why it could be null...
//		if (node.getReturnType2() != null
//				&& node.getReturnType2().resolveBinding() != null) {

			if (!node.isConstructor()) {
				// Fix for eclipse_12-15-2009 (fixed)
				if (node.getReturnType2() == null || node.getReturnType2().resolveBinding()==null) {
					// when a method is not well written, without void and
					// without a return type

				}
				else {
					returnTypeName =
						PadlParserUtil.getTypeName(node
							.getReturnType2()
							.resolveBinding(), false);
				}
			}

			char[] operationID;
			final List<IParameter> listOfParameters =
				PadlParserUtil.getListOfParameters(
					node.parameters(),
					this.myCurrentPackage,
					this.padlModel);

			try {
				if (node.isConstructor()) {

					operationID =
						PadlParserUtil.computeSignature(
							"<init>",
							listOfParameters,
							returnTypeName,
							modifier);
					this.myCurrentOperation =
						this.padlModel.getFactory().createConstructor(
							operationID,
							name.toCharArray());
				}
				else {
					operationID =
						PadlParserUtil.computeSignature(
							name,
							listOfParameters,
							returnTypeName,
							modifier);
					this.myCurrentOperation =
						this.padlModel.getFactory().createMethod(
							operationID,
							name.toCharArray());
					if (returnTypeName != null) {
						((padl.kernel.IMethod) this.myCurrentOperation)
							.setReturnType(returnTypeName.toCharArray());
					}
				}
				// don't add a method which already exist;
				// this happens when in the same class, there are 2 methods
				// with the same signature
				// in this case the parameters will not be added also
				if (!this.myCurrentEntity
					.doesContainConstituentWithID(this.myCurrentOperation
						.getID())) {
					this.myCurrentEntity
						.addConstituent(this.myCurrentOperation);

					if (!listOfParameters.isEmpty()) {
						final Iterator<IParameter> iter =
							listOfParameters.iterator();
						while (iter.hasNext()) {
							final IParameter parameter = iter.next();

							this.myCurrentOperation.addConstituent(parameter);

						}
					}

					this.myCurrentOperation.setVisibility(node.getModifiers());
				}

			}
			catch (final ModelDeclarationException e) {
				e.printStackTrace(Output.getInstance().errorOutput());
			}
//		}

		this.myCurrentOperation = null;

		return super.visit(node);
	}

	/**
	 * create or get a package from padl model
	 */
	@Override
	public boolean visit(final PackageDeclaration node) {

		this.myCurrentPackage =
			PadlParserUtil
				.getPackage(node.getName().toString(), this.padlModel);

		return super.visit(node);
	}

	@Override
	public boolean visit(final SimpleName node) {
		return super.visit(node);
	}

	/**
	 * Class, Interface, ClassMember or InterfaceMember Visit
	 */
	@Override
	public boolean visit(final TypeDeclaration node) {
		// To avoid to visit classes or interfaces which are defined in
		// methods!!!
		if (node.getParent().getNodeType() != ASTNode.TYPE_DECLARATION
				&& node.getParent().getNodeType() != ASTNode.COMPILATION_UNIT
				// Fix for eclipse_12-15-2009 (fixed) ok see in
				// VisitorFirstParsing
				|| node.resolveBinding() == null) {

			return false;
		}

		this.entityNb++;
		if (this.entityNb % 1000 == 0) {
			System.out.println("visit(final CompilationUnit node) entit� "
					+ this.entityNb + " "
					+ node.resolveBinding().getQualifiedName());

		}
		String qualifiedName = node.resolveBinding().getQualifiedName();
		final String simpleName = node.getName().toString();

		if (this.myCurrentEntity == null) {
			// means that it is a top level entity

			if (this.listOfVisitedEntities.contains(qualifiedName)) {
				// another class with the same id has already been visited
				return false;
			}
			this.listOfVisitedEntities.add(qualifiedName);
			if (this.myCurrentPackage == null) {
				this.myCurrentPackage =
					(IPackage) this.padlModel
						.getConstituentFromID(Constants.DEFAULT_PACKAGE_ID);
			}

			this.myCurrentEntity =
				(IFirstClassEntity) this.myCurrentPackage
					.getConstituentFromID(qualifiedName.toCharArray());

			//			if (this.myCurrentEntity == null) {
			//				// this should not happen (check after all tests and delete it
			//				
			//				return false;
			//			}

			this.listOfVisitedMemberEntities.clear();
		}
		else {
			// class member
			// id of a class member - replace the . by $
			qualifiedName =
				this.myCurrentEntity.getDisplayID() + "$" + simpleName;

			if (this.listOfVisitedMemberEntities.contains(qualifiedName)) {
				// another memberClass with the same id has already been visited
				return false;
			}

			this.listOfVisitedMemberEntities.add(qualifiedName);

			final IFirstClassEntity memberEntity =
				(IFirstClassEntity) this.myCurrentEntity
					.getConstituentFromID(qualifiedName.toCharArray());

			//			if (memberEntity == null) {// this should not be happened
			//				
			//				return false;
			//			}

			this.entitiesStack.addElement(this.myCurrentEntity);
			this.myCurrentEntity = memberEntity;

		}

		// manageInheritance
		this.manageInheritance(node);

		// Implemented interfaces management
		this.manageSuperInterfaces(node);

		return super.visit(node);
	}

	/**
	 * Classes or interfaces Fields
	 */
	@Override
	public boolean visit(final VariableDeclarationFragment node) {
		// check that it is a fragment of field declaration
		// is it sufficient to be sure that we are in the good class
		if (node.getParent().getNodeType() != ASTNode.FIELD_DECLARATION
				|| this.myCurrentEntity == null) {

			return false;
		}

		final ITypeBinding type =
			((FieldDeclaration) node.getParent()).getType().resolveBinding();

		// Fix for eclipse_12-15-2009 (fixed)
		if (type == null) {
			// when there are 2 fields with the same name in the same class

			return false;
		}
		else {

			final String typeName = PadlParserUtil.getTypeName(type, true);

			final String fieldName = node.getName().toString();

			final int dim = PadlParserUtil.getDim(type);

			try {
				final IField field =
					this.padlModel.getFactory().createField(
						fieldName.toCharArray(),
						fieldName.toCharArray(),
						typeName.toCharArray(),
						dim);

				field.setVisibility(((FieldDeclaration) node.getParent())
					.getModifiers());

				// avoid duplication if in the same class,
				// there is twice the same field
				if (!this.myCurrentEntity.doesContainConstituentWithID(field
					.getID())) {

					this.myCurrentEntity.addConstituent(field);
				}

			}
			catch (final ModelDeclarationException e) {
				e.printStackTrace(Output.getInstance().errorOutput());
			}

		}

		return super.visit(node);
	}
}

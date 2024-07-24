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

import java.util.Stack;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import padl.creator.javafile.util.PadlParserUtil;
import padl.kernel.Constants;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IConstituentOfEntity;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IPackage;
import padl.kernel.IPackageDefault;
import padl.kernel.exception.ModelDeclarationException;
import parser.wrapper.ExtendedASTVisitor;
import util.io.Output;

public class VisitorFirstParsing extends ExtendedASTVisitor {

	private long entityNb = 0;
	// Padl model attribute
	private final ICodeLevelModel padlModel;

	// Buffer Attributes
	private IPackage myCurrentPackage;
	private IFirstClassEntity myCurrentEntity;

	// Stack for managing member entities
	private final Stack<IFirstClassEntity> entitiesStack =
		new Stack<IFirstClassEntity>();

	public VisitorFirstParsing(final ICodeLevelModel aCodeLevelModel) {

		this.padlModel = aCodeLevelModel;

		System.out.println("debut first parsing...");

	}

	@Override
	public void endVisit(final CompilationUnit node) {
		super.endVisit(node);
	}

	@Override
	public void endVisit(final MethodDeclaration node) {

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

		// this check is needed because when a visit of a class is canceled for
		// any reason
		// this method is executed

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
				// check that the stack is empty to be sure that it is a top level class
				// or interface.
				if (!this.entitiesStack.empty()) {
					// this means that it is a memberInterface or memberClass
					// so take the current class in the top of the stack

					this.myCurrentEntity = this.entitiesStack.pop();
				}
				else {
					// For the case where there is one public class and many others
					// classes in the same file but those classes are not members of the
					// first
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

	@Override
	public boolean visit(final AnnotationTypeDeclaration node) {
		return false;
	}

	public boolean visit(final AnnotationTypeMemberDeclaration node) {
		return false;
	}

	// Methods for limiting the visit

	@Override
	public boolean visit(final AnonymousClassDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(final CompilationUnit node) {
		// Initialization of buffer variables
		this.myCurrentPackage = null;
		this.myCurrentEntity = null;
		return super.visit(node);
	}

	@Override
	public boolean visit(final EnumDeclaration node) {

		return false;
	}

	/**
	 * class or interface method or constructor
	 */
	@Override
	public boolean visit(final MethodDeclaration node) {

		return false;
	}

	/**
	 * create or get a package from padl model
	 */
	@Override
	public boolean visit(final PackageDeclaration node) {
		final char[] name = node.getName().toString().toCharArray();

		this.myCurrentPackage =
			PadlParserUtil.getOrCreatePackage(name, this.padlModel);

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
				// Fix for eclipse_12-15-2009 (case where the code is not
				// correct (two classes in the same package with the same name
				// or two member entities
				// of the same entity with the same name)
				|| node.resolveBinding() == null) {

			return false;
		}
		// any visited class
		this.entityNb++;

		if (this.entityNb % 1000 == 0) {
			System.out.println("visit(final CompilationUnit node) entit� "
					+ this.entityNb + " "
					+ node.resolveBinding().getQualifiedName());

		}
		char[] entityBindingName =
			node.resolveBinding().getQualifiedName().toCharArray();

		final String simpleName = node.getName().toString();

		// a class with this id is already in the model so we don't parse it
		if (this.padlModel.getTopLevelEntityFromID(entityBindingName) != null) {

			return false;
		}
		try {

			if (this.myCurrentEntity == null) {

				// visited class is a top level class
				if (this.myCurrentPackage == null) {
					// This means that it is a default package
					if ((this.myCurrentPackage =
						(IPackageDefault) this.padlModel
							.getConstituentFromID(Constants.DEFAULT_PACKAGE_ID)) == null) {
						this.myCurrentPackage =
							this.padlModel.getFactory().createPackageDefault();
						this.padlModel.addConstituent(this.myCurrentPackage);
					}
				}

				if (node.isInterface()) {
					this.myCurrentEntity =
						this.padlModel.getFactory().createInterface(
							entityBindingName,
							simpleName.toCharArray());
				}
				else {

					this.myCurrentEntity =
						this.padlModel.getFactory().createClass(
							entityBindingName,
							simpleName.toCharArray());

				}
				this.myCurrentPackage.addConstituent(this.myCurrentEntity);

			}
			else {
				// class member
				// id of a class member - replace the . by $
				entityBindingName =
					(this.myCurrentEntity.getDisplayID() + "$" + simpleName)
						.toCharArray();
				// if a class member with this id is already in the current
				// class we don't parse it
				if (this.myCurrentEntity
					.doesContainConstituentWithID(entityBindingName)) {

					return false;
				}

				IFirstClassEntity memberEntity;

				if (node.isInterface()) {
					memberEntity =
						this.padlModel.getFactory().createMemberInterface(
							entityBindingName,
							simpleName.toCharArray());
				}
				else {

					memberEntity =
						this.padlModel.getFactory().createMemberClass(
							entityBindingName,
							simpleName.toCharArray());
				}
				this.myCurrentEntity
					.addConstituent((IConstituentOfEntity) memberEntity);
				this.entitiesStack.addElement(this.myCurrentEntity);
				this.myCurrentEntity = memberEntity;

			}
			this.myCurrentEntity.setVisibility(node.getModifiers());
		}
		catch (final ModelDeclarationException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
		return super.visit(node);
	}
}

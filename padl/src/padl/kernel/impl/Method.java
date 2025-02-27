/*
 * (c) Copyright 2001, 2002 Herv� Albin-Amiot and Yann-Ga�l Gu�h�neuc,
 * Ecole des Mines de Nantes
 * Object Technology International, Inc.
 * Soft-Maint S.A.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the authors, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHORS ARE ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.kernel.impl;

import padl.kernel.Constants;
import padl.kernel.IElementMarker;
import padl.kernel.IMethod;
import padl.kernel.exception.ModelDeclarationException;
import util.io.Output;

class Method extends Constructor implements IElementMarker, IMethod {
	// I used to have static counters in MethodInvocation and Parameter to
	// build their IDs. I replaced these counters with the following counters
	// so that their are dependent on the method, to ease the comparison of
	// subset of models.
	//	private static int NumberOfMethodInvocations = 0;
	//	private static int NumberOfParameters = 0;
	//	static int getUniqueMethodInvocationNumber() {
	//		return Method.NumberOfMethodInvocations++;
	//	}
	//	static int getUniqueParameterNumber() {
	//		return Method.NumberOfParameters++;
	//	}
	// Yann 2006/02/24: UniqueID...
	// I need a UniqueID for IMethodInvocation and IParemeter in IMethod
	// and for any IRelationship in IEntity. So, when using the method
	// concretelyAddConstituent(), I take care of the unique ID.

	private static final long serialVersionUID = -4174549756886894781L;
	static final char[] VOID = "void".toCharArray();
	// String used instead of Class because the class may not be compiled yet.
	// TODO: Class should be available, at least as Ghost, so should remove this String...
	private char[] returnType = Method.VOID;

	public Method(final char[] anID) {
		super(anID);
	}
	public Method(final char[] anID, final IMethod attachedMethod)
			throws ModelDeclarationException {

		super(anID);
		this.attachTo(attachedMethod);
	}
	//	public Method(final String anID, final MethodInfo aMethod) {
	//		super(anID);
	//
	//		try {
	//			this.setVisibility(aMethod.getAccess());
	//		}
	//		catch (final ModelDeclarationException e) {
	//			// No error can occur.
	//		}
	//
	//		this.setReturnType(aMethod.getReturnType());
	//
	//		final String[] detectedParameters = aMethod.getParams();
	//		for (int i = 0; i < detectedParameters.length; i++) {
	//			try {
	//				this.addConstituent(
	//					new Parameter(
	//						Util.stripAndCapQualifiedName(
	//							detectedParameters[i])));
	//			}
	//			catch (final ModelDeclarationException e) {
	//			}
	//		}
	//	}
	public Method(final IMethod attachedMethod)
			throws ModelDeclarationException {

		this(attachedMethod.getID(), attachedMethod);
	}
	//	public Method(final MethodInfo aMethod) {
	//		this(aMethod.getName(), aMethod);
	//	}
	public Method(final String anID) {
		super(anID.toCharArray());
	}

	public String getDisplayReturnType() {
		return String.valueOf(this.getReturnType());
	}
	public char[] getReturnType() {
		if (this.getAttachedElement() == null) {
			return this.returnType;
		}
		return ((IMethod) this.getAttachedElement()).getReturnType();
	}
	public void setReturnType(final char[] aType) {
		this.returnType = aType;
	}
	public String toString() {
		if (Constants.DEBUG) {
			Output.getInstance().debugOutput().println("// Method.toString()");
		}
		return this.toString(0);
	}
	public String toString(final int tab) {
		if (Constants.DEBUG) {
			Output.getInstance().debugOutput().println(
				"// Method.toString(int)");
		}
		final StringBuffer codeEq = new StringBuffer();
		this.toStringStart(tab, codeEq);
		codeEq.append(this.getReturnType());
		codeEq.append(' ');
		this.toStringSignature(codeEq);
		if (isAbstract()) {
			return codeEq.toString() + ';';
		}
		this.toStringBody(tab, codeEq);
		return codeEq.toString();
	}
}

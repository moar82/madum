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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.lang.ArrayUtils;
import padl.kernel.Constants;
import padl.kernel.IConstituent;
import padl.kernel.IVisitor;
import padl.kernel.exception.ModelDeclarationException;
import padl.util.Util;
import util.io.Output;
import util.lang.Modifier;
import util.multilingual.MultilingualManager;
import com.ibm.toad.cfparse.utils.Access;

// TODO: Should we provide to people extending PADL some default
// implementation of the different Constituent of the meta-model?
// Maybe class "Constituent" could be public, or classes "Elements"
// and "Entities"?
abstract class Constituent implements IConstituent {
	private static final long serialVersionUID = -3089376780834846438L;
	private static final Map cachedAcceptClassNames = new HashMap();

	private Constituent clone;
	// Yann 2009/04/29: Useless?
	// It seems that the cloning protocol changed
	// so much that these variable are not useful.
	//	private List clonedBoundEventList = new ArrayList(0);
	//	private List clonedVetoEventList = new ArrayList(0);
	private String[] codeLines;
	private String comment;
	// Yann 2004/04/09: Why "extends AbstractContainer"?
	// From the point of view of the model (defined by the
	// interfaces in the kernel), only certain constituent
	// may hold other constituent (by implementing IContainer).
	// From the point of view of the implementation and to
	// avoid duplication, any constituent may hold other
	// constituent. This is not a problem because a model 
	// can only be manipulated through the interfaces.
	private char[] id;
	private char[] name;
	private char[] path;
	private int visibility = Access.ACC_PUBLIC;
	private int weight = Constants.MANDATORY;

	public Constituent(final char[] anID) {
		if (anID == null) {
			throw new RuntimeException(new ModelDeclarationException(
				MultilingualManager.getString(
					"ACTOR_ID_NULL",
					Constituent.class)));
		}
		this.id = anID;
		this.setName(anID);
		this.setPath(anID);
	}
	public void accept(final IVisitor visitor) {
		this.accept(visitor, "visit");
	}
	protected void accept(final IVisitor visitor, final String methodName) {
		// Yann 2010/06/21: Performance
		// The CreatorJCT spent a lot of time in this method,
		// in which the call to String.replaceAll() was
		// accounting for most of the time spent. I added a
		// cache to avoid doing the same computations again
		// and over again.
		final String className = this.getClass().getName();
		final String acceptClassName;
		if (!Constituent.cachedAcceptClassNames.containsKey(className)) {
			Constituent.cachedAcceptClassNames.put(className, className
				.replaceAll(".impl.", ".I"));
		}
		acceptClassName = (String) cachedAcceptClassNames.get(className);

		try {
			final java.lang.Class[] argument =
				new java.lang.Class[] { java.lang.Class
					.forName(acceptClassName) };

			try {
				// Yann 2003/12/05: Interfaces!
				// I must match a class a the kernel to the 
				// corresponding interface: 
				//     padl.kernel.kernel.impl.Association
				// ->
				//     padl.kernel.IAssociation
				final Method method =
					visitor.getClass().getMethod(methodName, argument);
				method.invoke(visitor, new Object[] { this });
			}
			catch (final Exception e) {
				// Yann 2004/04/10: New constituents!
				// In case I add new constituent and forget to update
				// the method in the IVisitor interface, I foward such
				// exceptions (to fail the appropriate tests).
				//	System.err.println(MultilingualManager.getString(
				//		"Exception_ACCEPT_METHOD",
				//		Constituent.class,
				//		new Object[] { methodName, acceptMethodName }));
				// e.printStackTrace();
				// Yann 2008/10/13: Fall back!
				// Following Mathieu's suggesting, the visiting of
				// an unknown constituent (by the visitor) won't
				// fail anymore but "gracefully" call an handler
				// method that should deal with it!
				// 
				if (e instanceof NoSuchMethodException) {
					visitor.unknownConstituentHandler(methodName + '('
							+ argument[0].getName() + ')', this);
				}
				else {
					throw new RuntimeException(e);
				}
			}
		}
		catch (final ClassNotFoundException e) {
			visitor.unknownConstituentHandler(methodName, this);
		}
	}
	protected final void updateWithIncomingPath(final char[] anIncomingPath) {
		// TODO: Replace with the use of an IModelListener, similar to the Listener for top-level entities :-)
		final char[] newPath =
			new char[anIncomingPath.length + 1 + this.getPath().length];
		System.arraycopy(anIncomingPath, 0, newPath, 0, anIncomingPath.length);
		newPath[anIncomingPath.length] = this.getPathSymbol();
		System.arraycopy(
			this.getPath(),
			0,
			newPath,
			anIncomingPath.length + 1,
			this.getPath().length);
		this.setPath(newPath);
	}
	protected abstract char getPathSymbol();
	public void endCloneSession() {
		this.clone = null;
		//	this.clonedBoundEventList.clear();
		//	this.clonedVetoEventList.clear();
	}
	public boolean equals(final Object obj) {
		if (!(obj instanceof IConstituent)) {
			return false;
		}
		return Arrays.equals(this.getID(), ((IConstituent) obj).getID());
	}
	public IConstituent getClone() {
		return this.clone;
	}
	public String[] getCodeLines() {
		return this.codeLines;
	}
	public String getComment() {
		return this.comment;
	}
	public String getDisplayID() {
		return String.valueOf(this.getID());
	}
	public String getDisplayName() {
		return String.valueOf(this.getName());
	}
	public String getDisplayPath() {
		return String.valueOf(this.getPath());
	}
	public char[] getID() {
		return this.id;
	}
	public char[] getName() {
		return this.name;
	}
	public char[] getPath() {
		return this.path;
	}
	public int getVisibility() {
		return this.visibility;
	}
	public int getWeight() {
		return this.weight;
	}
	public int hashCode() {
		return this.getID().hashCode();
	}
	public boolean isAbstract() {
		return Access.isAbstract(this.visibility);
	}
	public boolean isFinal() {
		return Access.isFinal(this.visibility);
	}
	public boolean isPrivate() {
		return Access.isPrivate(this.visibility);
	}
	public boolean isProtected() {
		return Access.isProtected(this.visibility);
	}
	public boolean isPublic() {
		return Access.isPublic(this.visibility);
	}
	public boolean isStatic() {
		return Access.isStatic(this.visibility);
	}
	/**
	 * This methods is used by the clone protocol.
	 */
	public void performCloneSession() {
	}

	public void resetCodeLines() throws ModelDeclarationException {
		this.setCodeLines("");
	}
	public void setAbstract(final boolean aBoolean)
			throws ModelDeclarationException {

		this
			.setVisibility(aBoolean ? (this.getVisibility() | Access.ACC_ABSTRACT)
					: (this.getVisibility() & ~Access.ACC_ABSTRACT));
	}
	public void setCodeLines(final String someCode)
			throws ModelDeclarationException {

		final List listOfLines = new ArrayList();
		final StringTokenizer tokeniser = new StringTokenizer(someCode, "\n\r");
		while (tokeniser.hasMoreTokens()) {
			final String line = (String) tokeniser.nextElement();
			listOfLines.add(line);
		}
		final String[] arrayOfLines = new String[listOfLines.size()];
		listOfLines.toArray(arrayOfLines);
		this.setCodeLines(arrayOfLines);
	}
	public void setCodeLines(String[] code) throws ModelDeclarationException {
		if (this.isAbstract()) {
			throw new ModelDeclarationException(MultilingualManager.getString(
				"ELEM_CODE_DEF",
				Constituent.class));
		}
		this.codeLines = code;
	}
	public void setComment(final String aComment) {
		this.comment = aComment;
	}
	public void setDisplayName(final String aName) {
		this.name = aName.toCharArray();
	}
	public void setFinal(final boolean aBoolean) {
		try {
			this
				.setVisibility(aBoolean ? (this.getVisibility() | Access.ACC_FINAL)
						: (this.getVisibility() & ~Access.ACC_FINAL));
		}
		catch (final ModelDeclarationException e) {
			// No error can occur.
		}
	}
	protected void setID(final char[] anID) {
		this.id = anID;
	}
	public void setName(final char[] aName) {
		this.name = aName;
	}
	protected void setPath(final char[] aPath) {
		this.path = aPath;
	}
	public void setPrivate(final boolean aBoolean) {
		try {
			this
				.setVisibility(aBoolean ? ((this.getVisibility() | Access.ACC_PRIVATE) & ~(Access.ACC_PUBLIC | Access.ACC_PROTECTED))
						: (this.getVisibility() & ~Access.ACC_PRIVATE));
		}
		catch (final ModelDeclarationException e) {
			// No error can occur.
		}
	}
	public void setProtected(final boolean aBoolean) {
		try {
			this
				.setVisibility(aBoolean ? ((this.getVisibility() | Access.ACC_PROTECTED) & ~(Access.ACC_PUBLIC | Access.ACC_PRIVATE))
						: (this.getVisibility() & ~Access.ACC_PROTECTED));
		}
		catch (final ModelDeclarationException e) {
			// No error can occur.
		}
	}
	public void setPublic(final boolean aBoolean) {
		try {
			this
				.setVisibility(aBoolean ? ((this.getVisibility() | Access.ACC_PUBLIC) & ~(Access.ACC_PRIVATE | Access.ACC_PROTECTED))
						: (this.getVisibility() & ~Access.ACC_PUBLIC));
		}
		catch (final ModelDeclarationException e) {
			// No error can occur.
		}
	}
	public void setStatic(final boolean aBoolean) {
		try {
			this
				.setVisibility(aBoolean ? (this.getVisibility() | Access.ACC_STATIC)
						: (this.getVisibility() & ~Access.ACC_STATIC));
		}
		catch (final ModelDeclarationException e) {
			// No error can occur.
		}
	}
	public void setVisibility(final int visibility)
			throws ModelDeclarationException {

		if (this.getCodeLines() != null && Access.isAbstract(visibility)) {
			// Why test getCodeLines() != null here ?
			throw new ModelDeclarationException(MultilingualManager.getString(
				"ELEM_ABSTRACT",
				Constituent.class));
		}
		this.visibility = visibility;
	}
	public void setWeight(final int weight) {
		this.weight = weight;
	}
	/**
	 * This method performs a shallow copy.
	 */
	public void startCloneSession() {
		try {
			final Constituent tmpObject = (Constituent) super.clone();
			this.clone = tmpObject;
			tmpObject.clone = null;

			// Yann 2010/10/03: Paths!
			// The "path" of each cloned constituent
			// must be reset of course because the
			// clone does not yet belong to any model.
			this.clone.setPath(ArrayUtils.EMPTY_CHAR_ARRAY);
		}
		catch (final CloneNotSupportedException cnse) {
			cnse.printStackTrace(Output.getInstance().errorOutput());
		}
	}
	public String toString() {
		return this.toString(0);
	}
	public String toString(final int tab) {
		final StringBuffer codeEq = new StringBuffer();
		Util.addTabs(tab, codeEq);
		if (this.getComment() != null) {
			codeEq.append("/* ");
			codeEq.append(this.getComment());
			codeEq.append(" */\n");
			Util.addTabs(tab, codeEq);
		}
		codeEq.append(Modifier.toString(this.getVisibility()));
		return codeEq.toString();
	}
}

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
package padl.kernel.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import padl.event.IEvent;
import padl.event.IModelListener;
import padl.kernel.IConstituent;
import padl.kernel.IPrimitiveEntity;
import padl.kernel.IVisitor;
import padl.kernel.exception.ModelDeclarationException;
import util.lang.Modifier;

public class PrimitiveEntity implements IPrimitiveEntity {
	private static final long serialVersionUID = 3604943383503049188L;

	// Yann 2010/10/10: DB4O
	// Used to be:
	//	private final char[] primitiveName;
	// I removed the final to make DB4O works...
	// TODO: Understand how to keep it final with DB4O!
	private char[] primitiveName;
	public PrimitiveEntity(final char[] aPrimitiveEntityName) {
		this.primitiveName = aPrimitiveEntityName;
	}
	public void accept(final IVisitor visitor) {
	}
	public boolean equals(final Object anotherPrimitiveEntity) {
		if (!(anotherPrimitiveEntity instanceof PrimitiveEntity)) {
			return false;
		}
		else {
			return Arrays.equals(
				this.primitiveName,
				((PrimitiveEntity) anotherPrimitiveEntity).primitiveName);
		}
	}
	public void addModelListener(final IModelListener modelListener) {
	}
	public void addModelListeners(final List aListOfModelListeners) {
	}
	public void removeModelListeners(final List aListOfModelListeners) {
	}
	public void endCloneSession() {
	}
	public void fireModelChange(final String anEventType, final IEvent anEvent) {
	}
	public IConstituent getClone() {
		return this;
	}
	public String[] getCodeLines() {
		return null;
	}
	public String getComment() {
		return null;
	}
	public String getDisplayID() {
		return String.valueOf(this.getID());
	}
	public String getDisplayName() {
		return String.valueOf(this.primitiveName);
	}
	public String getDisplayPath() {
		return String.valueOf(this.getPath());
	}
	public char[] getID() {
		return this.primitiveName;
	}
	public Iterator getIteratorOnModelListeners() {
		return null;
	}
	public char[] getName() {
		return this.primitiveName;
	}
	public char[] getPath() {
		return this.primitiveName;
	}
	public int getVisibility() {
		return Modifier.PUBLIC;
	}
	public int getWeight() {
		return 0;
	}
	public boolean isAbstract() {
		return false;
	}
	public boolean isFinal() {
		return false;
	}
	public boolean isPrivate() {
		return false;
	}
	public boolean isProtected() {
		return false;
	}
	public boolean isPublic() {
		return true;
	}
	public boolean isStatic() {
		return false;
	}
	public void performCloneSession() {
	}
	public void removeModelListener(final IModelListener modelListener) {
	}
	public void resetCodeLines() throws ModelDeclarationException {
	}
	public void setAbstract(final boolean boolean1)
			throws ModelDeclarationException {
	}
	public void setCodeLines(final String someCode)
			throws ModelDeclarationException {
	}
	public void setCodeLines(final String[] someCode)
			throws ModelDeclarationException {
	}
	public void setComment(final String comment) {
	}
	public void setDisplayName(final String name) {
	}
	public void setFinal(final boolean boolean1) {
	}
	public void setName(final char[] name) {
	}
	public void setPrivate(final boolean boolean1) {
	}
	public void setProtected(final boolean boolean1) {
	}
	public void setPublic(final boolean boolean1) {
	}
	public void setStatic(final boolean boolean1) {
	}
	public void setVisibility(final int visibility)
			throws ModelDeclarationException {
	}
	public void setWeight(final int weight) {
	}
	public void startCloneSession() {
	}
	public String toString(final int tab) {
		return this.getDisplayName();
	}
	public void updateWithIncomingPath(final char[] theIncomingPath) {
	}
}

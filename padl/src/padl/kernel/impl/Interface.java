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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import padl.kernel.IClass;
import padl.kernel.IElement;
import padl.kernel.IEntityMarker;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IInterface;
import padl.kernel.exception.ModelDeclarationException;

class Interface extends AbstractInterface implements IEntityMarker, IInterface {
	private static final long serialVersionUID = 3362527126531938666L;
	// Yann 2010/10/10: DB4O
	// Used to be:
	//	private final List shouldImplementEventList = new ArrayList();
	// I removed the final to make DB4O works...
	// TODO: Understand how to keep it final with DB4O!
	private List shouldImplementEventList = new ArrayList();

	public Interface(final char[] anID, final char[] aName) {
		super(anID, aName);
	}
	public void addConstituent(final IElement anElement)
			throws ModelDeclarationException {
		anElement.setAbstract(true);
		super.addConstituent(anElement);
	}
	public void endCloneSession() {
		super.endCloneSession();
		this.shouldImplementEventList.clear();
	}
	public void startCloneSession() {
		super.startCloneSession();

		// Duplicate hierarchy.
		final Interface clonedPInterface = (Interface) this.getClone();
		final Iterator iterator = this.shouldImplementEventList.iterator();
		while (iterator.hasNext()) {
			final IClass currentTarget = (IClass) iterator.next();
			currentTarget.removeImplementedInterface(this);
			try {
				currentTarget.addImplementedInterface(clonedPInterface);
			}
			catch (final ModelDeclarationException e) {
			}
		}
	}
	public String toString() {
		final StringBuffer codeEq = new StringBuffer();
		codeEq.append(super.toString());
		codeEq.append(" interface ");
		codeEq.append(getName());
		final Iterator iterator = this.getIteratorOnInheritedEntities();
		if (iterator.hasNext()) {
			codeEq.append(" extends ");
			while (iterator.hasNext()) {
				codeEq
					.append(((IFirstClassEntity) (iterator.next())).getName());
				if (iterator.hasNext())
					codeEq.append(", ");
			}
		}
		return codeEq.toString();
	}
}

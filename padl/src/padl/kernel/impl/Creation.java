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

import padl.kernel.ICreation;
import padl.kernel.IElementMarker;
import padl.kernel.IFirstClassEntity;
import padl.kernel.exception.ModelDeclarationException;

class Creation
	extends UseRelationship
	implements IElementMarker, ICreation {

	private static final long serialVersionUID = -3036096916992766588L;

	public Creation(
		final char[] anID,
		final IFirstClassEntity targetPEntity,
		final int cardinality)
		throws ModelDeclarationException {

		super(anID, targetPEntity, cardinality);
	}
}

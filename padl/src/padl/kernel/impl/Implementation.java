/*
 * (c) Copyright 2001-2004 Yann-Ga�l Gu�h�neuc,
 * University of Montr�al.
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

import padl.kernel.IElementMarker;
import padl.kernel.IFirstClassEntity;
import padl.kernel.ISpecialisation;
import padl.kernel.exception.ModelDeclarationException;

/**
 * @author Yann-Ga�l Gu�h�neuc
 * @since  2004/08/15
 */
class Implementation
	extends Relationship
	implements IElementMarker, ISpecialisation {

	private static final long serialVersionUID = 8431739221482018770L;

	public Implementation(
		final char[] anID,
		final IFirstClassEntity aTargetEntity)
		throws ModelDeclarationException {

		super(anID);
		this.setTargetEntity(aTargetEntity);
	}
}

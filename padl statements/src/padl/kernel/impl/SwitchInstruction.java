/* (c) Copyright 2001 and following years, Yann-Ga�l Gu�h�neuc,
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

import padl.kernel.ISwitchInstruction;

class SwitchInstruction extends Conditional implements ISwitchInstruction {
	private static final long serialVersionUID = 7691868702663850443L;
	private static final char[] SWITCH_INSTRUCTION =
		"Switch Instruction".toCharArray();
	private final int range;

	public SwitchInstruction(final int aRange) {
		super(SwitchInstruction.SWITCH_INSTRUCTION);
		this.range = aRange;
	}
	public int getRange() {
		return this.range;
	}
}

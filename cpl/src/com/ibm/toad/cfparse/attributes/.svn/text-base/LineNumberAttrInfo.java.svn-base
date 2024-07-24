/**
 * Duplicate of class from CFParse library to
 * allow public access to method read().
 */

package com.ibm.toad.cfparse.attributes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.ibm.toad.cfparse.ConstantPool;
import com.ibm.toad.utils.D;

public final class LineNumberAttrInfo extends AttrInfo {
	public void setLineNumber(int i, int j) {
		if (i < 0 || i >= d_numVars) {
			return;
		}
		else {
			d_varTable[2 * i + 1] = j;
			return;
		}
	}
	public int getLineNumber(int i) {
		if (i < 0 || i >= d_numVars)
			return -1;
		else
			return d_varTable[2 * i + 1];
	}
	public String toString() {
		StringBuffer stringbuffer =
			new StringBuffer(
				sindent()
					+ "Attribute: "
					+ super.d_cp.getAsString(super.d_idxName)
					+ ": \n");
		for (int i = 0; i < d_numVars; i++) {
			int j = d_varTable[i * 2];
			int k = d_varTable[i * 2 + 1];
			stringbuffer.append(sindent() + "  line " + k + ": " + j + "\n");
		}
		return stringbuffer.toString();
	}
	LineNumberAttrInfo(ConstantPool constantpool, int i, int j) {
		super(constantpool, i, j);
		d_numVars = 0;
	}
	public void read(DataInputStream datainputstream) throws IOException {
		super.d_len = datainputstream.readInt();
		d_numVars = datainputstream.readShort();
		D.assert(
			super.d_len == 2 + d_numVars * 4,
			"d_len != 2 + (d_numVars * 4)\n"
				+ super.d_len
				+ " != 2 + ("
				+ d_numVars
				+ "* 4)\n");
		d_varTable = new int[d_numVars * 2];
		for (int i = 0; i < d_varTable.length; i++)
			d_varTable[i] = datainputstream.readShort();
	}
	public void add(int i, int j) {
		for (int k = 0; k < d_numVars; k++)
			if (d_varTable[2 * k] == j) {
				d_varTable[2 * k + 1] = i;
				return;
			}
		if (d_varTable == null || d_numVars * 2 >= d_varTable.length)
			resize();
		d_varTable[d_numVars * 2] = j;
		d_varTable[d_numVars * 2 + 1] = i;
		d_numVars++;
	}
	protected int size() {
		return 8 + d_numVars * 4;
	}
	private void resize() {
		int ai[] = new int[d_numVars * 2 + 10];
		if (d_varTable != null)
			System.arraycopy(d_varTable, 0, ai, 0, d_numVars * 2);
		d_varTable = ai;
	}
	public int getStartPC(int i) {
		if (i < 0 || i >= d_numVars)
			return -1;
		else
			return d_varTable[2 * i];
	}
	public void setStartPC(int i, int j) {
		if (i < 0 || i >= d_numVars) {
			return;
		}
		else {
			d_varTable[2 * i] = j;
			return;
		}
	}
	protected void write(DataOutputStream dataoutputstream)
		throws IOException {
		dataoutputstream.writeShort(super.d_idxName);
		dataoutputstream.writeInt(2 + d_numVars * 4);
		dataoutputstream.writeShort(d_numVars);
		for (int i = 0; i < d_numVars * 2; i++)
			dataoutputstream.writeShort(d_varTable[i]);
	}
	public void clear() {
		d_numVars = 0;
		d_varTable = null;
	}
	public int length() {
		return d_numVars;
	}
	private int d_numVars;
	private int d_varTable[];
}

/***** DECOMPILATION REPORT *****

	DECOMPILED FROM: C:\Documents and Settings\Yann\Workspaces 2\Ptidej\CPL\cfparse.jar


	TOTAL TIME: 60 ms


	JAD REPORTED MESSAGES/ERRORS:

Method setLineNumber
Method getLineNumber
Method toString
Method <init>
Method read
Method add
Method size
Method resize
Method getStartPC
Method setStartPC
Method write
Method clear
Method length

	EXIT STATUS:	0


	CAUGHT EXCEPTIONS:

 ********************************/
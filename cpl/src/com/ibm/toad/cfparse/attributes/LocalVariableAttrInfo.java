/**
 * Duplicate of class from CFParse library to
 * allow public access to method read().
 */

package com.ibm.toad.cfparse.attributes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

import com.ibm.toad.cfparse.ConstantPool;
import com.ibm.toad.cfparse.utils.CPUtils;
import com.ibm.toad.utils.D;

public final class LocalVariableAttrInfo extends AttrInfo {
	public int getEndPC(int i) {
		if (i < 0 || i >= d_numVars) {
			return -1;
		}
		else {
			int j = d_varTable[5 * i];
			int k = d_varTable[5 * i + 1];
			return j + k;
		}
	}
	public String toString() {
		StringBuffer stringbuffer =
			new StringBuffer(
				sindent()
					+ "Attribute: "
					+ super.d_cp.getAsString(super.d_idxName)
					+ ": \n");
		for (int i = 0; i < d_varTable.length;) {
			int j = d_varTable[i++];
			int k = d_varTable[i++];
			int l = d_varTable[i++];
			int i1 = d_varTable[i++];
			int j1 = d_varTable[i++];
			String s = CPUtils.internal2java(super.d_cp.getAsString(i1));
			stringbuffer.append(
				sindent()
					+ "  "
					+ s
					+ " "
					+ super.d_cp.getAsString(l)
					+ " pc="
					+ j
					+ " length="
					+ k
					+ " slot="
					+ j1
					+ "\n");
		}
		return stringbuffer.toString();
	}
	LocalVariableAttrInfo(ConstantPool constantpool, int i, int j) {
		super(constantpool, i, j);
	}
	public void read(DataInputStream datainputstream) throws IOException {
		super.d_len = datainputstream.readInt();
		d_numVars = datainputstream.readShort();
		D.assert(
			super.d_len == 2 + d_numVars * 10,
			"d_len != 2 + (d_numVars * 10)\n"
				+ super.d_len
				+ " != 2 + ("
				+ d_numVars
				+ "* 10)\n");
		d_varTable = new int[d_numVars * 5];
		for (int i = 0; i < d_varTable.length; i++)
			d_varTable[i] = datainputstream.readShort();
	}
	protected void sort(int ai[]) {
		super.sort(ai);
		for (int i = 0; i < d_varTable.length; i++)
			if (i % 5 == 2 || i % 5 == 3)
				d_varTable[i] = ai[d_varTable[i]];
	}
	public String getVarType(int i) {
		if (i < 0 || i >= d_numVars) {
			return "";
		}
		else {
			int j = d_varTable[5 * i + 3];
			return CPUtils.internal2java(super.d_cp.getAsString(j));
		}
	}
	public String getVarName(int i) {
		if (i < 0 || i >= d_numVars) {
			return "";
		}
		else {
			int j = d_varTable[5 * i + 2];
			return super.d_cp.getAsString(j);
		}
	}
	public int getStartPC(int i) {
		if (i < 0 || i >= d_numVars) {
			return -1;
		}
		else {
			int j = d_varTable[5 * i];
			return j;
		}
	}
	protected void write(DataOutputStream dataoutputstream)
		throws IOException {
		dataoutputstream.writeShort(super.d_idxName);
		dataoutputstream.writeInt(super.d_len);
		dataoutputstream.writeShort(d_numVars);
		for (int i = 0; i < d_varTable.length; i++)
			dataoutputstream.writeShort(d_varTable[i]);
	}
	public int getVarNum(int i) {
		if (i < 0 || i >= d_numVars) {
			return -1;
		}
		else {
			int j = d_varTable[5 * i + 4];
			return j;
		}
	}
	protected BitSet uses() {
		BitSet bitset = super.uses();
		for (int i = 0; i < d_varTable.length; i++)
			if (i % 5 == 2 || i % 5 == 3)
				bitset.set(d_varTable[i]);
		return bitset;
	}
	public int length() {
		return d_numVars;
	}
	private int d_numVars;
	private int d_varTable[];
}

/***** DECOMPILATION REPORT *****

	DECOMPILED FROM: C:\Documents and Settings\Yann\Workspaces 2\Ptidej\CPL\cfparse.jar


	TOTAL TIME: 70 ms


	JAD REPORTED MESSAGES/ERRORS:

Method getEndPC
Method toString
Method <init>
Method read
Method sort
Method getVarType
Method getVarName
Method getStartPC
Method write
Method getVarNum
Method uses
Method length

	EXIT STATUS:	0


	CAUGHT EXCEPTIONS:

 ********************************/
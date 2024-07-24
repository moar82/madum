/**
 * Duplicate of class from CFParse library to
 * fix some problems with initialisers when 
 * adding fields .
 */

package com.ibm.toad.cfparse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Vector;

import com.ibm.toad.cfparse.attributes.AttrInfoList;
import com.ibm.toad.cfparse.attributes.CodeAttrInfo;
import com.ibm.toad.cfparse.attributes.ConstantValueAttrInfo;
import com.ibm.toad.cfparse.instruction.BaseInstruction;
import com.ibm.toad.cfparse.instruction.InstructionFormatException;
import com.ibm.toad.cfparse.instruction.MutableCodeSegment;
import com.ibm.toad.cfparse.instruction.StringInstructionFactory;
import com.ibm.toad.cfparse.utils.Access;
import com.ibm.toad.cfparse.utils.BadJavaError;
import com.ibm.toad.cfparse.utils.CPUtils;

public final class FieldInfo {
	public String toString() {
		final StringBuffer stringbuffer = new StringBuffer();
		if (Access.isPublic(d_accessFlags)) {
			stringbuffer.append("public ");
		}
		else if (Access.isPrivate(d_accessFlags)) {
			stringbuffer.append("private ");
		}
		else if (Access.isProtected(d_accessFlags)) {
			stringbuffer.append("protected ");
		}
		else if (Access.isFinal(d_accessFlags)) {
			stringbuffer.append("final ");
		}
		else if (Access.isStatic(d_accessFlags)) {
			stringbuffer.append("static ");
		}
		else if (Access.isVolatile(d_accessFlags)) {
			stringbuffer.append("volatile ");
		}
		else if (Access.isTransient(d_accessFlags)) {
			stringbuffer.append("transient ");
		}

		final String s = d_cp.getAsString(d_idxDescriptor);
		final String s1 = CPUtils.internal2java(s);
		stringbuffer.append(s1);
		stringbuffer.append(' ');
		stringbuffer.append(d_cp.getAsString(d_idxName));
		stringbuffer.append(";\n");
		stringbuffer.append(d_attrs);

		return stringbuffer.toString();
	}
	FieldInfo(final ConstantPool constantpool) {
		d_cp = constantpool;
		d_accessFlags = -1;
		d_idxName = -1;
		d_idxDescriptor = -1;
		d_attrs = new AttrInfoList(constantpool, 2);
	}
	FieldInfo(
		final ClassFile classfile,
		final ConstantPool constantpool,
		String s) {

		d_cp = constantpool;
		s = s.trim();
		d_accessFlags = Access.getFromString(s);
		if (!Access.isStatic(d_accessFlags))
			throw new BadJavaError("Incorrect method for non-static initialiser");
		int i;
		int j;
		for (i = 0;(j = s.indexOf(" ", i)) != -1; i = j + 1)
			if (!Access.isFlag(s.substring(i, j).trim()))
				break;
		s = s.substring(i, s.length());
		int k = s.indexOf(" ");
		if (k == -1)
			throw new BadJavaError("Bad Field Description : no whitespace");
		String s1 = s.substring(0, k).trim();
		int l = 0;
		StringBuffer stringbuffer = new StringBuffer();
		String s2 = s.substring(k + 1).trim();
		if (!Character.isJavaIdentifierStart(s2.charAt(0)))
			throw new BadJavaError("Bad Field Description : Invalid field identifier");
		stringbuffer.append(s2.charAt(0));
		for (int i1 = 1; i1 < s2.length(); i1++) {
			if (Character.isJavaIdentifierPart(s2.charAt(i1))) {
				stringbuffer.append(s2.charAt(i1));
				continue;
			}
			l = i1;
			break;
		}
		if (l == 0)
			throw new BadJavaError("Bad Field Description : Static definition with no initialiser");
		String s3 = stringbuffer.toString();
		if (s3.equals(""))
			throw new BadJavaError("Bad Field Description : no identifier");
		String s4 = CPUtils.java2internal(s1);
		d_idxName = constantpool.find(1, s3);
		if (d_idxName == -1)
			d_idxName = constantpool.addUtf8(s3);
		d_idxDescriptor = constantpool.find(1, s4);
		if (d_idxDescriptor == -1)
			d_idxDescriptor = constantpool.addUtf8(s4);
		d_attrs = new AttrInfoList(constantpool, 2);
		String s5 = s2.substring(l).trim();
		if (s5.charAt(0) != '=')
			throw new BadJavaError("Bad Field Description: Incorrect initializer");
		s5 = s5.substring(1).trim();
		if (Access.isFinal(d_accessFlags)) {
			ConstantValueAttrInfo constantvalueattrinfo =
				(ConstantValueAttrInfo) d_attrs.add("ConstantValue");
			if (s4.equals("B")) {
				byte byte0 = Byte.parseByte(s5);
				constantvalueattrinfo.set(byte0);
			}
			else if (s4.equals("C")) {
				if (s5.charAt(0) != '\'')
					throw new BadJavaError("Bad Field Description: unquoted character");
				constantvalueattrinfo.set(s5.charAt(1));
			}
			else if (s4.equals("D")) {
				double d = Double.parseDouble(s5);
				constantvalueattrinfo.set(d);
			}
			else if (s4.equals("F")) {
				float f = Float.parseFloat(s5);
				constantvalueattrinfo.set(f);
			}
			else if (s4.equals("I")) {
				int j1 = Integer.parseInt(s5);
				constantvalueattrinfo.set(j1);
			}
			else if (s4.equals("J")) {
				long l1 = Long.parseLong(s5);
				constantvalueattrinfo.set(l1);
			}
			else if (s4.equals("S")) {
				short word0 = Short.parseShort(s5);
				constantvalueattrinfo.set(word0);
			}
			else if (s4.equals("Z")) {
				boolean flag = Boolean.valueOf(s5).booleanValue();
				constantvalueattrinfo.set(flag ? 1 : 0);
			}
			else if (s4.equals("Ljava/lang/String;")) {
				if (!s5.equals("null")) {
					if (s5.charAt(0) != '"'
						|| s5.charAt(s5.length() - 1) != '"')
						throw new BadJavaError("Bad Field Description: unquoted String");
					constantvalueattrinfo.set(
						s5.substring(1, s5.length() - 1));
				}
			}
			else if (s4.charAt(0) == 'L') {
				//    BaseInstruction baseinstruction8 = mutablecodesegment.create("ldc " + s5);
				//    vector.addElement(baseinstruction8);
				//    baseinstruction8 = mutablecodesegment.create("putstatic java.lang.String " + classfile.getName() + "." + s3);
				//    vector.addElement(baseinstruction8);
			}
			else if (s4.charAt(0) == '[') {
				//    BaseInstruction baseinstruction8 = mutablecodesegment.create("ldc " + s5);
				//    vector.addElement(baseinstruction8);
				//    baseinstruction8 = mutablecodesegment.create("putstatic java.lang.String " + classfile.getName() + "." + s3);
				//    vector.addElement(baseinstruction8);
			}
			else {
				throw new BadJavaError(
					"Cannot initialise final variable of type " + s1);
			}
		}
		else {
			MethodInfoList methodinfolist = classfile.getMethods();
			MethodInfo methodinfo = null;
			for (int k1 = 0; k1 < methodinfolist.length(); k1++) {
				methodinfo = methodinfolist.get(k1);
				if (methodinfo.getName().equals("<clinit>"))
					break;
			}
			if (methodinfo == null)
				methodinfo = methodinfolist.add("static void <clinit>()");
			CodeAttrInfo codeattrinfo =
				(CodeAttrInfo) methodinfo.getAttrs().get("Code");
			MutableCodeSegment mutablecodesegment =
				new MutableCodeSegment(d_cp, codeattrinfo, false);
			mutablecodesegment.setInstructionFactory(
				new StringInstructionFactory());
			Vector vector = mutablecodesegment.getInstructions();

			if (vector.lastElement().toString().trim().equals("return")) {
				vector.removeElement(vector.lastElement());
			}

			try {
				if (s4.equals("B")) {
					byte byte1 = Byte.parseByte(s5);
					BaseInstruction baseinstruction =
						mutablecodesegment.create("bipush #" + byte1);
					vector.addElement(baseinstruction);
					baseinstruction =
						mutablecodesegment.create(
							"putstatic byte "
								+ classfile.getName()
								+ "."
								+ s3);
					vector.addElement(baseinstruction);
				}
				else if (s4.equals("C")) {
					if (s5.charAt(0) != '\'')
						throw new BadJavaError("Bad Field Description: unquoted character");
					BaseInstruction baseinstruction1 =
						mutablecodesegment.create("bipush #" + s5.charAt(0));
					vector.addElement(baseinstruction1);
					baseinstruction1 =
						mutablecodesegment.create(
							"putstatic char "
								+ classfile.getName()
								+ "."
								+ s3);
					vector.addElement(baseinstruction1);
				}
				else if (s4.equals("D")) {
					double d1 = Double.parseDouble(s5);
					BaseInstruction baseinstruction2 =
						mutablecodesegment.create("ldc2_w D" + d1);
					vector.addElement(baseinstruction2);
					baseinstruction2 =
						mutablecodesegment.create(
							"putstatic double "
								+ classfile.getName()
								+ "."
								+ s3);
					vector.addElement(baseinstruction2);
				}
				else if (s4.equals("F")) {
					float f1 = Float.parseFloat(s5);
					BaseInstruction baseinstruction3 =
						mutablecodesegment.create("ldc F" + f1);
					vector.addElement(baseinstruction3);
					baseinstruction3 =
						mutablecodesegment.create(
							"putstatic float "
								+ classfile.getName()
								+ "."
								+ s3);
					vector.addElement(baseinstruction3);
				}
				else if (s4.equals("I")) {
					int i2 = Integer.parseInt(s5);
					BaseInstruction baseinstruction4 =
						mutablecodesegment.create("bipush #" + i2);
					vector.addElement(baseinstruction4);
					baseinstruction4 =
						mutablecodesegment.create(
							"putstatic int "
								+ classfile.getName()
								+ "."
								+ s3);
					vector.addElement(baseinstruction4);
				}
				else if (s4.equals("J")) {
					long l2 = Long.parseLong(s5);
					BaseInstruction baseinstruction5 =
						mutablecodesegment.create("ldc2_w L" + l2);
					vector.addElement(baseinstruction5);
					baseinstruction5 =
						mutablecodesegment.create(
							"putstatic long "
								+ classfile.getName()
								+ "."
								+ s3);
					vector.addElement(baseinstruction5);
				}
				else if (s4.equals("S")) {
					short word1 = Short.parseShort(s5);
					BaseInstruction baseinstruction6 =
						mutablecodesegment.create("bipush #" + word1);
					vector.addElement(baseinstruction6);
					baseinstruction6 =
						mutablecodesegment.create(
							"putstatic short "
								+ classfile.getName()
								+ "."
								+ s3);
					vector.addElement(baseinstruction6);
				}
				else if (s4.equals("Z")) {
					boolean flag1 = Boolean.valueOf(s5).booleanValue();
					BaseInstruction baseinstruction7 =
						mutablecodesegment.create(
							flag1 ? "iconst_1" : "iconst_0");
					vector.addElement(baseinstruction7);
					baseinstruction7 =
						mutablecodesegment.create(
							"putstatic boolean "
								+ classfile.getName()
								+ "."
								+ s3);
					vector.addElement(baseinstruction7);
				}
				else if (s4.equals("Ljava/lang/String;")) {
					BaseInstruction baseinstruction8 =
						mutablecodesegment.create("ldc " + s5);
					vector.addElement(baseinstruction8);
					baseinstruction8 =
						mutablecodesegment.create(
							"putstatic java.lang.String "
								+ classfile.getName()
								+ "."
								+ s3);
					vector.addElement(baseinstruction8);
				}
				else if (s4.charAt(0) == 'L') {
					BaseInstruction baseinstruction8 =
						mutablecodesegment.create("aconst_null");
					vector.addElement(baseinstruction8);
					baseinstruction8 =
						mutablecodesegment.create(
							"putstatic "
								+ s1
								+ " "
								+ classfile.getName()
								+ "."
								+ s3);
					vector.addElement(baseinstruction8);
				}
				else if (s4.charAt(0) == '[') {
					//    BaseInstruction baseinstruction8 = mutablecodesegment.create("ldc " + s5);
					//    vector.addElement(baseinstruction8);
					//    baseinstruction8 = mutablecodesegment.create("putstatic java.lang.String " + classfile.getName() + "." + s3);
					//    vector.addElement(baseinstruction8);
				}
				else {
					throw new BadJavaError(
						"Cannot initialise variable of type " + s1);
				}
				vector.addElement(mutablecodesegment.create("return"));
			}
			catch (NumberFormatException _ex) {
				throw new BadJavaError("Unparsed initialiser <" + s5 + ">");
			}
			catch (final InstructionFormatException instructionformatexception) {
				throw new BadJavaError(
					"Bad Instruction in static initialiser <"
						+ instructionformatexception
						+ ">");
			}
			codeattrinfo.setCode(mutablecodesegment.getCode());
			codeattrinfo.setExceptions(mutablecodesegment.getExcTable());
		}
	}
	FieldInfo(ConstantPool constantpool, String s) {
		d_cp = constantpool;
		s = s.trim();
		d_accessFlags = Access.getFromString(s);
		int i;
		int j;
		for (i = 0;(j = s.indexOf(" ", i)) != -1; i = j + 1)
			if (!Access.isFlag(s.substring(i, j).trim()))
				break;
		s = s.substring(i, s.length());
		int k = s.indexOf(" ");
		if (k == -1)
			throw new BadJavaError("Bad Field Description");
		String s1 = s.substring(0, k).trim();
		int l = 0;
		StringBuffer stringbuffer = new StringBuffer();
		String s2 = s.substring(k + 1).trim();
		if (!Character.isJavaIdentifierStart(s2.charAt(0)))
			throw new BadJavaError("Invalid field identifier");
		for (int i1 = 1; i1 < s2.length(); i1++) {
			if (Character.isJavaIdentifierPart(s2.charAt(i1))) {
				stringbuffer.append(s2.charAt(i1));
				continue;
			}
			l = i1;
			break;
		}
		if (Access.isStatic(d_accessFlags) && l > 0)
			throw new BadJavaError("Incorrect method for static initialiser");
		String s3 = stringbuffer.toString();
		if (s3.equals(""))
			throw new BadJavaError("Bad Field Description");
		String s4 = CPUtils.java2internal(s1);
		d_idxName = constantpool.find(1, s3);
		if (d_idxName == -1)
			d_idxName = constantpool.addUtf8(s3);
		d_idxDescriptor = constantpool.find(1, s4);
		if (d_idxDescriptor == -1)
			d_idxDescriptor = constantpool.addUtf8(s4);
		d_attrs = new AttrInfoList(constantpool, 2);
	}
	void read(DataInputStream datainputstream) throws IOException {
		d_accessFlags = datainputstream.readShort();
		d_idxName = datainputstream.readShort();
		d_idxDescriptor = datainputstream.readShort();
		d_attrs.read(datainputstream);
	}
	public int getAccess() {
		return d_accessFlags;
	}
	void sort(int ai[]) {
		d_idxName = ai[d_idxName];
		d_idxDescriptor = ai[d_idxDescriptor];
		d_attrs.sort(ai);
	}
	public void setAccess(int i) {
		d_accessFlags = i;
	}
	public String getDesc() {
		return d_cp.getAsString(d_idxDescriptor);
	}
	void write(DataOutputStream dataoutputstream) throws IOException {
		dataoutputstream.writeShort(d_accessFlags);
		dataoutputstream.writeShort(d_idxName);
		dataoutputstream.writeShort(d_idxDescriptor);
		d_attrs.write(dataoutputstream);
	}
	public AttrInfoList getAttrs() {
		return d_attrs;
	}
	public void setAttrs(AttrInfoList attrinfolist) {
		d_attrs = attrinfolist;
	}
	BitSet uses() {
		BitSet bitset = new BitSet(d_cp.length());
		bitset.set(d_idxName);
		bitset.set(d_idxDescriptor);
		bitset.or(d_attrs.uses());
		return bitset;
	}
	public String getName() {
		return d_cp.getAsString(d_idxName);
	}
	public String getType() {
		return CPUtils.internal2java(d_cp.getAsString(d_idxDescriptor));
	}
	public void setName(String s) {
		((ConstantPool.Utf8Entry) d_cp.get(d_idxName)).setValue(s);
	}
	public void setType(String s) {
		String s1 = CPUtils.java2internal(s);
		((ConstantPool.Utf8Entry) d_cp.get(d_idxDescriptor)).setValue(s1);
	}
	private ConstantPool d_cp;
	private int d_accessFlags;
	private int d_idxName;
	private int d_idxDescriptor;
	private AttrInfoList d_attrs;
}

/***** DECOMPILATION REPORT *****

	DECOMPILED FROM: C:\Documents and Settings\Yann\Workspaces 2\Ptidej\CPL\cfparse.jar


	TOTAL TIME: 120 ms


	JAD REPORTED MESSAGES/ERRORS:

Method toString
Method <init>
Method <init>
Method <init>
Method read
Method getAccess
Method sort
Method setAccess
Method getDesc
Method write
Method getAttrs
Method setAttrs
Method uses
Method getName
Method getType
Method setName
Method setType

	EXIT STATUS:	0


	CAUGHT EXCEPTIONS:

 ********************************/
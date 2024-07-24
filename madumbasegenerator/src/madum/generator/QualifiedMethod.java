/* (c) Copyright 2009 and following years, Aminata SABANE,
 * Ecole Polytechnique de Montr̩al.
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
package madum.generator;

public class QualifiedMethod {
	
	private String methodId;
	private int methodAccessType;
	
	
	
	
	
	/**
	 * 
	 * @param aMethodId
	 * @param aMethodAccessType
	 */
	public QualifiedMethod(String aMethodId, int aMethodAccessType) {
		this.methodId = aMethodId;
		this.methodAccessType = aMethodAccessType;
	}

	/**
	 * 
	 * @return
	 */
	public String getMethodId() {
		return this.methodId;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMethodAccessType() {
		return this.methodAccessType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.methodAccessType;
		result =
			prime * result
					+ ((this.methodId == null) ? 0 : this.methodId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QualifiedMethod other = (QualifiedMethod) obj;
		if (this.methodAccessType != other.methodAccessType)
			return false;
		if (this.methodId == null) {
			if (other.methodId != null)
				return false;
		}
		else if (!this.methodId.equals(other.methodId))
			return false;
		return true;
	}
	
	

}

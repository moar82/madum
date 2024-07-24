/*
 * (c) Copyright 2001-2008 Yann-Ga�l Gu�h�neuc, Nelson Cabral
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
package padl.util.repository.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarException;
import java.util.jar.JarFile;
import padl.IFileRepository;
import util.io.NamedInputStream;

/**
 * @author Yann-Ga�l Gu�h�neuc 
 * @since 2009/03/03
 */
public class JarFileRepository implements IFileRepository {
	private static String JarFile;
	private static JarFileRepository UniqueInstance;
	public static JarFileRepository getInstance(final String aJARFile) {
		if (JarFileRepository.UniqueInstance == null
				|| JarFileRepository.JarFile != aJARFile) {

			JarFileRepository.JarFile = aJARFile;
			JarFileRepository.UniqueInstance =
				new JarFileRepository(JarFileRepository.JarFile);
		}
		return JarFileRepository.UniqueInstance;
	}

	private final NamedInputStream[] fileStreams;
	public NamedInputStream[] getFiles() {
		return this.fileStreams;
	}
	public String toString() {
		return this.fileStreams.length + " files in repository.";
	}
	private JarFileRepository(final String aJARFile) {
		final List listOfStreams = new ArrayList();
		try {
			final JarFile jarFile = new JarFile(aJARFile);
			final Enumeration entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				final JarEntry entry = (JarEntry) entries.nextElement();
				listOfStreams.add(new NamedInputStream(entry.getName(), jarFile
					.getInputStream(entry)));
			}
		}
		catch (final JarException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		this.fileStreams = new NamedInputStream[listOfStreams.size()];
		listOfStreams.toArray(this.fileStreams);
	}
}

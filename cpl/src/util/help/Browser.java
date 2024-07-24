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
package util.help;

import java.io.IOException;

import util.io.Output;
import util.multilingual.MultilingualManager;

/**
 * @author Steven Spencer
 * @link   http://www.javaworld.com/javaworld/javatips/jw-javatip66.html  
 *
 * A simple, static class to display a URL in the system browser.
 *
 * Under Unix, the system browser is hard-coded to be 'netscape'.
 * Netscape must be in your PATH for this to work.  This has been
 * tested with the following platforms: AIX, HP-UX and Solaris.
 *
 * Under Windows, this will bring up the default browser under windows,
 * usually either Netscape or Microsoft IE.  The default browser is
 * determined by the OS.  This has been tested under Windows 95/98/NT.
 *
 * Examples:
 * BrowserControl.displayURL("http://www.javaworld.com")
 * BrowserControl.displayURL("file://c:\\docs\\index.html")
 * BrowserContorl.displayURL("file:///user/joe/index.html");
 * 
 * Note - you must include the url type -- either "http://"
 * or "file://".
 */
public class Browser {
	// The flag to display a url.
	private static final String UNIX_FLAG = "-remote openURL";
	// The default browser under unix.
	private static final String UNIX_PATH = "firefox";
	// The flag to display a url.
	private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
	// Used to identify the windows platform.
	private static final String WIN_ID = "Windows";
	// The default system browser under windows.
	private static final String WIN_PATH = "rundll32";

	/**
	 * Display a file in the system browser.  If you want to display a
	 * file, you must include the absolute path name.
	 *
	 * @param url the file's url (the url must start with either "http://"
	 * or "file://").
	 */
	public static void displayURL(String url) {

		final String os = System.getProperty("os.name");
		// Modification Done by Mohamed Kahla
		// 12-08-2006-H1013
		System.out.println("Os : " + os);

		// TODO : optimization could be done 

		final boolean windows = Browser.isWindowsPlatform();
		String cmd = null;
		try {
			if (windows) {
				// cmd = 'rundll32 url.dll,FileProtocolHandler http://...'
				cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
				Runtime.getRuntime().exec(cmd);
			}

			// Under Mac Os X we run the url whith the default Browser
			else if (os.equals("Mac OS X")) {
				System.out.println("Os detected : " + os);

				// we invoke the command string : "open -a /Applications/Safari.app [url]"
				cmd = "open -a /Applications/Safari.app" + " " + url;
				Runtime.getRuntime().exec(cmd);
			}

			// TODO : i did not deleted the debug messages
			// TODO : the else suppose that ther is no other operating System 
			// but the code is specific for Unix or maybe Linux 
			// so in my openion we should test by the os name as i do for MAc OS X
			// Mohamed kahla
			// 12-08-2006- H1032

			else {
				// Under Unix, Netscape has to be running for the "-remote"
				// command to work.  So, we try sending the command and
				// check for an exit value.  If the exit command is 0,
				// it worked, otherwise we need to start the browser.
				// cmd = 'netscape -remote openURL(http://www.javaworld.com)'
				cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
				Process p = Runtime.getRuntime().exec(cmd);
				try {
					// wait for exit code -- if it's 0, command worked,
					// otherwise we need to start the browser up.
					final int exitCode = p.waitFor();
					if (exitCode != 0) {
						// Command failed, start up the browser
						// cmd = 'netscape http://www.javaworld.com'
						cmd = UNIX_PATH + " " + url;
						Runtime.getRuntime().exec(cmd);
					}
				}
				catch (final InterruptedException x) {
					Output.getInstance().errorOutput().print(
						MultilingualManager.getString(
							"Err_BRINGING_BROWSER",
							Browser.class));
					Output.getInstance().errorOutput().println(cmd);
					x.printStackTrace(Output.getInstance().errorOutput());
				}
			}
		}
		catch (final IOException x) {
			// couldn't exec browser
			Output.getInstance().errorOutput().print(
				MultilingualManager.getString(
					"Err_INVOKE_BROWSER",
					Browser.class));
			Output.getInstance().errorOutput().println(cmd);
			x.printStackTrace(Output.getInstance().errorOutput());
		}
	}

	/**
	 * Try to determine whether this application is running under Windows
	 * or some other platform by examing the "os.name" property.
	 *
	 * @return true if this application is running under a Windows OS
	 */
	public static boolean isWindowsPlatform() {
		final String os = System.getProperty("os.name");

		// Modification Done by Mohamed Kahla
		// 12-08-2006-H1013

		if (os != null && os.startsWith(WIN_ID)) {
			return true;
		}
		return false;
	}

	/**
	 * Simple example.
	 */
	public static void main(final String[] args) {
		Browser.displayURL(
			MultilingualManager.getString("BROWSER_URL", Browser.class));
	}
}

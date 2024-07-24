/*
 * (c) Copyright 2001-2002 Yann-Ga�l Gu�h�neuc,
 * Ecole des Mines de Nantes and Object Technology International, Inc.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * Yann-Ga�l Gu�h�neuc, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF YANN-GAEL GUEHENEUC IS
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package util.awt;

/**
 * Utility class to catch window close events 
 * on a target window and to dispose the window.
 */

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowCloser implements WindowListener {
	private boolean exitOnClose;

	/**
	 * Create an adaptor to listen for window closing events
	 * on the given window and actually perform the close.
	 */
	public WindowCloser(Window w) {
		this(w, false);
	}
	/**
	 * Create an adaptor to listen for window closing events
	 * on the given window and actually perform the close.
	 * If "exitOnClose" is true we do a System.exit on close.
	 */
	public WindowCloser(Window w, boolean exitOnClose) {
		this.exitOnClose = exitOnClose;
		w.addWindowListener(this);
	}
	public void windowActivated(WindowEvent e) {
	}
	public void windowClosed(WindowEvent e) {
		if (this.exitOnClose) {
			System.exit(0);
		}
	}
	public void windowClosing(WindowEvent e) {
		if (this.exitOnClose) {
			System.exit(0);
		}
		e.getWindow().dispose();
	}
	public void windowDeactivated(WindowEvent e) {
	}
	public void windowDeiconified(WindowEvent e) {
	}
	public void windowIconified(WindowEvent e) {
	}
	public void windowOpened(WindowEvent e) {
	}
}

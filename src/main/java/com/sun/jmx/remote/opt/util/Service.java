/*
 * @(#)Service.java	1.3
 *
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL")(collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://opendmk.dev.java.net/legal_notices/licenses.txt or in the
 * LEGAL_NOTICES folder that accompanied this code. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file found at
 *     http://opendmk.dev.java.net/legal_notices/licenses.txt
 * or in the LEGAL_NOTICES folder that accompanied this code.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.
 *
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 *
 *       "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding
 *
 *       "[Contributor] elects to include this software in this distribution
 *        under the [CDDL or GPL Version 2] license."
 *
 * If you don't indicate a single choice of license, a recipient has the option
 * to distribute your version of this file under either the CDDL or the GPL
 * Version 2, or to extend the choice of license to its licensees as provided
 * above. However, if you add GPL Version 2 code and therefore, elected the
 * GPL Version 2 license, then the option applies only if the new code is made
 * subject to such option by the copyright holder.
 *
 */
package com.sun.jmx.remote.opt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * EXTRACTED FROM sun.misc.Service
 * A simple service-provider lookup mechanism.  A <i>service</i> is a
 * well-known set of intjavax.management.remoteerfaces and (usually abstract) classes.  A <i>service
 * provider</i> is a specific implementation of a service.  The classes in a
 * provider typically implement the interfaces and subclass the classes defined
 * in the service itself.  Service providers may be installed in an
 * implementation of the Java platform in the form of extensions, that is, jar
 * files placed into any of the usual extension directories.  Providers may
 * also be made available by adding them to the applet or application class
 * path or by some other platform-specific means.
 * <p> In this lookup mechanism a service is represented by an interface or an
 * abstract class.  (A concrete class may be used, but this is not
 * recommended.)  A provider of a given service contains one or more concrete
 * classes that extend this <i>service class</i> with data and code specific to
 * the provider.  This <i>provider class</i> will typically not be the entire
 * provider itself but rather a proxy that contains enough information to
 * decide whether the provider is able to satisfy a particular request together
 * with code that can create the actual provider on demand.  The details of
 * provider classes tend to be highly service-specific; no single class or
 * interface could possibly unify them, so no such class has been defined.  The
 * only requirement enforced here is that provider classes must have a
 * zero-argument constructor so that they may be instantiated during lookup.
 * <p> A service provider identifies itself by placing a provider-configuration
 * file in the resource directory <tt>META-INF/services</tt>.  The file's name
 * should consist of the fully-qualified name of the abstract service class.
 * The file should contain a list of fully-qualified concrete provider-class
 * names, one per line.  Space and tab characters surrounding each name, as
 * well as blank lines, are ignored.  The comment character is <tt>'#'</tt>
 * (<tt>0x23</tt>); on each line all characters following the first comment
 * character are ignored.  The file must be encoded in UTF-8.
 * <p> If a particular concrete provider class is named in more than one
 * configuration file, or is named in the same configuration file more than
 * once, then the duplicates will be ignored.  The configuration file naming a
 * particular provider need not be in the same jar file or other distribution
 * unit as the provider itself.  The provider must be accessible from the same
 * class loader that was initially queried to locate the configuration file;
 * note that this is not necessarily the class loader that found the file.
 * <p> <b>Example:</b> Suppose we have a service class named
 * <tt>java.io.spi.CharCodec</tt>.  It has two abstract methods:
 * <pre>
 *   public abstract CharEncoder getEncoder(String encodingName);
 *   public abstract CharDecoder getDecoder(String encodingName);
 * </pre>
 * Each method returns an appropriate object or <tt>null</tt> if it cannot
 * translate the given encoding.  Typical <tt>CharCodec</tt> providers will
 * support more than one encoding.
 * <p> If <tt>sun.io.StandardCodec</tt> is a provider of the <tt>CharCodec</tt>
 * service then its jar file would contain the file
 * <tt>META-INF/services/java.io.spi.CharCodec</tt>.  This file would contain
 * the single line:
 * <pre>
 *   sun.io.StandardCodec    # Standard codecs for the platform
 * </pre>
 * To locate an encoder for a given encoding name, the internal I/O code would
 * do something like this:
 * <pre>
 *   CharEncoder getEncoder(String encodingName) {
 *       Iterator ps = Service.providers(CharCodec.class);
 *       while (ps.hasNext()) {
 *           CharCodec cc = (CharCodec)ps.next();
 *           CharEncoder ce = cc.getEncoder(encodingName);
 *           if (ce != null)
 *               return ce;
 *       }
 *       return null;
 *   }
 * </pre>
 * The provider-lookup mechanism always executes in the security context of the
 * caller.  Trusted system code should typically invoke the methods in this
 * class from within a privileged security context.
 */

public final class Service {

	private static final String prefix = "META-INF/services/";

	private Service() {
	}

	private static void fail(Class service, String msg, Throwable cause) throws IllegalArgumentException {
		IllegalArgumentException sce = new IllegalArgumentException(service.getName() + ": " + msg);
		throw (IllegalArgumentException) EnvHelp.initCause(sce, cause);
	}

	private static void fail(Class service, String msg) throws IllegalArgumentException {
		throw new IllegalArgumentException(service.getName() + ": " + msg);
	}

	private static void fail(Class service, URL u, int line, String msg) throws IllegalArgumentException {
		fail(service, u + ":" + line + ": " + msg);
	}

	/**
	 * Parse a single line from the given configuration file, adding the name
	 * on the line to both the names list and the returned set iff the name is
	 * not already a member of the returned set.
	 */
	private static int parseLine(Class service, URL u, BufferedReader r, int lc, List names, Set returned)
			throws IOException, IllegalArgumentException {
		String ln = r.readLine();
		if (ln == null) {
			return -1;
		}
		int ci = ln.indexOf('#');
		if (ci >= 0) {
			ln = ln.substring(0, ci);
		}
		ln = ln.trim();
		int n = ln.length();
		if (n != 0) {
			if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)) {
				fail(service, u, lc, "Illegal configuration-file syntax");
			}
			if (!Character.isJavaIdentifierStart(ln.charAt(0))) {
				fail(service, u, lc, "Illegal provider-class name: " + ln);
			}
			for (int i = 1; i < n; i++) {
				char c = ln.charAt(i);
				if (!Character.isJavaIdentifierPart(c) && (c != '.')) {
					fail(service, u, lc, "Illegal provider-class name: " + ln);
				}
			}
			if (!returned.contains(ln)) {
				names.add(ln);
				returned.add(ln);
			}
		}
		return lc + 1;
	}

	/**
	 * Parse the content of the given URL as a provider-configuration file.
	 *
	 * @param service  The service class for which providers are being sought;
	 *                 used to construct error detail strings
	 * @param url      The URL naming the configuration file to be parsed
	 * @param returned A Set containing the names of provider classes that have already
	 *                 been returned.  This set will be updated to contain the names
	 *                 that will be yielded from the returned <tt>Iterator</tt>.
	 * @return A (possibly empty) <tt>Iterator</tt> that will yield the
	 * provider-class names in the given configuration file that are
	 * not yet members of the returned set
	 * @throws IllegalArgumentException If an I/O error occurs while reading from the given URL, or
	 *                                  if a configuration-file format error is detected
	 */
	private static Iterator parse(Class service, URL url, Set returned) throws IllegalArgumentException {
		InputStream in = null;
		BufferedReader r = null;
		ArrayList names = new ArrayList();
		try {
			in = url.openStream();
			r = new BufferedReader(new InputStreamReader(in, "utf-8"));
			int lc = 1;
			while ((lc = parseLine(service, url, r, lc, names, returned)) >= 0)
				;
		}
		catch (IOException x) {
			fail(service, ": " + x);
		}
		finally {
			try {
				if (r != null) {
					r.close();
				}
				if (in != null) {
					in.close();
				}
			}
			catch (IOException y) {
				fail(service, ": " + y);
			}
		}
		return names.iterator();
	}

	/**
	 * Private inner class implementing fully-lazy provider lookup
	 */
	private static class LazyIterator implements Iterator {

		Class service;
		ClassLoader loader;
		Enumeration configs = null;
		Iterator pending = null;
		Set returned = new TreeSet();
		String nextName = null;

		private LazyIterator(Class service, ClassLoader loader) {
			this.service = service;
			this.loader = loader;
		}

		public boolean hasNext() throws IllegalArgumentException {
			if (nextName != null) {
				return true;
			}
			if (configs == null) {
				try {
					String fullName = prefix + service.getName();
					if (loader == null) {
						configs = ClassLoader.getSystemResources(fullName);
					} else {
						configs = loader.getResources(fullName);
					}
				}
				catch (IOException x) {
					fail(service, ": " + x);
				}
			}
			while ((pending == null) || !pending.hasNext()) {
				if (!configs.hasMoreElements()) {
					return false;
				}
				pending = parse(service, (URL) configs.nextElement(), returned);
			}
			nextName = (String) pending.next();
			return true;
		}

		public Object next() throws IllegalArgumentException {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			String cn = nextName;
			nextName = null;
			try {
				return Class.forName(cn, true, loader).newInstance();
			}
			catch (ClassNotFoundException x) {
				fail(service, "Provider " + cn + " not found");
			}
			catch (Exception x) {
				fail(service, "Provider " + cn + " could not be instantiated: " + x, x);
			}
			return null;    /* This cannot happen */
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Locates and incrementally instantiates the available providers of a
	 * given service using the given class loader.
	 * <p> This method transforms the name of the given service class into a
	 * provider-configuration filename as described above and then uses the
	 * <tt>getResources</tt> method of the given class loader to find all
	 * available files with that name.  These files are then read and parsed to
	 * produce a list of provider-class names.  The iterator that is returned
	 * uses the given class loader to lookup and then instantiate each element
	 * of the list.
	 * <p> Because it is possible for extensions to be installed into a running
	 * Java virtual machine, this method may return different results each time
	 * it is invoked. <p>
	 *
	 * @param service The service's abstract service class
	 * @param loader  The class loader to be used to load provider-configuration files
	 *                and instantiate provider classes, or <tt>null</tt> if the system
	 *                class loader (or, failing that the bootstrap class loader) is to
	 *                be used
	 * @return An <tt>Iterator</tt> that yields provider objects for the given
	 * service, in some arbitrary order.  The iterator will throw a
	 * <tt>IllegalArgumentException</tt> if a provider-configuration
	 * file violates the specified format or if a provider class cannot
	 * be found and instantiated.
	 * @throws IllegalArgumentException If a provider-configuration file violates the specified format
	 *                                  or names a provider class that cannot be found and instantiated
	 */
	public static Iterator providers(Class service, ClassLoader loader) throws IllegalArgumentException {
		return new LazyIterator(service, loader);
	}
}

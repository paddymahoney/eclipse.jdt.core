/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.compiler.tool.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.ServiceLoader;

import javax.lang.model.SourceVersion;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileManager.Location;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

import junit.framework.TestCase;

public class CompilerToolJava9Tests extends TestCase {
	private static final String RESOURCES_DIR = "resources";
	private JavaCompiler[] compilers;
	private String[] compilerNames;
	private boolean isJREBelow9;
	private static String _tmpFolder;
	private static String _tmpSrcFolderName;
	private static File _tmpSrcDir;
	private static String _tmpBinFolderName;
	private static File _tmpBinDir;
	public static String _tmpGenFolderName;
	private static File _tmpGenDir;
	
	private static String modules_directory;
	public CompilerToolJava9Tests(String name) {
		super(name);
	}
	@Override
	protected void setUp() throws Exception {
		this.isJREBelow9 = SourceVersion.latest().compareTo(SourceVersion.RELEASE_8) <= 0;
		if (isJREBelow9)
			return;
		this.compilers = new JavaCompiler[2];
		this.compilerNames = new String[2];
		ServiceLoader<JavaCompiler> javaCompilerLoader = ServiceLoader.load(JavaCompiler.class);
		int compilerCounter = 0;
		for (JavaCompiler compiler : javaCompilerLoader) {
			compilerCounter++;
			if (compiler instanceof EclipseCompiler) {
				this.compilers[1] = compiler;
				this.compilerNames[1] = "Eclipse Compiler";
			}
		}
		this.compilerNames[0] = "System compiler";
		this.compilers[0] = ToolProvider.getSystemJavaCompiler();
		assertEquals("Only one compiler available", 2, compilerCounter);
		assertNotNull("System compiler unavailable", this.compilers[0]);
		assertNotNull("Eclipse compiler unavailable", this.compilers[1]);
		initializeLocations();
	}
	protected void initializeLocations() {
		_tmpFolder = System.getProperty("java.io.tmpdir");
		if (_tmpFolder.endsWith(File.separator)) {
			_tmpFolder += "eclipse-temp";
		} else {
			_tmpFolder += (File.separator + "eclipse-temp");
		}
		_tmpBinFolderName = _tmpFolder + File.separator + "bin";
		_tmpBinDir = new File(_tmpBinFolderName);
		deleteTree(_tmpBinDir); // remove existing contents
		_tmpBinDir.mkdirs();
		assert _tmpBinDir.exists() : "couldn't mkdirs " + _tmpBinFolderName;

		_tmpGenFolderName = _tmpFolder + File.separator + "gen-src";
		_tmpGenDir = new File(_tmpGenFolderName);
		deleteTree(_tmpGenDir); // remove existing contents
		_tmpGenDir.mkdirs();
		assert _tmpGenDir.exists() : "couldn't mkdirs " + _tmpGenFolderName;

		_tmpSrcFolderName = _tmpFolder + File.separator + "src";
		_tmpSrcDir = new File(_tmpSrcFolderName);
		deleteTree(_tmpSrcDir); // remove existing contents
		_tmpSrcDir.mkdirs();
		assert _tmpSrcDir.exists() : "couldn't mkdirs " + _tmpSrcFolderName;

		modules_directory = getPluginDirectoryPath() + File.separator + "resources" + File.separator + "module_locations";
	}
	public void testGetLocationForModule1() {
		if (this.isJREBelow9) return;
		for(int i = 0; i < 2; i++) {
			String cName = this.compilerNames[i];
			JavaCompiler compiler = this.compilers[i];
			StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());
			try {
	            Location location = manager.getLocationForModule(StandardLocation.SYSTEM_MODULES, "java.base");
	            assertNotNull(cName + ": Location should not be null", location);
	        } catch (UnsupportedOperationException ex) {
	            fail(cName + ": Should support getLocationForModule()");
	        }
			catch (IOException e) {
	        	 fail(cName + ": Should support getLocationForModule()");
			}
		}

	}

	public void testGetLocationForModule2() throws IOException {
		if (this.isJREBelow9) return;
		for(int i = 0; i < 2; i++) {
			String cName = this.compilerNames[i];
			JavaCompiler compiler = this.compilers[i];
			StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());
			Path path = Paths.get(modules_directory + File.separator + "SimpleModules");
			manager.setLocationFromPaths(StandardLocation.MODULE_PATH, Arrays.asList(path));
			try {
				JavaFileManager.Location location = manager.getLocationForModule(StandardLocation.MODULE_PATH, "module.two");
				assertNotNull(cName + ":module path location should not be null", location);
			} catch (UnsupportedOperationException ex) {
				fail(cName + ":Should support getLocationForModule()");
			}
		}
	}
	public void testGetLocationForModule3() throws IOException {
		if (this.isJREBelow9) return;
		for(int i = 0; i < 2; i++) {
			String cName = this.compilerNames[i];
			JavaCompiler compiler = this.compilers[i];
			StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());
			Path path = Paths.get(modules_directory + File.separator + "SimpleModules" + File.separator + "module.one");
			manager.setLocationFromPaths(StandardLocation.MODULE_PATH, Arrays.asList(path));
			try {
				JavaFileManager.Location location = manager.getLocationForModule(StandardLocation.MODULE_PATH, "module.one");
				assertNotNull(cName + ":module path location should not be null", location);
			} catch (UnsupportedOperationException ex) {
				fail(cName + ":Should support getLocationForModule()");
			}
		}
	}
	public void testGetJavaFileObjects() {
		if (this.isJREBelow9) return;
	}
	public void testGetJavaFileObjects2() {
		if (this.isJREBelow9) return;
	}
	public void testSetLocationAsPaths() {
		if (this.isJREBelow9) return;
	}
	public void testContains() {
		if (this.isJREBelow9) return;
	}
	public void testGetServiceLoader() {
		if (this.isJREBelow9) return;
	}
	public void testInferModuleName() {
		if (this.isJREBelow9) return;
	}
	public void testListLocationsForModules() {
		if (this.isJREBelow9) return;
	}
	public void testAsPath() {
		if (this.isJREBelow9) return;
	}
	/**
	 * Recursively delete the contents of a directory, including any subdirectories.
	 * This is not optimized to handle very large or deep directory trees efficiently.
	 * @param f is either a normal file (which will be deleted) or a directory
	 * (which will be emptied and then deleted).
	 */
	public static void deleteTree(File f)
	{
		if (null == f) {
			return;
		}
		File[] children = f.listFiles();
		if (null != children) {
			// if f has any children, (recursively) delete them
			for (File child : children) {
				deleteTree(child);
			}
		}
		// At this point f is either a normal file or an empty directory
		f.delete();
	}
	/**
	 * Copy a file from one location to another, unless the destination file already exists and has
	 * the same timestamp and file size. Create the destination location if necessary. Convert line
	 * delimiters according to {@link #shouldConvertToIndependentLineDelimiter(File)}.
	 *
	 * @param src
	 *            the full path to the resource location.
	 * @param destFolder
	 *            the full path to the destination location.
	 * @throws IOException
	 */
	public static void copyResource(File src, File dest) throws IOException {
		if (dest.exists() &&
				src.lastModified() < dest.lastModified() &&
				src.length() == dest.length())
		{
			return;
		}

		// read source bytes
		byte[] srcBytes = null;
		srcBytes = read(src);

		if (shouldConvertToIndependentLineDelimiter(src)) {
			String contents = new String(srcBytes);
			contents = convertToIndependentLineDelimiter(contents);
			srcBytes = contents.getBytes();
		}
		writeFile(dest, srcBytes);
	}

	public static void writeFile(File dest, byte[] srcBytes) throws IOException {

		File destFolder = dest.getParentFile();
		if (!destFolder.exists()) {
			if (!destFolder.mkdirs()) {
				throw new IOException("Unable to create directory " + destFolder);
			}
		}
		// write bytes to dest
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest);
			out.write(srcBytes);
			out.flush();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * Copy a resource that is located under the <code>resources</code> folder of the plugin to a
	 * corresponding location under the specified target folder. Convert line delimiters according
	 * to {@link #shouldConvertToIndependentLineDelimiter(File)}.
	 *
	 * @param resourcePath
	 *            the relative path under <code>[plugin-root]/resources</code> of the resource to
	 *            be copied
	 * @param targetFolder
	 *            the absolute path of the folder under which the resource will be copied. Folder
	 *            and subfolders will be created if necessary.
	 * @return a file representing the copied resource
	 * @throws IOException
	 */
	public static File copyResource(String resourcePath, File targetFolder) throws IOException {
		File resDir = new File(getPluginDirectoryPath(), RESOURCES_DIR);
		File resourceFile = new File(resDir, resourcePath);
		File targetFile = new File(targetFolder, resourcePath);
		copyResource(resourceFile, targetFile);
		return targetFile;
	}

	/**
	 * Copy all the files under the directory specified by src to the directory
	 * specified by dest.  The src and dest directories must exist; child directories
	 * under dest will be created as required.  Existing files in dest will be
	 * overwritten.  Newlines will be converted according to
	 * {@link #shouldConvertToIndependentLineDelimiter(File)}.  Directories
	 * named "CVS" will be ignored.
	 * @param resourceFolderName the name of the source folder, relative to
	 * <code>[plugin-root]/resources</code>
	 * @param the absolute path of the destination folder
	 * @throws IOException
	 */
	public static void copyResources(String resourceFolderName, File destFolder) throws IOException {
		File resDir = new File(getPluginDirectoryPath(), RESOURCES_DIR);
		File resourceFolder = new File(resDir, resourceFolderName);
		copyResources(resourceFolder, destFolder);
	}

	private static void copyResources(File resourceFolder, File destFolder) throws IOException {
		if (resourceFolder == null) {
			return;
		}
		// Copy all resources in this folder
		String[] children = resourceFolder.list();
		if (null == children) {
			return;
		}
		// if there are any children, (recursively) copy them
		for (String child : children) {
			if ("CVS".equals(child)) {
				continue;
			}
			File childRes = new File(resourceFolder, child);
			File childDest = new File(destFolder, child);
			if (childRes.isDirectory()) {
				copyResources(childRes, childDest);
			}
			else {
				copyResource(childRes, childDest);
			}
		}
	}
	protected static String getPluginDirectoryPath() {
		try {
			if (Platform.isRunning()) {
				URL platformURL = Platform.getBundle("org.eclipse.jdt.compiler.tool.tests").getEntry("/");
				return new File(FileLocator.toFileURL(platformURL).getFile()).getAbsolutePath();
			}
			return new File(System.getProperty("user.dir")).getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @return true if this file's end-of-line delimiters should be replaced with
	 * a platform-independent value, e.g. for compilation.
	 */
	public static boolean shouldConvertToIndependentLineDelimiter(File file) {
		return file.getName().endsWith(".java");
	}
	public static byte[] read(java.io.File file) throws java.io.IOException {
		int fileLength;
		byte[] fileBytes = new byte[fileLength = (int) file.length()];
		java.io.FileInputStream stream = null;
		try {
			stream = new java.io.FileInputStream(file);
			int bytesRead = 0;
			int lastReadSize = 0;
			while ((lastReadSize != -1) && (bytesRead != fileLength)) {
				lastReadSize = stream.read(fileBytes, bytesRead, fileLength - bytesRead);
				bytesRead += lastReadSize;
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return fileBytes;
	}

	public static String convertToIndependentLineDelimiter(String source) {
		if (source.indexOf('\n') == -1 && source.indexOf('\r') == -1) return source;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, length = source.length(); i < length; i++) {
			char car = source.charAt(i);
			if (car == '\r') {
				buffer.append('\n');
				if (i < length-1 && source.charAt(i+1) == '\n') {
					i++; // skip \n after \r
				}
			} else {
				buffer.append(car);
			}
		}
		return buffer.toString();
	}
}

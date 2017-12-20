package tech.ecoa.osets.eclipse.plugin.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;

public class CopyrightAmend {

	public static final String ROOT_FOLDER = "C:\\WS\\git\\OSETS\\osets-eclipse-plugin\\src\\";
	public static final String IAWG_FOLDER = "com\\iawg\\ecoa";
	public static final String OSETS_FOLDER = "tech\\ecoa\\osets";

	public static void main(String[] args) throws IOException {
		CopyrightAmend amend = new CopyrightAmend();
		amend.processIawg();
		amend.processOsets();
	}

	public void clearCopyright() throws IOException {
		File iawgFolder = new File(ROOT_FOLDER + IAWG_FOLDER);
		Collection<File> iawgJava = FileUtils.listFiles(iawgFolder, new JavaFileFilter(), new IAWGDirFilter());
		for (File file : iawgJava) {
			if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("java")) {
				String content = FileUtils.readFileToString(file);
				content = "package" + StringUtils.substringAfter(content, "package");
				FileUtils.write(file, content, false);
			}
		}
		File osetsFolder = new File(ROOT_FOLDER + OSETS_FOLDER);
		Collection<File> osetsJava = FileUtils.listFiles(osetsFolder, new JavaFileFilter(), new OSETSDirFilter());
		for (File file : osetsJava) {
			if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("java")) {
				String content = FileUtils.readFileToString(file);
				content = "package" + StringUtils.substringAfter(content, "package");
				FileUtils.write(file, content, false);
			}
		}
	}

	public void processIawg() throws IOException {
		String iawgCopyright = IOUtils.toString(CopyrightAmend.class.getClassLoader().getResourceAsStream("copyright_iawg.txt"));
		File iawgFolder = new File(ROOT_FOLDER + IAWG_FOLDER);
		Collection<File> iawgJava = FileUtils.listFiles(iawgFolder, new JavaFileFilter(), new IAWGDirFilter());
		for (File file : iawgJava) {
			if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("java")) {
				String content = FileUtils.readFileToString(file);
				content = iawgCopyright + content;
				FileUtils.write(file, content, false);
			}
		}
	}

	public void processOsets() throws IOException {
		String osetsCopyright = IOUtils.toString(CopyrightAmend.class.getClassLoader().getResourceAsStream("copyright_osets.txt"));
		File osetsFolder = new File(ROOT_FOLDER + OSETS_FOLDER);
		Collection<File> osetsJava = FileUtils.listFiles(osetsFolder, new JavaFileFilter(), new OSETSDirFilter());
		for (File file : osetsJava) {
			if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("java")) {
				String content = FileUtils.readFileToString(file);
				content = osetsCopyright + content;
				FileUtils.write(file, content, false);
			}
		}
	}

	public class JavaFileFilter implements IOFileFilter {

		@Override
		public boolean accept(File arg) {
			return FilenameUtils.getExtension(arg.getName()).equalsIgnoreCase("java");
		}

		@Override
		public boolean accept(File arg0, String arg1) {
			return FilenameUtils.getExtension(arg1).equalsIgnoreCase("java");
		}

	}

	public class IAWGDirFilter implements IOFileFilter {

		@Override
		public boolean accept(File arg0) {
			return !(StringUtils.contains(arg0.getAbsolutePath(), "jaxbclasses"));
		}

		@Override
		public boolean accept(File arg0, String arg1) {
			return !(StringUtils.contains(arg0.getAbsolutePath(), "jaxbclasses"));
		}

	}

	public class OSETSDirFilter implements IOFileFilter {

		@Override
		public boolean accept(File arg0) {
			return !(StringUtils.contains(arg0.getAbsolutePath(), "model"));
		}

		@Override
		public boolean accept(File arg0, String arg1) {
			return !(StringUtils.contains(arg0.getAbsolutePath(), "model"));
		}

	}
}

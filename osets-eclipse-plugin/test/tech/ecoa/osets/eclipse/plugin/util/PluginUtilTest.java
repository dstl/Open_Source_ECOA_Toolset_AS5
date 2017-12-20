package tech.ecoa.osets.eclipse.plugin.util;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class PluginUtilTest {

	private PluginUtil util = new PluginUtil();

	@Test
	public void testGetResLocation() {
		util.getResourcesWithExtension("types", "");
	}

	@Test
	public void testRemove() {
		System.out.println(removeEmptyTags("attrib=\"venu\" module=\"\" type=\"sample\" value=\"\""));
	}

	public String removeEmptyTags(String string) {
		String ret = "";
		ArrayList<String> fin = new ArrayList<String>();
		if (StringUtils.contains(string, "\"\"")) {
			String[] brk = StringUtils.split(string, " ");
			for (String str : brk) {
				if (!StringUtils.contains(str, "\"\""))
					fin.add(str);
			}
		}
		if (fin.size() > 0)
			for (String val : fin) {
				ret += val + " ";
			}
		else
			ret = string;
		return ret;
	}

}

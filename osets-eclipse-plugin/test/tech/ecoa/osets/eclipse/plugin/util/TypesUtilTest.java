package tech.ecoa.osets.eclipse.plugin.util;

import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.Test;

public class TypesUtilTest {

	private TypesUtil util = new TypesUtil();

	@Test
	public void testGetAllSimpleTypes() throws IOException, JAXBException {
		util.getAllSimpleTypeNames();
	}

	@Test
	public void testGetAllBasicTypes() {
		fail("Not yet implemented");
	}

}

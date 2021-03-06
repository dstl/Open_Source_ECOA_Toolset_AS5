//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.07.10 at 03:49:20 PM IST 
//

package tech.ecoa.osets.model.deploy;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the tech.ecoa.osets.model.deploy package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _Deployment_QNAME = new QName("http://www.ecoa.technology/deployment-1.0", "deployment");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: tech.ecoa.osets.model.deploy
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link ProtectionDomain }
	 * 
	 */
	public ProtectionDomain createProtectionDomain() {
		return new ProtectionDomain();
	}

	/**
	 * Create an instance of {@link Use }
	 * 
	 */
	public Use createUse() {
		return new Use();
	}

	/**
	 * Create an instance of {@link Deployment }
	 * 
	 */
	public Deployment createDeployment() {
		return new Deployment();
	}

	/**
	 * Create an instance of {@link PlatformConfiguration }
	 * 
	 */
	public PlatformConfiguration createPlatformConfiguration() {
		return new PlatformConfiguration();
	}

	/**
	 * Create an instance of {@link ComponentLog }
	 * 
	 */
	public ComponentLog createComponentLog() {
		return new ComponentLog();
	}

	/**
	 * Create an instance of {@link ComputingNodeConfiguration }
	 * 
	 */
	public ComputingNodeConfiguration createComputingNodeConfiguration() {
		return new ComputingNodeConfiguration();
	}

	/**
	 * Create an instance of {@link LogPolicy }
	 * 
	 */
	public LogPolicy createLogPolicy() {
		return new LogPolicy();
	}

	/**
	 * Create an instance of {@link ModuleLog }
	 * 
	 */
	public ModuleLog createModuleLog() {
		return new ModuleLog();
	}

	/**
	 * Create an instance of {@link ProtectionDomain.ExecuteOn }
	 * 
	 */
	public ProtectionDomain.ExecuteOn createProtectionDomainExecuteOn() {
		return new ProtectionDomain.ExecuteOn();
	}

	/**
	 * Create an instance of {@link ProtectionDomain.DeployedModuleInstance }
	 * 
	 */
	public ProtectionDomain.DeployedModuleInstance createProtectionDomainDeployedModuleInstance() {
		return new ProtectionDomain.DeployedModuleInstance();
	}

	/**
	 * Create an instance of {@link ProtectionDomain.DeployedTriggerInstance }
	 * 
	 */
	public ProtectionDomain.DeployedTriggerInstance createProtectionDomainDeployedTriggerInstance() {
		return new ProtectionDomain.DeployedTriggerInstance();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link Deployment
	 * }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "http://www.ecoa.technology/deployment-1.0", name = "deployment")
	public JAXBElement<Deployment> createDeployment(Deployment value) {
		return new JAXBElement<Deployment>(_Deployment_QNAME, Deployment.class, null, value);
	}

}

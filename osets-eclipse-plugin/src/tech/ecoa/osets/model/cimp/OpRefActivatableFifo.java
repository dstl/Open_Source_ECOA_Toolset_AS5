//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.06.23 at 07:36:06 AM IST 
//

package tech.ecoa.osets.model.cimp;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for OpRefActivatableFifo complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="OpRefActivatableFifo">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ecoa.technology/implementation-1.0}OpRefActivatable">
 *       &lt;attribute name="fifoSize" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="8" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpRefActivatableFifo")
@XmlSeeAlso({ OpRefServer.class })
public class OpRefActivatableFifo extends OpRefActivatable {

	@XmlAttribute(name = "fifoSize")
	@XmlSchemaType(name = "positiveInteger")
	protected BigInteger fifoSize;

	/**
	 * Gets the value of the fifoSize property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getFifoSize() {
		if (fifoSize == null) {
			return new BigInteger("8");
		} else {
			return fifoSize;
		}
	}

	/**
	 * Sets the value of the fifoSize property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setFifoSize(BigInteger value) {
		this.fifoSize = value;
	}

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.07.20 at 08:12:16 AM BST 
//

package com.iawg.ecoa.jaxbclasses.vcbinding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for destinationPlatformType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="destinationPlatformType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="platformName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="platformID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="VCID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "destinationPlatformType")
public class DestinationPlatformType {

	@XmlAttribute(name = "platformName")
	protected String platformName;
	@XmlAttribute(name = "platformID")
	protected String platformID;
	@XmlAttribute(name = "VCID")
	protected String vcid;

	/**
	 * Gets the value of the platformName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPlatformName() {
		return platformName;
	}

	/**
	 * Sets the value of the platformName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPlatformName(String value) {
		this.platformName = value;
	}

	/**
	 * Gets the value of the platformID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPlatformID() {
		return platformID;
	}

	/**
	 * Sets the value of the platformID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPlatformID(String value) {
		this.platformID = value;
	}

	/**
	 * Gets the value of the vcid property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getVCID() {
		return vcid;
	}

	/**
	 * Sets the value of the vcid property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setVCID(String value) {
		this.vcid = value;
	}

}

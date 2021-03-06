//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.06.06 at 09:10:02 AM IST 
//

package tech.ecoa.osets.model.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * A set of data type definitions
 * 
 * <p>
 * Java class for DataTypes complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="DataTypes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="simple" type="{http://www.ecoa.technology/types-1.0}Simple"/>
 *         &lt;element name="record" type="{http://www.ecoa.technology/types-1.0}Record"/>
 *         &lt;element name="constant" type="{http://www.ecoa.technology/types-1.0}Constant"/>
 *         &lt;element name="variantRecord" type="{http://www.ecoa.technology/types-1.0}VariantRecord"/>
 *         &lt;element name="array" type="{http://www.ecoa.technology/types-1.0}Array"/>
 *         &lt;element name="fixedArray" type="{http://www.ecoa.technology/types-1.0}FixedArray"/>
 *         &lt;element name="enum" type="{http://www.ecoa.technology/types-1.0}Enum"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataTypes", propOrder = { "simpleOrRecordOrConstant" })
public class DataTypes {

	@XmlElements({ @XmlElement(name = "simple", type = Simple.class), @XmlElement(name = "record", type = Record.class), @XmlElement(name = "constant", type = Constant.class), @XmlElement(name = "variantRecord", type = VariantRecord.class), @XmlElement(name = "array", type = Array.class), @XmlElement(name = "fixedArray", type = FixedArray.class), @XmlElement(name = "enum", type = Enum.class) })
	protected List<Object> simpleOrRecordOrConstant;

	/**
	 * Gets the value of the simpleOrRecordOrConstant property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the simpleOrRecordOrConstant property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSimpleOrRecordOrConstant().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Simple }
	 * {@link Record } {@link Constant } {@link VariantRecord } {@link Array }
	 * {@link FixedArray } {@link Enum }
	 * 
	 * 
	 */
	public List<Object> getSimpleOrRecordOrConstant() {
		if (simpleOrRecordOrConstant == null) {
			simpleOrRecordOrConstant = new ArrayList<Object>();
		}
		return this.simpleOrRecordOrConstant;
	}

}

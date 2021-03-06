//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.06.06 at 09:11:41 AM IST 
//

package tech.ecoa.osets.model.intf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Use of the "event" exchange mechanism.
 * 
 * 
 * <p>
 * Java class for Event complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="Event">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ecoa.technology/interface-1.0}Operation">
 *       &lt;sequence>
 *         &lt;element name="input" type="{http://www.ecoa.technology/interface-1.0}Parameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="direction" use="required" type="{http://www.ecoa.technology/interface-1.0}E_EventDirection" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Event", propOrder = { "input" })
public class Event extends Operation {

	protected List<Parameter> input;
	@XmlAttribute(name = "direction", required = true)
	protected EEventDirection direction;

	/**
	 * Gets the value of the input property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the input property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getInput().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Parameter
	 * }
	 * 
	 * 
	 */
	public List<Parameter> getInput() {
		if (input == null) {
			input = new ArrayList<Parameter>();
		}
		return this.input;
	}

	/**
	 * Gets the value of the direction property.
	 * 
	 * @return possible object is {@link EEventDirection }
	 * 
	 */
	public EEventDirection getDirection() {
		return direction;
	}

	/**
	 * Sets the value of the direction property.
	 * 
	 * @param value
	 *            allowed object is {@link EEventDirection }
	 * 
	 */
	public void setDirection(EEventDirection value) {
		this.direction = value;
	}

}

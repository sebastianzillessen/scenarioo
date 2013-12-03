package org.scenarioo.model.docu.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * Contains all the data collected from a webtest for one step of the webtest.
 * 
 * The data is processed by the Scenarioo webapplication to transform into webapps internal structure.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Step implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Page page;
	
	private StepDescription stepDescription;
	
	private StepHtml html;
	
	private StepMetadata metadata = new StepMetadata();
	
}

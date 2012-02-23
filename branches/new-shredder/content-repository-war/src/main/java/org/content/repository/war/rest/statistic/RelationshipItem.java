package org.content.repository.war.rest.statistic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.scapdev.content.core.query.RelationshipStatistic;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class RelationshipItem {
	@SuppressWarnings("unused")
	@XmlAttribute(required=true)
	private String id;
	// TODO: add support for language bundle
	private String title;
	@SuppressWarnings("unused")
	@XmlAttribute(required=true)
	private int count;

	public RelationshipItem () {
		// No arg required by JAXB
	}

	public RelationshipItem(RelationshipStatistic stat) {
		id = stat.getRelationshipInfo().getId();
		count = stat.getCount();
	}
}

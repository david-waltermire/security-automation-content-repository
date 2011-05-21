package org.content.repository.war.rest.statistic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.scapdev.content.core.query.EntityStatistic;
import org.scapdev.content.core.query.RelationshipStatistic;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class StatItem {
	@SuppressWarnings("unused")
	@XmlAttribute(required=true)
	private String id;
	// TODO: add support for language bundle
	private String title;
	@SuppressWarnings("unused")
	@XmlAttribute(required=true)
	private int count;
	@XmlElement
	private List<RelationshipItem> relationship;

	public StatItem () {
		// No arg required by JAXB
	}

	public StatItem(EntityStatistic stat) {
		this.id = stat.getEntityInfo().getId();
		this.count = stat.getCount();
		Collection<? extends RelationshipStatistic> relationshipStats = stat.getRelationshipInfoStatistics().values();
		this.relationship = new ArrayList<RelationshipItem>(relationshipStats.size());
		for (RelationshipStatistic item : relationshipStats) {
			this.relationship.add(new RelationshipItem(item));
		}
	}
}
package org.scapdev.content.core.query;

import static org.scapdev.content.core.query.Conditional.allOf;
import static org.scapdev.content.core.query.Conditional.anyOf;
import static org.scapdev.content.core.query.entity.ContentId.contentId;
import static org.scapdev.content.core.query.entity.EntityContext.entityType;
import static org.scapdev.content.core.query.entity.EntityQuery.selectEntitiesWith;
import static org.scapdev.content.core.query.entity.Key.field;
import static org.scapdev.content.core.query.entity.Key.key;
import static org.scapdev.content.core.query.relationship.Relationship.relationship;
import static org.scapdev.content.core.query.relationship.RelationshipContext.relationshipType;
import static org.scapdev.content.core.query.relationship.To.to;

import java.io.IOException;

import org.junit.Test;
import org.scapdev.content.core.query.entity.EntityQuery;
import org.scapdev.content.core.query.json.JSONSerializer;

public class EntityQueryTest {

	@SuppressWarnings("static-method")
	@Test
	public void test() throws IOException {
		EntityQuery query = selectEntitiesWith(
				allOf(
						anyOf(
							key("http://scap.nist.gov/resource/content/source/1.2#key-datastream-collection",
									field("datastream-collection-id", "scap_gov.nist_collection_Win7-54-1.2.0.0.zip")
							),
							contentId("123456789")
						),
						entityType("http://scap.nist.gov/resource/content/source/1.2#document-datastream-collection"),
						relationship(
								anyOf(
										relationshipType("http://scap.nist.gov/resource/content/source/1.2#relationship-datastream-boundary"),
										relationshipType("http://scap.nist.gov/resource/content/source/1.2#relationship-component-boundary"),
										to(
												contentId("23456")
										)
								)
								
						)
				));

		JSONSerializer<EntityQuery> serializer = new JSONSerializer<EntityQuery>();
		String text = serializer.serialize(query);
		System.out.println("JSON: "+text);

		query = serializer.deserialize(text, EntityQuery.class);
		text = serializer.serialize(query);
		System.out.println("JSON: "+text);
	}
}

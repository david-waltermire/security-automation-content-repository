package org.scapdev.content.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
public @interface ExternalIdentifierRef {
	String idRef();
	String qualifier() default "";
}

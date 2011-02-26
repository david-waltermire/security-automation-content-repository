<?xml version="1.0" encoding="UTF-8"?>
<!--
  The MIT License
  
  Copyright (c) 2011 David Waltermire
  
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
  
  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.
  
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
  xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
  xmlns:annox="http://annox.dev.java.net"
  xmlns:a="http://annox.dev.java.net/org.scapdev.content.annotation"
  xmlns:meta="http://scapdev.org/schema/meta-model/0.1"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:saxon="http://saxon.sf.net/"
  extension-element-prefixes="saxon"
  version="2.0">

  <xsl:output indent="yes" xml:space="default" method="xml" />

  <xsl:template match="meta:meta-model">
    <xsl:comment>
      XSLT Version = <xsl:copy-of select="system-property('xsl:version')"/>
      XSLT Vendor = <xsl:copy-of select="system-property('xsl:vendor')"/>
      XSLT Vendor URL = <xsl:copy-of select="system-property('xsl:vendor-url')"/>
    </xsl:comment>
    <jaxb:bindings jaxb:extensionBindingPrefixes="annox" jaxb:version="2.1"
                   xsi:schemaLocation="http://java.sun.com/xml/ns/jaxb http://java.sun.com/xml/ns/jaxb/bindingschema_2_0.xsd">
      <xsl:apply-templates/>
    </jaxb:bindings>
  </xsl:template>
  
  <xsl:template match="meta:schema">
    <jaxb:bindings schemaLocation="{@schemaLocation}" node="{meta:schema-node/@node}">
      <xsl:comment> == Documents == </xsl:comment>
      <xsl:for-each select="meta:documents/meta:document">
        <xsl:apply-templates mode="document" select="."/>
      </xsl:for-each>
      <xsl:comment> == Document Containers == </xsl:comment>
      <xsl:for-each select="meta:documents/meta:document">
        <xsl:apply-templates mode="container" select="."/>
      </xsl:for-each>
      <xsl:comment> == Entities == </xsl:comment>
      <xsl:apply-templates mode="entity" select="meta:entities"/>
      <xsl:comment> == Relationships == </xsl:comment>
      <xsl:apply-templates mode="relationship" select="meta:relationships"/>
    </jaxb:bindings>
  </xsl:template>
  
  <xsl:template match="meta:document" mode="document">
    <xsl:comment>document <xsl:value-of select="@id"/></xsl:comment>
    <jaxb:bindings xsl:use-attribute-sets="node-attribute-set" >
      <xsl:apply-templates mode="#current">
        <xsl:with-param name="document-id" select="@id" tunnel="yes"/>
      </xsl:apply-templates>
    </jaxb:bindings>
  </xsl:template>

  <xsl:template match="meta:generated-document-model" mode="document">
    <xsl:param name="document-id" tunnel="yes">unset</xsl:param>
    <xsl:comment>generated-document-model {@document-id}</xsl:comment>
    <annox:annotate>
      <annox:annotate annox:class="org.scapdev.content.annotation.SchemaDocument" id="{$document-id}" type="GENERATED"/>
    </annox:annotate>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="meta:document" mode="container">
    <xsl:comment>document <xsl:value-of select="@id"/></xsl:comment>
    <xsl:apply-templates mode="#current">
      <xsl:with-param name="document-id" select="@id" tunnel="yes"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="meta:entity-container" mode="container">
    <xsl:param name="document-id" tunnel="yes">unset</xsl:param>
    <xsl:comment>entity-container <xsl:value-of select="$document-id"/></xsl:comment>
    <jaxb:bindings xsl:use-attribute-sets="node-attribute-set">
      <jaxb:bindings node="{meta:entity-schema-node/@node}">
        <xsl:apply-templates mode="#current"/>
      </jaxb:bindings>
    </jaxb:bindings>
  </xsl:template>

  <xsl:template match="meta:generated-property" mode="container">
    <xsl:param name="document-id" tunnel="yes">unset</xsl:param>
    <xsl:comment>generated-property <xsl:value-of select="$document-id"/></xsl:comment>
    <jaxb:bindings xsl:use-attribute-sets="node-attribute-set">
      <annox:annotate target="field">
        <annox:annotate annox:class="org.scapdev.content.annotation.Generated"/>
      </annox:annotate>
    </jaxb:bindings>
  </xsl:template>

  <xsl:template match="meta:entity-schema-node" mode="container">
    <annox:annotate target="field">
      <annox:annotate annox:class="org.scapdev.content.annotation.EntityContainer" id="urn:scap-content:container:org.mitre.oval:definitions">
        <annox:annotate annox:field="entityIds">
          <xsl:for-each select="meta:entity-ref/@id-ref">
            <xsl:value-of select="."/>
            <xsl:if test="position() != last()">
              <xsl:text>,</xsl:text>
            </xsl:if>
          </xsl:for-each>
        </annox:annotate>
      </annox:annotate>
    </annox:annotate>
  </xsl:template>

  <xsl:template match="meta:entity" mode="entity">
    <xsl:comment>entity <xsl:value-of select="@id"/></xsl:comment>
    <jaxb:bindings xsl:use-attribute-sets="node-attribute-set">
      <annox:annotate>
        <annox:annotate annox:class="org.scapdev.content.annotation.Entity"
          id="{@id}"
          keyId="{meta:key/@id}">
          <xsl:if test="@local-part">
	        <annox:annotate annox:field="localPart"><xsl:value-of select="@local-part"/></annox:annotate>
	      </xsl:if>
        </annox:annotate>
      </annox:annotate>
      <xsl:apply-templates mode="#current"/>
    </jaxb:bindings>
  </xsl:template>

  <xsl:template match="meta:key" mode="entity">
    <annox:annotate>
      <annox:annotate annox:class="org.scapdev.content.annotation.Key" id="{@id}">
        <annox:annotate annox:field="keyIds">
          <xsl:for-each select="meta:field/@id">
            <xsl:value-of select="."/>
            <xsl:if test="position() != last()">
              <xsl:text>,</xsl:text>
            </xsl:if>
          </xsl:for-each>
        </annox:annotate>
      </annox:annotate>
    </annox:annotate>
    <xsl:apply-templates mode="#current"/>
  </xsl:template>

  <xsl:template match="meta:field" mode="entity">
    <jaxb:bindings xsl:use-attribute-sets="node-attribute-set">
      <annox:annotate target="field">
        <annox:annotate annox:class="org.scapdev.content.annotation.Field" id="{@id}"/>
      </annox:annotate>
    </jaxb:bindings>
  </xsl:template>

  <xsl:template match="meta:local-relationship" mode="relationship">
    <xsl:comment>local-relationship <xsl:value-of select="@id"/></xsl:comment>
    <jaxb:bindings xsl:use-attribute-sets="node-attribute-set">
      <xsl:apply-templates mode="#current"/>
    </jaxb:bindings>
  </xsl:template>

  <xsl:template match="meta:indirect-relationship" mode="relationship">
    <xsl:comment>indirect-relationship <xsl:value-of select="@id"/></xsl:comment>
    <jaxb:bindings xsl:use-attribute-sets="node-attribute-set">
      <annox:annotate>
        <annox:annotate annox:class="org.scapdev.content.annotation.Indirect" id="{@id}"/>
      </annox:annotate>
    </jaxb:bindings>
    <xsl:apply-templates mode="#current"/>
  </xsl:template>

  <xsl:template match="meta:key-ref" mode="relationship">
    <annox:annotate>
      <annox:annotate annox:class="org.scapdev.content.annotation.KeyRef" id="{@id}" keyId="{@id-ref}">
        <annox:annotate annox:field="fieldIds">
          <xsl:for-each select="meta:field-ref/@id">
            <xsl:value-of select="."/>
            <xsl:if test="position() != last()">
              <xsl:text>,</xsl:text>
            </xsl:if>
          </xsl:for-each>
        </annox:annotate>
      </annox:annotate>
    </annox:annotate>
    <xsl:apply-templates mode="#current"/>
  </xsl:template>

  <xsl:template match="meta:field-ref" mode="relationship">
    <jaxb:bindings xsl:use-attribute-sets="node-attribute-set">
      <annox:annotate target="field">
        <annox:annotate annox:class="org.scapdev.content.annotation.FieldRef" id="{@id}" idRef="{@id-ref}"/>
      </annox:annotate>
    </jaxb:bindings>
  </xsl:template>

  <xsl:template match="meta:indirect-relationship" mode="relationship">
    <xsl:comment>indirect-relationship <xsl:value-of select="@id"/></xsl:comment>
    <jaxb:bindings xsl:use-attribute-sets="node-attribute-set">
      <annox:annotate>
        <xsl:if test="meta:schema-node/@javaLocation = 'FIELD'">
          <xsl:attribute name="target">field</xsl:attribute>
        </xsl:if>
        <annox:annotate annox:class="org.scapdev.content.annotation.Indirect" id="{@id}">
          <annox:annotate annox:field="externalIdentifiers">
            <xsl:for-each select="meta:external-identifier-ref">
                <annox:annotate annox:class="org.scapdev.content.annotation.ExternalIdentifierRef" idRef="{@id-ref}">
                  <xsl:if test="@qualifier-value">
                    <annox:annotate annox:field="qualifier"><xsl:value-of select="@qualifier-value"/></annox:annotate>
                  </xsl:if>
                </annox:annotate>
            </xsl:for-each>
          </annox:annotate>
        </annox:annotate>
      </annox:annotate>
    </jaxb:bindings>
    <xsl:if test="meta:qualifier-node">
      <jaxb:bindings node="{meta:qualifier-node/@node}">
        <annox:annotate target="field">
          <annox:annotate annox:class="org.scapdev.content.annotation.IndirectQualifier"/>
        </annox:annotate>
      </jaxb:bindings>
    </xsl:if>
    <jaxb:bindings node="{meta:value-node/@node}">
      <annox:annotate target="field">
        <annox:annotate annox:class="org.scapdev.content.annotation.IndirectValue"/>
      </annox:annotate>
    </jaxb:bindings>
  </xsl:template>

  <xsl:attribute-set name="node-attribute-set">
    <xsl:attribute name="node" select="meta:schema-node/@node" />
  </xsl:attribute-set>

</xsl:stylesheet>

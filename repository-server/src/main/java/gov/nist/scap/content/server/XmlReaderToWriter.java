package gov.nist.scap.content.server;

/*   Copyright 2004 The Apache Software Foundation
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*  limitations under the License.
*/

//Revised from xmlbeans
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

public final class XmlReaderToWriter {
 private XmlReaderToWriter() {
 }

 public static void writeAll(XMLStreamReader xmlr, XMLStreamWriter writer)
     throws XMLStreamException {
   while (xmlr.hasNext()) {
     write(xmlr, writer);
     xmlr.next();
   }
   write(xmlr, writer); // write the last element
   writer.flush();
 }

 public static void write(XMLStreamReader xmlr, XMLStreamWriter writer) throws XMLStreamException {
   switch (xmlr.getEventType()) {
   case XMLEvent.START_ELEMENT:
     final String localName = xmlr.getLocalName();
     final String namespaceURI = xmlr.getNamespaceURI();
     if (namespaceURI != null && namespaceURI.length() > 0) {
       final String prefix = xmlr.getPrefix();
       if (prefix != null)
         writer.writeStartElement(prefix, localName, namespaceURI);
       else
         writer.writeStartElement(namespaceURI, localName);
     } else {
       writer.writeStartElement(localName);
     }

     for (int i = 0, len = xmlr.getNamespaceCount(); i < len; i++) {
       writer.writeNamespace(xmlr.getNamespacePrefix(i), xmlr.getNamespaceURI(i));
     }

     for (int i = 0, len = xmlr.getAttributeCount(); i < len; i++) {
       String attUri = xmlr.getAttributeNamespace(i);
       if (attUri != null && !attUri.isEmpty())
         writer.writeAttribute(attUri, xmlr.getAttributeLocalName(i), xmlr.getAttributeValue(i));
       else
         writer.writeAttribute(xmlr.getAttributeLocalName(i), xmlr.getAttributeValue(i));
     }
     break;
   case XMLEvent.END_ELEMENT:
     writer.writeEndElement();
     break;
   case XMLEvent.SPACE:
   case XMLEvent.CHARACTERS:
     writer.writeCharacters(xmlr.getTextCharacters(), xmlr.getTextStart(), xmlr.getTextLength());
     break;
   case XMLEvent.PROCESSING_INSTRUCTION:
     writer.writeProcessingInstruction(xmlr.getPITarget(), xmlr.getPIData());
     break;
   case XMLEvent.CDATA:
     writer.writeCData(xmlr.getText());
     break;

   case XMLEvent.COMMENT:
     writer.writeComment(xmlr.getText());
     break;
   case XMLEvent.ENTITY_REFERENCE:
     writer.writeEntityRef(xmlr.getLocalName());
     break;
   case XMLEvent.START_DOCUMENT:
	 // omit
     break;
   case XMLEvent.END_DOCUMENT:
	 // omit
     break;
   case XMLEvent.DTD:
     writer.writeDTD(xmlr.getText());
     break;
   }
 }
}
//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Speech-TTS
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

public class XmlDom {
    protected static Logger logger = LoggerFactory.getLogger(XmlDom.class);

    private static String jstname = "scrxkj";

    private static String jstpwd = "XuqBJIoC";

    private static String opKind = "51";


    /**
     * <speak encoding="iso-8859-1" version="1.0" xml:lang="ar-EG">
     *     <voice name="Microsoft Server Speech Text to Speech Voice (ar-EG, Hoda)" xml:gender="Female" xml:lang="ar-EG">
     *         <prosody rate="-16%">إنه يوم جميل اليوم</prosody>
     *     </voice>
     * </speak>
     * @param name
     * @param pwd
     * @param opkind
     * @param phonenums
     * @param content
     * @return
     */
    public static String createDom(String name, String pwd, String opkind, List<String> phonenums, String content) {
        Document doc = null;
        Element Group, E_Time, Mobile, Content, ClientID;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            doc = builder.newDocument();
            if (doc != null) {
                Group = doc.createElement("Group");
                Group.setAttribute("Login_Name",name);
                Group.setAttribute("Login_Pwd", pwd);
                Group.setAttribute("OpKind", opkind);
                Group.setAttribute("InterFaceID", "");
                Group.setAttribute("SerType", "");
                E_Time = doc.createElement("E_Time");
                E_Time.appendChild(doc.createTextNode(AlexUtil.formatDatealex(new Date())));
                Mobile = doc.createElement("Mobile");
                String phones = "";
                for (String str : phonenums){
                    phones += str;
                }
                Mobile.appendChild(doc.createTextNode(phones));
                Content = doc.createElement("Content");
                Content.appendChild(doc.createTextNode(content));
                ClientID = doc.createElement("ClientID");
                ClientID.appendChild(doc.createTextNode(System.currentTimeMillis() + ""));

                Group.appendChild(E_Time);
                Group.appendChild(Mobile);
                Group.appendChild(Content);
                Group.appendChild(ClientID);
                doc.appendChild(Group);
            }
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }
        return transformDom(doc);
    }

    private static String transformDom(Document doc) {
        StringWriter writer = new StringWriter();
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer;
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }
        return writer.getBuffer().toString().replaceAll("\n|\r", "");
    }
}

/**
 * Copyright (C) 2018-2020 toop.eu
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.dsd.service;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.w3c.dom.Document;

import eu.toop.edm.xml.cagv.CCAGV;
import eu.toop.regrep.ERegRepResponseStatus;
import eu.toop.regrep.RegRep4Writer;
import eu.toop.regrep.RegRepHelper;
import eu.toop.regrep.SlotBuilder;
import eu.toop.regrep.query.QueryResponse;
import eu.toop.regrep.rim.RegistryObjectListType;
import eu.toop.regrep.rim.RegistryObjectType;

/**
 * A simple RegRep helper that wraps data into RegRep slots.
 *
 * @author yerlibilgin
 */
public class DSDRegRep
{
  /**
   * Wrap the given list of <code>org.w3c.dom.Document</code> objects into
   * RegRep slots of <code>AnyValueType</code> and returns the created
   * <code>QueryResponse</code> as a <code>String</code>
   *
   * @param sRequestID
   *        the request identifier, obtained from the QueryRequest
   * @param dcatDocuments
   *        the list of dcat documents to be contained in slots
   * @return the QueryResponse as a String
   */
  public static String createQueryResponse (final String sRequestID, final List <Document> dcatDocuments)
  {

    final QueryResponse aQResponse = RegRepHelper.createQueryResponse (ERegRepResponseStatus.SUCCESS, sRequestID);

    RegistryObjectListType registryObjectListType = new RegistryObjectListType();
    dcatDocuments.forEach (dcatDocument -> {
      RegistryObjectType registryObjectType = new RegistryObjectType();
      registryObjectType.setId(UUID.randomUUID().toString());
      registryObjectType.addSlot (new SlotBuilder ().setName ("Dataset")
                                            .setValue (dcatDocument.getDocumentElement ())
                                            .build ());
      registryObjectListType.addRegistryObject(registryObjectType);
    });

    aQResponse.setRegistryObjectList(registryObjectListType);
    aQResponse.setTotalResultCount (BigInteger.valueOf (dcatDocuments.size ()));
    aQResponse.setStartIndex (BigInteger.ZERO);
    // Additional XSDs are required for xsi:type
    return RegRep4Writer.queryResponse (CCAGV.XSDS).setFormattedOutput (true).getAsString (aQResponse);
  }
}

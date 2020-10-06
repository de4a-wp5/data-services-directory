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


import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import com.helger.commons.io.stream.StreamHelper;
import org.junit.Assert;
import org.junit.Test;

import com.helger.commons.datetime.PDTFactory;
import com.helger.pd.searchapi.PDSearchAPIReader;
import com.helger.pd.searchapi.PDSearchAPIWriter;
import com.helger.pd.searchapi.v1.MatchType;
import com.helger.pd.searchapi.v1.ResultListType;

import eu.toop.dsd.api.DsdResponseReader;
import eu.toop.dsd.api.DsdResponseWriter;
import eu.toop.dsd.api.types.DoctypeParts;

/**
 * The type Dsd tests.
 *
 * @author yerlibilgin
 */
public class DSDTests {

  /**
   * Test convert match types.
   */
  @Test
  public void testConvertMatchTypes() {

    String xml = StreamHelper.getAllBytesAsString(DSDTests.class.getResourceAsStream("/directory-results.xml"), StandardCharsets.UTF_8);
    String resultXml = DsdResponseWriter.convertDIRToDSD(xml, null);
    System.out.println(resultXml);
  }

  /**
   * Test convert single match type.
   */
  @Test
  public void testConvertSingleMatchType() {
    String xml = StreamHelper.getAllBytesAsString(DSDTests.class.getResourceAsStream("/directory-result-single.xml"), StandardCharsets.UTF_8);
    final String dataSetType = "REGISTERED_ORGANIZATION";

    String resultXml = DsdResponseWriter.convertDIRToDSD(xml, dataSetType);
    System.out.println(resultXml);
  }


  /**
   * Write read.
   *
   * @throws DatatypeConfigurationException the datatype configuration exception
   */
  @Test
  public void writeRead() {
    String xml = StreamHelper.getAllBytesAsString(DSDTests.class.getResourceAsStream("/directory-result-single.xml"), StandardCharsets.UTF_8);
    final String s_dataSetType = "S";

    String resultXml = DsdResponseWriter.convertDIRToDSD(xml, s_dataSetType);
    System.out.println(resultXml);

    List<MatchType> matchTypeList = DsdResponseReader.matchTypeListReader().read(resultXml);


    ResultListType rls = new ResultListType();
    rls.setVersion("1");
    rls.setQueryTerms("terms");
    rls.setCreationDt(PDTFactory.getCurrentLocalDateTime());
    matchTypeList.forEach(matchType -> {
      rls.addMatch(matchType);
    });


    System.out.println(PDSearchAPIWriter.resultListV1().setFormattedOutput(true).getAsString(rls));
  }

  /**
   * Test doctype parts.
   */
  @Test
  public void testDoctypeParts() {
    DoctypeParts parts = DoctypeParts.parse(
        "toop-doctypeid-qns::RegisteredOrganization::REGISTERED_ORGANIZATION_TYPE::CONCEPT##CCCEV::toop-edm:v2.0");
    Assert.assertNotNull(parts);

    Assert.assertEquals("RegisteredOrganization", parts.getDataSetIdentifier());
    Assert.assertEquals("REGISTERED_ORGANIZATION_TYPE", parts.getDatasetType());
    Assert.assertEquals("CONCEPT", parts.getDistributionFormat());
    Assert.assertEquals("CCCEV", parts.getDistributionConformsTo());
    Assert.assertEquals("toop-edm:v2.0", parts.getConformsTo());
  }

  /**
   * Test doctype parts old doc type.
   */
  @Test
  public void testDoctypePartsOldDocType() {
    DoctypeParts parts = DoctypeParts.parse(
        "toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-1p40::Request##urn:eu.toop.request.registeredorganization::1.40");
    Assert.assertNotNull(parts);

    Assert.assertEquals("urn:eu:toop:ns:dataexchange-1p40", parts.getDatasetType());
    Assert.assertEquals("Request", parts.getDistributionFormat());
    Assert.assertEquals("urn:eu.toop.request.registeredorganization", parts.getDistributionConformsTo());
    Assert.assertEquals("1.40", parts.getConformsTo());
  }
}



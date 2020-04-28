package eu.toop.dsd.service;


import com.helger.commons.io.resource.ClassPathResource;
import com.helger.pd.searchapi.PDSearchAPIReader;
import com.helger.pd.searchapi.PDSearchAPIWriter;
import com.helger.pd.searchapi.v1.ResultListType;
import eu.toop.regrep.CRegRep4;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BRegDcatHelperTest {

  @Test
  public void testConvertMatchTypes() throws Exception{

    CRegRep4.allXSDsQueryList.addAll(CRegRep4.getAllXSDIncludes());
    CRegRep4.allXSDsQueryList.add(CRegRep4.getXSDResourceQuery());
    //FIXME muhammet: I added these here because it wasn't being able to
    // serialize the cagv:PublicOrganizationType as dct:publisher because
    // it couldn't validate against the xsd (not being able to find it).
    // however this is the 'regrep' module and these xsds shouldn't be
    // here. We need to find a better place for them.
    CRegRep4.allXSDsQueryList.add(new ClassPathResource("schemas/CV-Agent.xsd",CRegRep4.class.getClassLoader()));
    CRegRep4.allXSDsQueryList.add(new ClassPathResource("schemas/dcterms.xsd",CRegRep4.class.getClassLoader()));
    CRegRep4.allXSDsQueryList.add(new ClassPathResource("schemas/locn.xsd", CRegRep4.class.getClassLoader()));

    final ResultListType read = PDSearchAPIReader.resultListV1().read(BRegDcatHelperTest.class.getResourceAsStream("/directory-results.xml"));
    final List<Document> documents = BregDCatHelper.convertBusinessCardsToDCat(read.getMatch());




    String resultXml = DSDRegRep.createQueryResponse(UUID.randomUUID().toString(), documents);

    System.out.println(resultXml);

    //try(PrintWriter pw = new PrintWriter("tmp.xml")){
    //  pw.println(resultXml);
    //}
  }


  @Ignore
  @Test
  public void saveSearches() throws IOException {
    final ResultListType resultListType = ToopDirClient.performSearchResultsLists(null, null);
    PDSearchAPIWriter p = PDSearchAPIWriter.resultListV1();
    p.setFormattedOutput(true);

    try (StringWriter writer = new StringWriter()) {
      p.write(resultListType, writer);
      System.out.println("Writer: " + writer.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  public static void main(String[] args) throws IOException {

    Map<String, String[]> paramMap = new HashMap<>();

    //paramMap.put(DSDQueryService.PARAM_NAME_DATA_SET_TYPE, new String[]{"REGISTERED_ORGANIZATION_TYPE"});
    //paramMap.put(DSDQueryService.PARAM_NAME_COUNTRY_CODE, new String[]{"GQ"});

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    DSDQueryService.processDataSetRequest(paramMap, os);

    System.out.println(new String(os.toByteArray()));
    try (FileOutputStream xml = new FileOutputStream("sampleresponse.xml")) {
      xml.write(os.toByteArray());
    }
  }
}



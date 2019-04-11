package com.eresearch.dblp.consumer.it;


import com.eresearch.dblp.consumer.EresearchDblpConsumerApplication;
import com.eresearch.dblp.consumer.core.FileSupport;
import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = EresearchDblpConsumerApplication.class,
        properties = {"application.properties"}
)

@RunWith(SpringRunner.class)
public class SpecificationIT {


    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(1234);

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    public void dblp_consumption_works_as_expected() throws Exception {
        Assert.assertEquals(1, 1);

        // given
        String dblpConsumerDtoAsString = FileSupport.getResource("test/first_case_input.json");

        DblpConsumerDto dblpConsumerDto = objectMapper.readValue(dblpConsumerDtoAsString, DblpConsumerDto.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        HttpEntity<DblpConsumerDto> httpEntity = new HttpEntity<>(dblpConsumerDto, httpHeaders);

        mockDblp();


        // when
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/dblp-consumer/find",
                HttpMethod.POST,
                httpEntity,
                String.class);


        // then
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        Assert.assertNotNull(responseEntity.getBody());

        String expected = FileSupport.getResource("test/first_case_output.json");


        JSONAssert.assertEquals(expected, responseEntity.getBody(),
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("process-finished-date", (o1, o2) -> true)));

    }

    //TODO
    @Test
    public void dblp_consumption_works_as_expected_async_queue() {
        Assert.assertEquals(1, 1);


        // given


        // when


        // then

    }

    private void mockDblp() {
        // 1 hit: getDblpAuthors
        stubFor(get(urlEqualTo("/search/author?xauthor=Christos%20Skourlas"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpAuthors_Christos Skourlas.xml"))));

        // 2 hit: getDblpAuthorPublications
        stubFor(get(urlEqualTo("/rec/pers/s/Skourlas:Christos/xk"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpAuthorPublications_s$Skourlas:Christos.xml"))));

        // 3 now for every returned publication hit: getDblpEntry
        stubFor(get(urlEqualTo("/rec/bibtex/conf/nlucs/FragosMS04.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$nlucs$FragosMS04.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/mie/BelsisVSP08.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$mie$BelsisVSP08.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/FragosS16.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$FragosS16.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/VassisBSMT13.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$VassisBSMT13.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/Anagnostopoulos18.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$Anagnostopoulos18.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/nlucs/FragosS06a.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$nlucs$FragosS06a.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/jsit/VosMTEGS14.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$jsit$VosMTEGS14.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/jsit/MetalidouMTEGS14.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$jsit$MetalidouMTEGS14.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/series/sci/BelsisKMPS08.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_series$sci$BelsisKMPS08.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/StylianidisGSS17.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$StylianidisGSS17.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/SkourlasDL15.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$SkourlasDL15.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/jis/KaranikolasS10.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$jis$KaranikolasS10.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/adbis/KaranikolasNYS07.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$adbis$KaranikolasNYS07.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/trustbus/BelsisGST07.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$trustbus$BelsisGST07.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/ijapuc/BelsisSG13.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$ijapuc$BelsisSG13.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/FragosS15.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$FragosS15.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/iceis/BelsisGSD04.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$iceis$BelsisGSD04.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/TsoukalasKS13.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$TsoukalasKS13.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/iwuc/BelsisMGSC05.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$iwuc$BelsisMGSC05.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/iceis/KaranikolasS03.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$iceis$KaranikolasS03.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/BelsisGMSV12.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$BelsisGMSV12.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/jcs/BelsisFGS09.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$jcs$BelsisFGS09.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/kes/PaliourasANAS06.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$kes$PaliourasANAS06.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/ijmbl/MarinagiS13.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$ijmbl$MarinagiS13.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/jsit/AnagnostopoulosS14.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$jsit$AnagnostopoulosS14.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/jss/KaranikolasNYS09.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$jss$KaranikolasNYS09.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/iceis/AlevizosKPSB07.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$iceis$AlevizosKPSB07.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/sac/BelsisFGS06.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$sac$BelsisFGS06.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/scn/BelsisVS11.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$scn$BelsisVS11.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/KampourakiVBS15.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$KampourakiVBS15.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/trustbus/BelsisGMSC05.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$trustbus$BelsisGMSC05.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/jcs/BelsisFGS08.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$jcs$BelsisFGS08.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/petra/VassisBSG09.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$petra$VassisBSG09.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/FragosS14.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$FragosS14.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/wscg/MiaoulisPMS00.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$wscg$MiaoulisPMS00.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/isca/BelsisKMPS12.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$isca$BelsisKMPS12.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/puc/VassisBSP10.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$puc$VassisBSP10.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/Anagnostopoulos17.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$Anagnostopoulos17.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/iceis/MarinagiAKS06.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$iceis$MarinagiAKS06.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/ijpcc/MalatrasPBGSC05.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$ijpcc$MalatrasPBGSC05.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/petra/ZafeirisBS13.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$petra$ZafeirisBS13.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/iceis/KaranikolasSNY08.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$iceis$KaranikolasSNY08.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/nlucs/FragosS06.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$nlucs$FragosS06.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/petra/VassisBSP08.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$petra$VassisBSP08.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/iceis/HadjidiakosSA04.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$iceis$HadjidiakosSA04.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pakm/BelsisGMSC04.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pakm$BelsisGMSC04.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/VassisBS12.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$VassisBS12.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/icann/VassilasS06.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$icann$VassilasS06.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/homepages/65/4173.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_homepages$65$4173.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/nlucs/FragosMS05.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$nlucs$FragosMS05.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/journals/jitr/BelsisSG11.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_journals$jitr$BelsisSG11.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/asian/BelsisGST07.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$asian$BelsisGST07.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/icps/MalatrasPBGSC05.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$icps$MalatrasPBGSC05.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/petra/SkourlasBSTVM09.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$petra$SkourlasBSTVM09.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/VassisZSB12.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$VassisZSB12.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/GalliakisSGV18.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$GalliakisSGV18.xml"))));

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/MarinagiSG18.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$MarinagiSG18.xml"))));
    }

}

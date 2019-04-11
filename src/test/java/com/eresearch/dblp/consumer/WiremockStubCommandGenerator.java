package com.eresearch.dblp.consumer;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class WiremockStubCommandGenerator {


    public static void main(String[] args) throws Exception {

        /*

        stubFor(get(urlEqualTo("/rec/bibtex/conf/pci/Anagnostopoulos18.xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody(FileSupport.getResource("test/dblp_capture/getDblpEntry_conf$pci$Anagnostopoulos18.xml"))));


         */


        String blueprintCommand = "stubFor(get(urlEqualTo(\"/rec/bibtex/URL\"))\n" +
                "                .willReturn(aResponse()\n" +
                "                        .withStatus(200)\n" +
                "                        .withHeader(\"Content-Type\", \"text/xml\")\n" +
                "                        .withBody(FileSupport.getResource(\"test/dblp_capture/FILENAME\"))));";


        URI uri = WiremockStubCommandGenerator.class.getClassLoader().getResource("test/dblp_capture").toURI();


        Path path = Paths.get(uri);

        //Files.walk(path).forEach(System.out::println);

        for (Path p : Files.walk(path).collect(Collectors.toList())) {

            Path fileName = p.getName(p.getNameCount() - 1);

            String filenameAsString = fileName.toString();

            if (filenameAsString.contains("capture")) {
                continue;
            }

            //System.out.println(filenameAsString);


            String temp = blueprintCommand.replace("FILENAME", filenameAsString);


            String url = filenameAsString.split("_")[1].replaceAll("\\$", "/");

            temp = temp.replace("URL", url);

            //System.out.println(url);


            System.out.println(temp);
            System.out.println();
        }

    }

}

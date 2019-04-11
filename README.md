# Eresearch Dblp Consumer Service #

### Description

The purpose of this service is to consume the info which DBLP service provides.
See dblp.dtd at http://dblp.org/xml/

We generate POJO classes from dblp.dtd using JAXB binding compiler (xjc -dtd -d generatedsrc -p com.examples dblp.dtd).


We get the info for the 'requested author' in the three following steps:

First we hit the url: "http://dblp.uni-trier.de/search/author?xauthor=" + authorName (eg: http://dblp.uni-trier.de/search/author?xauthor=%20Christos%20Skourlas).

Secondly for each entry of the first url we hit: "http://dblp.uni-trier.de/rec/pers/" + urlpt + "/xk" (eg: http://dblp.uni-trier.de/rec/pers/s/Skourlas:Christos/xk).

Thirdly (and finally) for each entry of the second url we hit: "http://dblp.uni-trier.de/rec/bibtex/" + dblpKeyValue + ".xml" (eg: http://dblp.uni-trier.de/rec/bibtex/conf/petra/SkourlasBSTVM09.xml)
which returns a detailed dblp entry (which is what we are looking for) then we return all these dblp entries to the http response (or to a jms queue).


### External Dependencies needed in order to run service

* MySQL && ActiveMQ
    * Execute: `docker-compose up` in order to have a running instance of MySQL DB.
    * Execute: `docker-compose down` in order to shutdown MySQL DB.


### Integration Tests (run docker-compose first)

* Execute: `mvn clean verify`


### Create Docker Image
* Execute: `mvn clean install -DskipITs=true`
* Execute: `docker build -t chriniko/eresearch-dblp-consumer:1.0 .` in order to build docker image.

* Fast: `mvn clean install -DskipITs=true && docker build -t chriniko/eresearch-dblp-consumer:1.0 .`


### How to run service (not dockerized)
* Execute: `docker-compose up`

* Two options:
    * Execute: 
        * `mvn clean install -DskipITs=true`
        * `java -jar -Dspring.profiles.active=dev target/eresearch-dblp-consumer-1.0-boot.jar`
                
    * Execute:
        * `mvn spring-boot:run -Dspring.profiles.active=dev`

* (Optional) When you finish: `docker-compose down`


### How to run service (dockerized)
* Uncomment the section in `docker-compose.yml` file for service: `eresearch-dblp-consumer:`

* Execute: `mvn clean install -DskipITs=true`

* Execute: `docker-compose build`

* Execute: `docker-compose up`

* (Optional) When you finish: `docker-compose down`


### Example Request

```json

{
	"firstname":"Christos",
	"initials":"",
	"surname":"Skou"
}

```


### Example Response

```json

{
  "operation-result": true,
  "process-finished-date": "2019-04-11T07:42:17.026Z",
  "requested-dblp-consumer-dto": {
    "firstname": "Christos",
    "initials": "",
    "surname": "Skourlas"
  },
  "fetched-results-size": 1,
  "fetched-results": {
    "authorName=Christos Skourlas#urlpt=s/Skourlas:Christos": [
      {
        "mdate": null,
        "articles": null,
        "inproceedings": null,
        "proceedings": null,
        "books": null,
        "incollections": null,
        "phdthesis": null,
        "mastersthesis": null,
        "www": [
          {
            "key": "homepages/65/4173",
            "mdate": "2009-06-10",
            "publtype": null,
            "cdate": null,
            "authors": [
              {
                "aux": null,
                "bibtex": null,
                "orcid": null,
                "value": "Christos Skourlas"
              }
            ],
            "editors": null,
            "titles": [
              {
                "bibtex": null,
                "value": "Home Page"
              }
            ],
            "booktitles": null,
            "pages": null,
            "years": null,
            "addresses": null,
            "journals": null,
            "volumes": null,
            "numbers": null,
            "months": null,
            "urls": null,
            "ees": null,
            "cdroms": null,
            "cites": null,
            "publishers": null,
            "notes": null,
            "crossrefs": null,
            "isbns": null,
            "series": null,
            "schools": null,
            "chapters": null,
            "publnrs": null
          }
        ],
        "persons": null,
        "data": null
      },
      {
        "mdate": null,
        "articles": null,
        "inproceedings": [
          {
            "key": "conf/pci/Anagnostopoulos18",
            "mdate": "2019-01-09",
            "publtype": null,
            "cdate": null,
            "authors": [
              {
                "aux": null,
                "bibtex": null,
                "orcid": null,
                "value": "Theodoros Anagnostopoulos"
              },
              {
                "aux": null,
                "bibtex": null,
                "orcid": null,
                "value": "Klimis S. Ntalianis"
              },
              {
                "aux": null,
                "bibtex": null,
                "orcid": null,
                "value": "Christos Skourlas"
              },
              {
                "aux": null,
                "bibtex": null,
                "orcid": null,
                "value": "S. R. Jino Ramson"
              }
            ],
            "editors": null,
            "titles": [
              {
                "bibtex": null,
                "value": "IoT-enabled fall verification of elderly and impaired people in smart cities."
              }
            ],
            "booktitles": [
              {
                "value": "PCI"
              }
            ],
            "pages": [
              {
                "type": null,
                "value": "88-92"
              }
            ],
            "years": [
              {
                "value": "2018"
              }
            ],
            "addresses": null,
            "journals": null,
            "volumes": null,
            "numbers": null,
            "months": null,
            "urls": [
              {
                "aux": null,
                "value": "db/conf/pci/pci2018.html#Anagnostopoulos18"
              }
            ],
            "ees": [
              {
                "aux": null,
                "value": "https://doi.org/10.1145/3291533.3291553"
              }
            ],
            "cdroms": null,
            "cites": null,
            "publishers": null,
            "notes": null,
            "crossrefs": [
              {
                "value": "conf/pci/2018"
              }
            ],
            "isbns": null,
            "series": null,
            "schools": null,
            "chapters": null,
            "publnrs": null
          }
        ],
        "proceedings": null,
        "books": null,
        "incollections": null,
        "phdthesis": null,
        "mastersthesis": null,
        "www": null,
        "persons": null,
        "data": null
      },
      
      
      ......
      
```

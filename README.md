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
* Execute: `mvn clean install -DskipITs`
* Execute: `docker build -t chriniko/eresearch-dblp-consumer:1.0 .` in order to build docker image.

* Fast: `mvn clean install -DskipITs && docker build -t chriniko/eresearch-dblp-consumer:1.0 .`


### How to run service (not dockerized)
* Execute: `docker-compose up`

* Two options:
    * Execute: 
        * `mvn clean install -DskipITs`
        * `java -jar -Dspring.profiles.active=dev target/eresearch-dblp-consumer-1.0-boot.jar`
                
    * Execute:
        * `mvn spring-boot:run -Dspring.profiles.active=dev`

* (Optional) When you finish: `docker-compose down`


### How to run service (dockerized)
* Uncomment the section in `docker-compose.yml` file for service: `eresearch-dblp-consumer:`

* Execute: `mvn clean install -DskipITs`

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
  "process-finished-date": "2017-06-21T18:54:33.574Z",
  "requested-dblp-consumer-dto": {
    "firstname": "Anastasios",
    "initials": "",
    "surname": "Tsolakidis"
  },
  "fetched-results-size": 1,
  "fetched-results": {
    "authorName=Anastasios Tsolakidis#urlpt=t/Tsolakidis:Anastasios": [
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
            "key": "homepages/04/7556",
            "mdate": "2009-11-24",
            "publtype": null,
            "cdate": null,
            "authors": [
              {
                "aux": null,
                "bibtex": null,
                "orcid": null,
                "value": "Anastasios Tsolakidis"
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
            "key": "conf/educon/TsolakidisTSC17",
            "mdate": "2017-06-14",
            "publtype": null,
            "cdate": null,
            "authors": [
              {
                "aux": null,
                "bibtex": null,
                "orcid": null,
                "value": "Anastasios Tsolakidis"
              },
              {
                "aux": null,
                "bibtex": null,
                "orcid": null,
                "value": "Evangelia Triperina"
              },
              {
                "aux": null,
                "bibtex": null,
                "orcid": null,
                "value": "Cleo Sgouropoulou"
              },
              {
                "aux": null,
                "bibtex": null,
                "orcid": null,
                "value": "Nikos Christidis"
              }
            ],
            "editors": null,
            "titles": [
              {
                "bibtex": null,
                "value": "Measuring academic research impact based on open data: A case of engineering faculties."
              }
            ],
            "booktitles": [
              {
                "value": "EDUCON"
              }
            ],
            "pages": [
              {
                "type": null,
                "value": "1611-1618"
              }
            ],
            "years": [
              {
                "value": "2017"
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
                "value": "db/conf/educon/educon2017.html#TsolakidisTSC17"
              }
            ],
            "ees": [
              {
                "aux": null,
                "value": "https://doi.org/10.1109/EDUCON.2017.7943064"
              }
            ],
            "cdroms": null,
            "cites": null,
            "publishers": null,
            "notes": null,
            "crossrefs": [
              {
                "value": "conf/educon/2017"
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

# jfrog
Finds the most popular and the 2nd most popular jar file (artifact) in a
maven repository. The most popular artifact will be the one with highest number of downloads.

This is a maven project

How to run:
1. mvn clean install
2. run the jar
3. hit the url: /api/artifacts (example: http://localhost:8080/api/artifacts) 

You would see an out put as below:

{
artifactsDto: [
{
rank: 1,
name: "http://35.222.197.119:80/artifactory/jcenter-cache/asm/asm-parent/3.3/asm-parent-3.3.pom",
count: 31
},
{
rank: 2,
name: "http://35.222.197.119:80/artifactory/jcenter-cache/commons-codec/commons-codec/1.2/commons-codec-1.2.pom",
count: 30
}
]
}

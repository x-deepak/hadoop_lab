
# Steps to execute the program

!!!Do not download the BX-Books.csv file - its too large. Use Books-mini.csv instead!!!

Download the .java and input file (.txt or .csv) to your machine in a folder

### 1.  Compile .java files:
```
mkdir classes
javac -cp $(hadoop classpath) -d classes BookPublicationFrequency.java
```

### 2.  Create Jar file using the compiled classes:
```
jar -cvf BookPublicationFrequency.jar -C classes .
```

### 3.  Upload input file to Hadoop (Hadoop server should be running i.e. start-all.sh):
```
hdfs dfs -mkdir /input
hdfs dfs -mkdir /output

hdfs dfs -put Books-mini.csv /input
```


### 4. Run Jar in Hadoop Cluster :

```
hadoop jar BookPublicationFrequency.jar BookPublicationFrequency /input/Books-mini.csv /output/lab10
```

### 5. View Output:
In Terminal: 
```
hdfs dfs -cat /output/lab10/part-*
```

Or Navigate to the output file in the browser:
```
http://localhost:9870/explorer.html#/
```



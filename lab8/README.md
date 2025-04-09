
# Steps to execute the program

Download the .java and input file (.txt or .csv) to your machine in a folder

### 1.  Compile .java files:
```
mkdir classes
javac -cp $(hadoop classpath) -d classes ProductSalesAnalyzer.java
```

### 2.  Create Jar file using the compiled classes:
```
jar -cvf ProductSalesAnalyzer.jar -C classes .
```

### 3.  Upload input file to Hadoop (Hadoop server should be running i.e. start-all.sh):
```
hdfs dfs -mkdir /input
hdfs dfs -mkdir /output

hdfs dfs -put productsSold_dataset.txt /input
```


### 4. Run Jar in Hadoop Cluster :

```
hadoop jar ProductSalesAnalyzer.jar ProductSalesAnalyzer /input/productsSold_dataset.txt /output/lab8
```

### 5. View Output:
In Terminal: 
```
hdfs dfs -cat /output/lab8/part-*
```

Or Navigate to the output file in the browser:
```
http://localhost:9870/explorer.html#/
```



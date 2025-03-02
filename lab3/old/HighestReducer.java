import java.io.IOException;

import java.util.Iterator;

import org.apache.hadoop.io.*;

import org.apache.hadoop.mapred.*;

public class HighestReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>

{

  public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException

      {

     int max_temp = 0; 

               while (values.hasNext())

                      {

         int current=values.next().get();

                         if ( max_temp <  current)  

                        max_temp =  current;

                      }

          output.collect(key, new IntWritable(max_temp/10));

      }

}

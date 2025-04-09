import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class BookPublicationFrequency {
    // Mapper Class
    public static class PublicationYearMapper extends Mapper<Object, Text, Text, IntWritable> {
        private static final int YEAR_INDEX = 3; // Index of the publication year field

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            
            String line = value.toString();
            if (line.startsWith("ISBN")) return;


            String[] fields = line.split(",");
            if (fields.length > YEAR_INDEX) {
                    String year = fields[YEAR_INDEX].trim();
                    context.write(new Text(year), new IntWritable(1));
            }
        }
    }

    // Reducer Class
    public static class PublicationFrequencyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable value : values) {
                count += value.get();
            }
            context.write(key, new IntWritable(count));
        }
    }

    // Driver Method
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "book publication frequency");
        job.setJarByClass(BookPublicationFrequency.class);
        job.setMapperClass(PublicationYearMapper.class);
        job.setReducerClass(PublicationFrequencyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0])); // Input path
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output path
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

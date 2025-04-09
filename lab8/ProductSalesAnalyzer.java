import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ProductSalesAnalyzer {
    // Mapper Class
    public static class SalesMapper extends Mapper<Object, Text, Text, IntWritable> {
        private static final int COUNTRY_INDEX = 8; // Country field index

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // Skip header
            String line = value.toString();
            if (value.startsWith("Transaction")) return;

            String[] fields = line.split(",");
            if (fields.length > COUNTRY_INDEX) {
                String country = fields[COUNTRY_INDEX].trim();
                context.write(new Text(country), new IntWritable(1));
            }
        }
    }

    // Reducer Class
    public static class SalesReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable value : values) {
                sum += value.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }

    // Driver Method
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "product sales analyzer");
        job.setJarByClass(ProductSalesAnalyzer.class);
        job.setMapperClass(SalesMapper.class);
        job.setReducerClass(SalesReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0])); // Input path
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output path
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

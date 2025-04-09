import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class UberTripAnalyzer {

    // Mapper
    public static class TripMapper extends Mapper<LongWritable, Text, Text, Text> {
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            // Skip header
            if (line.startsWith("dispatching_base_number")) return;

            String[] fields = line.split(",");
            if (fields.length >= 4) {
                String basement = fields[0].trim();
                String date = fields[1].trim();
                String trips = fields[3].trim();
                context.write(new Text(date), new Text(basement + "," + trips));
            }
        }
    }

    // Reducer
    public static class TripReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            int maxTrips = Integer.MIN_VALUE;
            String maxBasement = "";

            for (Text val : values) {
                String[] parts = val.toString().split(",");
                String basement = parts[0];
                int trips = Integer.parseInt(parts[1]);

                if (trips > maxTrips) {
                    maxTrips = trips;
                    maxBasement = basement;
                }
            }
            context.write(key, new Text(maxBasement + "\t" + maxTrips));
        }
    }

    // Driver
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "uber trip analyzer");

        job.setJarByClass(UberTripAnalyzer.class);
        job.setMapperClass(TripMapper.class);
        job.setReducerClass(TripReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

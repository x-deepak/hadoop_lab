import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HighestTemperature {

    // Mapper class
    public static class HighestMapper extends Mapper<Object, Text, Text, IntWritable> {
        @Override
        protected void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            String line = value.toString().trim();
            String[] parts = line.split("\\s+");  // Assuming space-separated input

            if (parts.length == 2) {
                String year = parts[0];
                try {
                    int temperature = Integer.parseInt(parts[1]);
                    context.write(new Text(year), new IntWritable(temperature));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid temperature value: " + parts[1]);
                }
            } else {
                System.err.println("Skipping malformed line: " + line);
            }
        }
    }

    // Reducer class
    public static class HighestReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            int maxTemp = Integer.MIN_VALUE;

            for (IntWritable val : values) {
                maxTemp = Math.max(maxTemp, val.get());
            }

            context.write(key, new IntWritable(maxTemp));
        }
    }

    // Driver
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Highest Temperature Per Year");

        job.setJarByClass(HighestTemperature.class);
        job.setMapperClass(HighestMapper.class);
        job.setReducerClass(HighestReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean success = job.waitForCompletion(true);
        System.exit(success ? 0 : 1);
    }
}

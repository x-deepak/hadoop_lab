import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class HighestTemperature extends Configured implements Tool {

    public static class HighestMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            String line = value.toString().trim();
            String[] parts = line.split("\\s+");  // Splitting on whitespace

            if (parts.length == 2) { // Ensure at least 2 columns exist
                String year = parts[0];
                try {
                    int temperature = Integer.parseInt(parts[1]);
                    output.collect(new Text(year), new IntWritable(temperature));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid temperature value: " + parts[1]);
                }
            } else {
                System.err.println("Skipping malformed line: " + line);
            }
        }
    }

    public static class HighestReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            int max_temp = Integer.MIN_VALUE; // Start with lowest possible value

            while (values.hasNext()) {
                int current = values.next().get();
                if (max_temp < current) max_temp = current;
            }

            output.collect(key, new IntWritable(max_temp / 10));
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: HighestTemperature <input path> <output path>");
            return -1;
        }

        JobConf conf = new JobConf(getConf(), HighestTemperature.class);
        conf.setJobName("Highest Temperature Per Year");

        conf.setMapperClass(HighestMapper.class);
        conf.setReducerClass(HighestReducer.class);

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        JobClient.runJob(conf);
        return 0;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new HighestTemperature(), args);
        System.exit(exitCode);
    }
}

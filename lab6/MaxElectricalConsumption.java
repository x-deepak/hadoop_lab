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
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MaxElectricalConsumption {

    // Mapper Class
    public static class MaxConsumptionMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] tokens = value.toString().split(",");
            
            if (tokens.length == 3) {
                try {
                    int year = Integer.parseInt(tokens[0].trim());
                    int consumption = Integer.parseInt(tokens[2].trim());
                    context.write(new IntWritable(year), new IntWritable(consumption));
                } catch (NumberFormatException e) {
                    // Ignore malformed lines
                }
            }
        }
    }

    // Reducer Class
    public static class MaxConsumptionReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        @Override
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int maxConsumption = Integer.MIN_VALUE;
            
            for (IntWritable value : values) {
                maxConsumption = Math.max(maxConsumption, value.get());
            }
            
            context.write(key, new IntWritable(maxConsumption));
        }
    }

    // Driver Method
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: MaxElectricalConsumption <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Max Electrical Consumption");

        job.setJarByClass(MaxElectricalConsumption.class);
        job.setMapperClass(MaxConsumptionMapper.class);
        job.setReducerClass(MaxConsumptionReducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0])); // Input path
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output path

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

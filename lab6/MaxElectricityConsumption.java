import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;

public class MaxElectricityConsumption {

    // Mapper Class
    public static class MaxElectricityMapper extends Mapper<Object, Text, Text, IntWritable> {
        private Text year = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString().trim();
            String[] parts = line.split("\\s+"); // Split by spaces or tabs

            // Skip the header row
            if (parts[0].equalsIgnoreCase("year")) {
                return;
            }

            try {
                year.set(parts[0]); // Extract year
                int maxConsumption = Integer.MIN_VALUE;

                // Iterate over monthly values (ignoring the last column "Average")
                for (int i = 1; i < parts.length - 1; i++) {
                    int consumption = Integer.parseInt(parts[i]);
                    maxConsumption = Math.max(maxConsumption, consumption);
                }

                context.write(year, new IntWritable(maxConsumption));
            } catch (NumberFormatException e) {
                // Ignore lines with invalid numbers
            }
        }
    }

    // Reducer Class
    public static class MaxElectricityReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int maxConsumption = Integer.MIN_VALUE;
            for (IntWritable val : values) {
                maxConsumption = Math.max(maxConsumption, val.get());
            }
            context.write(key, new IntWritable(maxConsumption));
        }
    }

    // Driver Class (Main Method)
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Max Electricity Consumption");

        job.setJarByClass(MaxElectricityConsumption.class);
        job.setMapperClass(MaxElectricityMapper.class);
        job.setReducerClass(MaxElectricityReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class HighestDriver extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {
        // Ensure correct number of arguments
        if (args.length != 2) {
            System.err.println("Usage: HighestDriver <input path> <output path>");
            return -1;
        }

        JobConf conf = new JobConf(getConf(), HighestDriver.class);
        conf.setJobName("Highest Temperature Per Year");

        conf.setMapperClass(HighestMapper.class);
        conf.setReducerClass(HighestReducer.class);

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(conf, new Path(args[0]));  // Input Path
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));  // Output Path

        JobClient.runJob(conf);
        return 0;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new HighestDriver(), args);
        System.exit(exitCode);
    }
}

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MovieTagsAnalyzer {
    // Mapper Class
    public static class TagsMapper extends Mapper<LongWritable, Text, Text, Text> {
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] fields = value.toString().split("::");
            if (fields.length >= 3) {
                String movieId = fields[1].trim();
                String tags = fields[2].trim();
                context.write(new Text(movieId), new Text(tags));
            }
        }
    }

    // Reducer Class
    public static class TagsReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuilder tagsBuilder = new StringBuilder();
            for (Text value : values) {
                tagsBuilder.append(value.toString()).append(", ");
            }
            // Remove the last comma and space
            String allTags = tagsBuilder.toString().replaceAll(", $", "");
            context.write(key, new Text(allTags));
        }
    }

    // Driver Method
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "movie tags analyzer");
        job.setJarByClass(MovieTagsAnalyzer.class);
        job.setMapperClass(TagsMapper.class);
        job.setReducerClass(TagsReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0])); // Input path
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output path
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MatrixMultiplication {

    // Mapper class
    public static class MatrixMapper extends Mapper<Object, Text, Text, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] tokens = value.toString().split(",");
            if (tokens.length < 4) return;  // Ignore malformed lines

            String matrixName = tokens[0];
            int row = Integer.parseInt(tokens[1]);
            int col = Integer.parseInt(tokens[2]);
            int val = Integer.parseInt(tokens[3]);
            
            int matrixK = 2;
            
            if (matrixName.equals("A")) {
                // Emit row-wise elements of A
                for (int k = 0; k < matrixK; k++) {
                    context.write(new Text(row + "," + k), new Text("A," + col + "," + val));
                }
            } else if (matrixName.equals("B")) {
                // Emit column-wise elements of B
                for (int i = 0; i < matrixK; i++) {
                    context.write(new Text(i + "," + col), new Text("B," + row + "," + val));
                }
            }
        }
    }

    // Reducer class
    public static class MultiplicationReducer extends Reducer<Text, Text, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            int matrixK = 2;
            int[] vectorA = new int[matrixK];
            int[] vectorB = new int[matrixK];

            // Populate the vectors
            for (Text value : values) {
                String[] tokens = value.toString().split(",");
                String matrixName = tokens[0];
                int index = Integer.parseInt(tokens[1]);
                int val = Integer.parseInt(tokens[2]);

                if (matrixName.equals("A")) {
                    vectorA[index] = val;
                } else if (matrixName.equals("B")) {
                    vectorB[index] = val;
                }
            }

            // Compute dot product
            int result = 0;
            for (int i = 0; i < matrixK; i++) {
                result += vectorA[i] * vectorB[i];
            }

            // Output result (row, col) -> value
            context.write(key, new IntWritable(result));
        }
    }

    // Driver method
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "matrix multiplication");
        job.setJarByClass(MatrixMultiplication.class);

        job.setMapperClass(MatrixMapper.class);
        job.setReducerClass(MultiplicationReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0])); // Input
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
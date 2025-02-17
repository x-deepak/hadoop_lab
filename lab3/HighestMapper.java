import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class HighestMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String line = value.toString().trim(); // Trim whitespace

        // Ensure line is long enough before calling substring()
        String[] parts = line.split("\\s+"); // Splitting on whitespace

        if (parts.length == 2) { // Check if the line has at least 2 parts
            String year = parts[0];  // Year is the first column
            try {
                int temperature = Integer.parseInt(parts[1]); // Second column is the temperature
                output.collect(new Text(year), new IntWritable(temperature));
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid temperature value: " + parts[1]);
            }
        } else {
            System.err.println("Skipping malformed line: " + line);
        }
    }
}

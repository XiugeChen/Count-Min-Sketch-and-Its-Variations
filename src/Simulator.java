import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Simulator {
	private static final String FOLDER = "resources/data/dataWithUpdate/";
	private static final String FILE_PATH = "resources/data/dataWithUpdate/space_time_test.txt";
	private static final double ERROR = 0.01;
	private static final double BAD_PROB = 0.001;
	
	public static void main(String[] args) {
		// initialization
		int w = (int) Math.round(2.0 / ERROR);
		int d = (int) Math.round(Math.log(1.0 / BAD_PROB) / Math.log(2) + Math.log(Integer.MAX_VALUE) / Math.log(2));
		
		// loop through all files
		try (Stream<Path> walk = Files.walk(Paths.get(FOLDER))) {
			List<String> result = walk.map(x -> x.toString())
					.filter(f -> f.endsWith(".txt"))
					.filter(f -> !f.contains("result"))
					.filter(f -> !f.contains("test"))
					.filter(f -> !f.contains("88888"))
					.collect(Collectors.toList());

			for (String file: result) {
				System.out.println(file);
				// run all of three cms to measure the performance
				runAll(file, w, d);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Simulation ends");
		
		/*
		// run specific cms to measure the time
		double[] time = {0.0, 0.0};
		double[] total_time = {0.0, 0.0};
		//CMS cms = new CMS_conservative(d, w);
		//CMS cms = new CMS_default(d, w);
		CMS cms = new CMS_Morris(d, w);
		for (int i = 0; i < 1000; i++) {
			time = runOne(cms, w, d);
			total_time[0] += time[0];
			total_time[1] += time[1];
		}
		System.out.println("####INFO: Query time: " + (double) total_time[0] / 1000);
		System.out.println("####INFO: Update time: " + (double) total_time[1] / 1000);
		*/
		/*
		// run specific cms to measure the space
		double[] time = {0.0, 0.0};
		CMS cms = new CMS_conservative(d, w);
		//CMS cms = new CMS_default(d, w);
		//CMS cms = new CMS_Morris(d, w);
		time = runOne(cms, w, d);
		
		while (true) {}
		*/
	}
	
	private static void runAll(String filePath, int w, int d) {
		int numDistinctItems = 0;
		
		CMS_conservative cms_conservative = new CMS_conservative(d, w);
		CMS_default cms_default = new CMS_default(d, w);
		CMS_Morris cms_morris = new CMS_Morris(d, w);
		System.out.println("####INFO: Initialiazation finished");
		
		// updating data
		try (LineNumberReader fp = new LineNumberReader(new FileReader(new File(filePath)))) {
			String output_file = filePath;
			output_file = output_file.replaceAll("dataWithUpdate", "dataWithUpdate/result");
			output_file = output_file.replaceAll(".txt", "_runningResult_error" 
					+ ERROR + "_badprob" + BAD_PROB + ".txt");
			File file = new File(output_file);
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			
            String s;
            System.out.println("####INFO: started reading and querying");
            while ((s = fp.readLine()) != null) {
            	// query sketch
            	if (s.startsWith("#")) {
            		if (s.contains("####"))
            			continue;
            		
            		String[] data = s.split(",");
            		String sample = data[1];
            		int correct_result = Integer.parseInt(data[2]);
            		String count = data[3];
            		
            		// query items
            		long result_conservative = cms_conservative.query(sample);
            		long result_default = cms_default.query(sample);
            		long result_morris = cms_morris.query(sample);
            		
            		String output = String.format("sample:%s,count:%s,correct:%d,conservatice:%d,default:%d,morris:%d\n", 
            				sample, count, correct_result, result_conservative, result_default, result_morris);
            		fw.write(output);
            		
            		numDistinctItems++;
            	}
            	// update sketch
            	else {
            		String[] data = s.split(",");
            		String sample = data[0];
            		int update = Integer.parseInt(data[1]);
            		
            		cms_conservative.update(sample, update);
            		cms_default.update(sample, update);
            		cms_morris.update(sample, update);
            	}
            }
            System.out.println("####INFO: finishing reading and querying");
            
            fw.write("numDistinctItems:" + numDistinctItems + "\n");
            
            System.out.println("####INFO: end application");
            fw.close();
            fp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private static double[] runOne(CMS cms, int w, int d) {
		long numQuery = 0;
		long totalTimeQuery = 0;
		long numUpdate = 0;
		long totalTimeUpdate = 0;
		
		// updating data
		try (LineNumberReader fp = new LineNumberReader(new FileReader(new File(FILE_PATH)))) {
            String s;
            System.out.println("####INFO: started reading and querying");
            while ((s = fp.readLine()) != null) {
            	// query sketch
            	if (s.startsWith("#")) {
            		if (s.contains("####"))
            			continue;
            		
            		String[] data = s.split(",");
            		String sample = data[1];
            		
            		// query items
            		long start = System.nanoTime();
            		long result = cms.query(sample);
            		long end = System.nanoTime();
            		totalTimeQuery += end - start;
            		numQuery++;
            	}
            	// update sketch
            	else {
            		String[] data = s.split(",");
            		String sample = data[0];
            		int update = Integer.parseInt(data[1]);
            		
            		long start = System.nanoTime();
            		cms.update(sample, update);
            		long end = System.nanoTime();
            		totalTimeUpdate += end - start;
            		numUpdate++;
            	}
            }
            System.out.println("####INFO: finishing reading and querying");
            
            fp.close();
            System.out.println("####INFO: end application");
            
            double result[] = {0.0, 0.0};
            result[0] = (double) totalTimeQuery / numQuery;
            result[1] = (double) totalTimeUpdate / numUpdate;
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
}

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;

public class Simulator {
	private static final String FILE_PATH = "resources/data/dataWithUpdate/increment_pos_max100zipf_s1.4_len10000.txt";
	private static final double ERROR = 0.001;
	private static final double BAD_PROB = 0.001;
	
	public static void main(String[] args) {
		// initialization
		int w = (int) Math.round(2.0 / ERROR);
		int d = (int) Math.round(Math.log(1.0 / BAD_PROB) / Math.log(2));
		
		// run all of three cms to measure the performance
		// runAll(w, d);
		
		// run specific cms to measure the space
		CMS cms = new CMS_conservative(d, w);
		// CMS cms = new CMS_default(d, w);
		// CMS cms = new CMS_Morris(d, w);
		runOne(cms, w, d);
	}
	
	private static void runAll(int w, int d) {
		int numDistinctItems = 0;
		
		CMS_conservative cms_conservative = new CMS_conservative(d, w);
		CMS_default cms_default = new CMS_default(d, w);
		CMS_Morris cms_morris = new CMS_Morris(d, w);
		System.out.println("####INFO: Initialiazation finished");
		
		// updating data
		try (LineNumberReader fp = new LineNumberReader(new FileReader(new File(FILE_PATH)))) {
			String output_file = FILE_PATH;
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
            		
            		// query items
            		int result_conservative = cms_conservative.query(sample);
            		int result_default = cms_default.query(sample);
            		int result_morris = cms_morris.query(sample);
            		
            		String output = String.format("sample:%s,correct:%d,conservatice:%d,default:%d,morris:%d\n", 
            				sample, correct_result, result_conservative, result_default, result_morris);
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
	
	private static void runOne(CMS cms, int w, int d) {
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
            		int result = cms.query(sample);
            	}
            	// update sketch
            	else {
            		String[] data = s.split(",");
            		String sample = data[0];
            		int update = Integer.parseInt(data[1]);
            		
            		cms.update(sample, update);
            	}
            }
            System.out.println("####INFO: finishing reading and querying");
            
            fp.close();
            System.out.println("####INFO: end application");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}

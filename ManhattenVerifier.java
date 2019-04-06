import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ManhattenVerifier {
	// initialize class level variables
	static int NoOfUsers = 51;
	static int NoOFRecordsPerUser = 400;
	static int NoOfColumnsInRecord = 31;
    static int n;
	static int userNumber;
    static String fileLocation = "DSL-StrongPasswordData.txt";
    static double threshold;
	static ArrayList<double[][]> CompiledData = new ArrayList<>();

    public static void main(String[] args) {
		//program start
		Scanner scan = new Scanner(System.in);
		boolean inputLoop = true;
		String option;

		LoadData(); //fetch data from text file to class level variable CompiledData

		while(inputLoop){ //present options
			System.out.println("PLEASE SELECT AN OPTION");
			System.out.println("[1] Calculate Key Hold to get False Accept Rate and False Reject Rate");
			System.out.println("[2] Calculate Key Interval to get False Accept Rate and False Reject Rate");
			System.out.println("[3] Calculate Key Hold values");
			System.out.println("[4] Calculate Interval values");
			System.out.println("[5] Calculate Genuine Scores");
			System.out.println("[6] Calculate Imposter Scores");
			System.out.println("[7] EXIT");

			option = scan.nextLine();

			switch (option) { //select options
					case "1":
						KeyHoldFalseAcc_RejRate(scan);
					break;
					case "2":
						KeyIntervalFalseAcc_RejRate(scan);
					break;
					case "3":
                            KeyHold(scan);
					break;
					case "4":
                            Interval(scan);
					break;
					case "5":
                            GenuineScores(scan);
						break;
						case "6":
							ImposterScores(scan);
						break;
					case "7":
						inputLoop = false;
					break;
			}
			
		}
    }

	//loads data from text file to CompiledData
	public static void LoadData() {
		for(int usernumber = 1;usernumber<=NoOfUsers;usernumber++){
			String record = null;
			int linecount = 1;

			File file = new File(fileLocation);

			StringBuilder stringBuilder = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
		
				reader.readLine(); //skip headings of data

				for(int ii = 1; ii<=((usernumber-1) * NoOFRecordsPerUser);ii++ ) //skip all user data which are before usernumber
					reader.readLine();

				while ((record = reader.readLine()) != null && linecount <= NoOFRecordsPerUser) { //get 400 records for the usernumber requested
					linecount++;
					String hh=record.trim().replaceAll("( )+", " ");
						stringBuilder.append(hh+" ");
				}
			}
			catch (Exception ex) {
				System.out.println("Problem reading or locating ther input data file");
			}

			String[] results1 = stringBuilder.toString().replace("\t", " ").split(" ");

			double[] singleUserData = new double[results1.length];
			int filedatacolumn = 0;
			int jj=0;
			for (int j=0; j < results1.length; j++) {
			if(results1[j].indexOf('s')>=0)
				filedatacolumn=3;

			if(filedatacolumn >0)
				filedatacolumn --;
			else{
				singleUserData[jj] = Double.parseDouble(results1[j]);
				jj++;
				filedatacolumn=0;
				}
			}

			double[][] tempUserData = new double[NoOFRecordsPerUser][NoOfColumnsInRecord];

			int k = 0;
			for (int i = 0; i < NoOFRecordsPerUser; i++) {
				for (int j = 0; j < NoOfColumnsInRecord; j++) {
					tempUserData[i][j] = singleUserData[k++];
				}
			}
			CompiledData.add(tempUserData);
		}
    }
	
	//initialize and get key hold false accept and reject rates
    public static void KeyHoldFalseAcc_RejRate(Scanner scan) {
        System.out.print("Enter a user number (1 - " + NoOfUsers + ") > ");
        userNumber = (scan.nextInt() - 1);
		System.out.print("Enter value for N: ");
        n = scan.nextInt();
        System.out.print("Enter a threshold value from (0 - 0.1): ");
        threshold = scan.nextDouble();

        ManhattenVerifierCalculations ManhattenObj = new ManhattenVerifierCalculations(CompiledData, userNumber, n);
        ArrayList<double[]> genuijneScores = ManhattenObj.getGenuineKeyHold();

        ManhattenObj = new ManhattenVerifierCalculations(CompiledData, userNumber, n);
        ArrayList<double[][]> imposterScores = ManhattenObj.impostorScoreKeyHold();

        double[] cr = ManhattenObj.getRates(genuijneScores, imposterScores, threshold);
        
        System.out.println("Key Hold - False Accept Rate: " + new DecimalFormat("###.##").format((cr[3] / cr[2]) * 100) + "% ");
		System.out.println("Key Hold - False Reject Rate: " + new DecimalFormat("###.##").format((cr[1] / cr[0]) * 100) + "% ");
		
		scanNxtLine(scan);
    }

	//initialize and get key interval false accept and reject rates
    public static void KeyIntervalFalseAcc_RejRate(Scanner scan) {
        System.out.print("Enter user number from (1 - " + NoOfUsers + "): ");
        userNumber = (scan.nextInt() - 1);
		System.out.print("Enter value for N: ");
        n = scan.nextInt();
        System.out.print("Enter threshold value from (0 - 0.1): ");
        threshold = scan.nextDouble();

        ManhattenVerifierCalculations ManhattenObj = new ManhattenVerifierCalculations(CompiledData, userNumber, n);
        ArrayList<double[]> genuineScores = ManhattenObj.genuineScoreInt();

        ManhattenObj = new ManhattenVerifierCalculations(CompiledData, userNumber, n);
        ArrayList<double[][]> imposterScores = ManhattenObj.getImpostorScoreInterval();

        double[] cr = ManhattenObj.getRates(genuineScores, imposterScores, threshold);

        System.out.println("Interval - False Accept Rate: " + new DecimalFormat("###.##").format((cr[3] / cr[2]) * 100) + "%");
		System.out.println("Interval - False Reject Rate: " + new DecimalFormat("###.##").format((cr[1] / cr[0]) * 100) + "%");

		scanNxtLine(scan);
    }

	//initialize and get key hold values
	public static void KeyHold(Scanner scan){
							System.out.print("Enter user number from (1 - " + NoOfUsers + "): ");
                            userNumber = scan.nextInt() - 1;

                            ManhattenVerifierCalculations ManhattenObj = new ManhattenVerifierCalculations(CompiledData, userNumber, 0);
                            displayData(ManhattenObj.getKeyHold());

							scanNxtLine(scan);
	}

	//initialize and get interval scores
	public static void Interval(Scanner scan){
							System.out.print("Enter user number from (1 to " + NoOfUsers + "): ");
                            userNumber = scan.nextInt() - 1;

                            ManhattenVerifierCalculations ManhattenObj = new ManhattenVerifierCalculations(CompiledData, userNumber, 0);
                            displayData(ManhattenObj.getInterval());

                            scan.nextLine();
							System.out.print("Press Enter to continue.");
                            scan.nextLine();
	}

	// initialize and get genuine scores
	public static void GenuineScores(Scanner scan){
							System.out.print("Enter user number from (1 to " + NoOfUsers + "): ");
                            int userGen = scan.nextInt();
                            userNumber = userGen - 1;
                            System.out.print("Enter value of N: ");
                            int N = scan.nextInt();

                            ManhattenVerifierCalculations ManhattenObj = new ManhattenVerifierCalculations(CompiledData, userNumber, N);
                            
							ArrayList<double[]> userGenScores = ManhattenObj.getGenuineKeyHold();
                            System.out.println("Key Hold - Genuine Scores for User: " + userGen + " are ");
                            displayData(userGenScores.get(userNumber));
							
							ArrayList<double[]> userGenuineScores = ManhattenObj.getGenuineKeyHold();
                            System.out.println("Interval - Genuine Scores for User: " + userGen + " are ");
                            displayData(userGenuineScores.get(userNumber));

							scanNxtLine(scan);
	}

	// initialize and get imposter scores
	public static void ImposterScores(Scanner scan){
		System.out.print("Enter user number from (1 to " + NoOfUsers + "): ");
                            int user = scan.nextInt();
                            userNumber = user - 1;
                            System.out.print("Enter value for N: ");
                            int n = scan.nextInt();

                            ManhattenVerifierCalculations ManhattenObj = new ManhattenVerifierCalculations(CompiledData, userNumber, n);
                            
							ArrayList<double[][]> userImpScores = ManhattenObj.impostorScoreKeyHold();
                            System.out.println("Key Hold - Impostor Scores for User " + user + " are ");
                            displayData(userImpScores.get(userNumber));
							
                            ArrayList<double[][]> userImposterScores = ManhattenObj.getImpostorScoreInterval();
                            System.out.println("Interval - Impostor Scores for User " + user + " are ");
                            displayData(userImposterScores.get(userNumber));

							scanNxtLine(scan);
	}

	//display data after fetched
	public static void displayData(double[] data) {
        for (int i = 0; i < data.length; i++) {
            System.out.println(data[i] + " ");
        }
        System.out.println();
    }

	// display data after fetched
    public static void displayData(double[][] data) {
        for (int i = 0; i < data.length; i++) {
            for (int c = 0; c < data[i].length; c++) {
                System.out.print(data[i][c] + " ");
            }
            System.out.println();
        }
    }

	// wait after results
	public static void scanNxtLine(Scanner scan){
							scan.nextLine();
							System.out.print("Press Enter to continue.");
                            scan.nextLine();
	}
}
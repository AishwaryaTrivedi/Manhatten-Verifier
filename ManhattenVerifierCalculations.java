import java.util.ArrayList;

public class ManhattenVerifierCalculations {

	// initialize class level variable
    private int N = 10;
    ArrayList<double[][]> CompiledData = new ArrayList<>();
    private double[][] userData;

	//constructor
    public ManhattenVerifierCalculations(ArrayList<double[][]> compiledData, int userNumber, int n) {
        this.N = n;
        this.userData = compiledData.get(userNumber);
        this.CompiledData = compiledData;
    }

	// Get false accep rate and false reject rate 
	// from genuine scores and imposter scores:
	public double[] getRates(ArrayList<double[]> genuineScores, ArrayList<double[][]> imposterScores, double threshhold) {
        double[] genuinescores;
        double[][] imposterscores;
        double[] rates = new double[4];

        int falseRejectRates = 0;
        int falseAccRates = 0;
        int count = 0;

        for (int i = 0; i < genuineScores.size(); i++) {
            genuinescores = genuineScores.get(i);
            for (int j = 0; j < genuinescores.length; j++) {
                count++;
                if (genuinescores[j] > threshhold) {
                    falseRejectRates++;
                }
            }
        }
        rates[0] = count;
        rates[1] = falseRejectRates;

        count = 0;
        for (int i = 0; i < imposterScores.size(); i++) {
            imposterscores = imposterScores.get(i);
            for (int j = 0; j < imposterscores.length; j++) {
                for (int k = 0; k < imposterscores[j].length; k++) {
                    count++;
                    if (imposterscores[j][k] <= threshhold) {
                        falseAccRates++;
                    }
                }
            }
            rates[2] = count;
            rates[3] = falseAccRates;
        }
        return rates;
    }

	// calculate keyhold template 
	// to get mean key hold template results:
    public double[][] getKeyHoldTemplate() {

        double[][] userKeyHold = userData;
        double[][] keyHold = new double[N][11];

        for (int i = 0, j = 0; i < N; i++) {
            for (int k = 0; k < userKeyHold[i].length; k += 3) {
                keyHold[i][j++] = userKeyHold[i][k];
            }
            j = 0;
        }
        return keyHold;
    }

	// get testing results to get Imposter scores Key hold 
	// and genuine scores key hold:
    public double[][] getKeyHoldTest() {
        double[][] userKeyHold = userData;
        int testing = userKeyHold.length - N;
        double[][] keyHold = new double[testing][11];

        for (int i = N, j = 0; i < userKeyHold.length; i++) {
            for (int k = 0; k < userKeyHold[i].length; k += 3) {
                keyHold[(i - N)][j++] = userKeyHold[i][k];
            }
            j = 0;
        }
        return keyHold;
    }

	//Get key hold values for selected user
    public double[][] getKeyHold() {
        double[][] userKeyHold = userData;
        double[][] keyHold = new double[userKeyHold.length][11];

        for (int i = 0, j = 0; i < userKeyHold.length; i++) {
            for (int c = 0; c < userKeyHold[i].length; c += 3) {
                keyHold[i][j++] = userKeyHold[i][c];
            }
            j = 0;
        }
        return keyHold;
    }

	//get interval values for selected user
    public double[][] getInterval() {
        double[][] userInterval = userData;
        double[][] keyHold = new double[userInterval.length][10];

        for (int i = 0, j = 0; i < userInterval.length; i++) {
            for (int c = 2; c < userInterval[i].length; c += 3) {
                keyHold[i][j++] = userInterval[i][c];
            }
            j = 0;
        }
        return keyHold;
    }
	
	// get mean key hold template values
	// to get imposter scores for Key hold and genuine scores for key hold
    public double[] getMeanKeyHoldTemplate() {
        double[][] userKeyHold = getKeyHoldTemplate();
        int keyHoldLength = userKeyHold[0].length;

        double[] KeyHoldMean = new double[keyHoldLength];

        for (int j = 0; j < keyHoldLength; j++) {
            for (int i = 0; i < userKeyHold.length; i++) {
                KeyHoldMean[j] += (userKeyHold[i][j] / userKeyHold.length);
            }
        }
        return KeyHoldMean;
    }

	// get interval template values 
	// to get mean key interval template values
    public double[][] getIntervalTemplate() {
        double[][] userInterval = userData;
        double[][] interval = new double[400][10];

        for (int i = 0, j = 0; i < N; i++) {
            for (int k = 2; k < userInterval[i].length; k += 3) {
                interval[i][j++] = userInterval[i][k];
            }
            j = 0;
        }
        return interval;
    }

	// get interval test values 
	// for interval imposter scores and genuine scores
    public double[][] getIntervalTest() {
        double[][] userInterval = userData;
        double[][] interval = new double[userInterval.length - N][10];
        
        for (int i = N, j = 0; i < userInterval.length; i++) {
            for (int k = 2; k < userInterval[i].length; k += 3) {
                interval[i - N][j++] = userInterval[i][k];
            }
            j = 0;
        }

        return interval;
    }
	
	// get interval test values 
	// for interval imposter scores interval and genuine scores interval
    public double[] getMeanIntervalTemplate() {
        double[][] userInterval = getIntervalTemplate();

        double[] meanInterval = new double[userInterval[0].length];

        for (int i = 0; i < userInterval.length; i++) {
            for (int j = 0; j < userInterval[i].length; j++) {
                meanInterval[j] += (userInterval[i][j] / userInterval.length);
            }
        }
        return meanInterval;
    }

	// calculate Key hold imposter score for single user
    public ArrayList<double[][]> impostorScoreKeyHold() {
        ArrayList<double[][]> imposterScores = new ArrayList<>();
        double[] userMeanTempKeyHold = new double[11];
        double tempValue = 0;
        double[][] userImposterScores = new double[userData.length - N][11];
        double[][] template = userData;
        userMeanTempKeyHold = getMeanKeyHoldTemplate();

        for (int j = 0; j < CompiledData.size(); j++) {

            if (template != CompiledData.get(j)) {
                userData = CompiledData.get(j);

                double[][] KeyHoldTesting = new double[userData.length - N][11];
                KeyHoldTesting = getKeyHoldTest();

                for (int x = 0; x < (userData.length - N); x++) { // go through tests
                    for (int y = 0; y < KeyHoldTesting[y].length; y++) {
                        tempValue = +Math.abs(userMeanTempKeyHold[y] - KeyHoldTesting[x][y]);
                        userImposterScores[x][y] = tempValue;
                        tempValue = 0;
                    }
                }
            }
            imposterScores.add(userImposterScores);
        }
        return imposterScores;
    }
	
	// get imposter scores for a single user
    public ArrayList<double[][]> getImpostorScoreInterval() {
        ArrayList<double[][]> imposterScores = new ArrayList<>();
        double[] userMeanTemplateInterval = new double[10];
        double temp = 0;
        double[][] userIntervalScore = new double[userData.length - N][10];
        userMeanTemplateInterval = getMeanIntervalTemplate();
		
        for (int j = 0; j < CompiledData.size(); j++) {

            if (userData != CompiledData.get(j)) {
                userData = CompiledData.get(j);

                double[][] intervalTest = new double[userData.length - N][10];
                intervalTest = getIntervalTest();

                for (int x = 0; x < (userData.length - N); x++) { // go through tests
                    for (int y = 0; y < intervalTest[y].length; y++) { 
                        temp = +Math.abs(userMeanTemplateInterval[y] - intervalTest[x][y]);

                        userIntervalScore[x][y] = temp;
                        temp = 0;
                    }
                }
            }
            imposterScores.add(userIntervalScore);
        }
        return imposterScores;
    }
	
    // get genuine key hold values by comparing test and template values
    public ArrayList<double[]> getGenuineKeyHold() {
        ArrayList<double[]> GenuineScores = new ArrayList<>();
        double[][] KeyHoldTest = getKeyHoldTest();

        double genuineScore = 0;

        for (int k = 0; k < CompiledData.size(); k++) {
            userData = CompiledData.get(k);
            double[] KeyHoldTemplate = getMeanKeyHoldTemplate();
            double[] genuineScores = new double[KeyHoldTest.length];

            for (int i = 0; i < KeyHoldTest.length; i++) {
                for (int j = 0; j < KeyHoldTemplate.length; j++) {
                    genuineScore += Math.abs(KeyHoldTemplate[j] - KeyHoldTest[i][j]);
                }
                genuineScore = (genuineScore / KeyHoldTemplate.length);
                genuineScores[i] = genuineScore;
                genuineScore = 0;
            }

            GenuineScores.add(genuineScores);
        }
        return GenuineScores;
    }
	
    // get genuine interval values by comparing test and template values
    public ArrayList<double[]> genuineScoreInt() {

        int size = CompiledData.size();
        ArrayList<double[][]> userList = CompiledData;
        ArrayList<double[]> resultInt = new ArrayList<>();
        double[][] test = getIntervalTest();

        double gs = 0;

        for (int x = 0; x < size; x++) {
            userData = userList.get(x);
            double[] template = getMeanIntervalTemplate();
            double[] genuineScore = new double[test.length];

            for (int i = 0; i < test.length; i++) {
                for (int j = 0; j < template.length; j++) {
                    gs += Math.abs(template[j] - test[i][j]);
                }
                gs = (gs / template.length);
                genuineScore[i] = gs;
                gs = 0;
            }
            resultInt.add(genuineScore);

        }

        return resultInt;

    }

    
	
    

}
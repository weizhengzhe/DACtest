public class DFTclass {

    public static void dft(double[] inR, double[] outR, double[] outI) {
        for (int k = 0; k < inR.length; k++) {
            for (int t = 0; t < inR.length; t++) {
                outR[k] += inR[t] * Math.cos(2 * Math.PI * t * k / inR.length);
                outI[k] -= inR[t] * Math.sin(2 * Math.PI * t * k / inR.length);
            }
        }
    }

    public static void main(String[] args) throws Exception {

        //        for (int i = 0; i < buffer.length; i++) {
//            System.out.println(buffer[i]);
//        }

//        double[] outR = new double[buffer.length];
//        double[] outI = new double[buffer.length];
//
//        dft(buffer, outR, outI);
//
//        double[] results = new double[buffer.length];
//        for (int i = 0; i < buffer.length; i++) {
//            double real = outR[i];
//            double imaginary = outI[i];
//            results[i] = Math.sqrt(real * real + imaginary * imaginary);
//        }

    }

}
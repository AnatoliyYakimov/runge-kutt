package ru.yakimov;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RungeKuttMethod {


	public static void main(String[] args) throws IOException {
		if (args.length < 6 || args[0].equals("-h")) {
			System.out.println("Arguments: A B hMin hMax yn epsilon");
			System.exit(0);
		}
		double A = Double.valueOf(args[0]);
		double B = Double.valueOf(args[1]);
		double hMin = Double.valueOf(args[2]);
		double hMax = Double.valueOf(args[3]);
		double y = Double.valueOf(args[4]);
		double eps = Double.valueOf(args[5]);
		count(A, B, hMin, hMax, y, eps);
		System.out.println("Done!");
	}

	public static void count(Double A, Double B, Double hMin, Double hMax, Double yc, Double eps) throws IOException {
		StringBuilder sb = new StringBuilder();
		Step step = new Step(hMin, hMax, eps);
		int N = 1;
		double xn = A;
		double yn = yc;
		double yp3;
		double yp4 = yc;
		double localError;
		double h = step.estimateStartingStep(A, B);

		sb.append("N;x;y;error\n");
		sb.append(String.format("%d;%f;%f;%f\n", N, xn, yn, 0d));
		while (xn <= B) {
			yp3 = rungeKutt3(xn, yn, h);
			yp4 = rungeKutt4(xn, yp4, h);
			localError = Math.abs(yp3 - yp4);
			h = step.estimate(h, localError);
			xn = xn + h;
			yn = yp3;
			N++;
			sb.append(String.format("%d;%.4f;%.4f;%.16f\n", N, xn, yn, localError));
		}
		try (OutputStream os = new FileOutputStream("stats.csv")) {
			os.write(sb.toString().getBytes());
		}
		StringBuilder summary = new StringBuilder();
		summary.append(String.format("Всего произведено %d шагов\n", N));
		String accuracy = step.isAccuracyAchieved() ? "достигнута" : "не достигнута";
		summary.append(String.format("Заданная точность %f %s\n", eps, accuracy));
		if (!step.isAccuracyAchieved()) {
			summary.append(String.format("Шагов, для которых погрешность превысила eps: %d\n",
				step.getIterationsWithErrorMoreThanEpsilon()));
		}
		summary.append(String.format("Шагов с максимальным шагом %f: %d\n", hMax, step.getIterationsWithMaxStep()));
		summary.append(String.format("Шагов с минимальным шагом %f: %d\n", hMin, step.getIterationsWithMinStep()));
		double avgStep = step.getSteps().stream().reduce(0d, Double::sum) / step.getSteps().size();
		summary.append(String.format("Средний шаг: %f", avgStep));
		try (OutputStream os = new FileOutputStream("summary.txt")){
			os.write(summary.toString().getBytes());
		}
	}

	public static double f(double x, double y) {
		return Math.pow(x, 3) +  2 * Math.pow(x, 2) + 2 * x;
	}

	public static double rungeKutt3(double xn, double yn, double hn) {
		double k1, k2, k3, ynext;
		k1 = hn * f(xn, yn);
		k2 = hn * f(xn + hn / 3, yn + k1 / 3);
		k3 = hn * f(xn + 2 * hn / 3, yn + 2 * k2 / 3);

		ynext = yn + (k1 + 3 * k3) / 4;

		return ynext;
	}

	public static double rungeKutt4(double xn, double yn, double hn) {
		double k1, k2, k3, k4, ynext;
		k1 = hn * f(xn, yn);
		k2 = hn * f(xn + hn / 3, yn + k1 / 3);
		k3 = hn * f(xn + 2 * hn / 3, yn - k1 / 3 + k2);
		k4 = hn * f(xn + hn, yn + k1 - k2 + k3);
		ynext = yn + (k1 + 3 * k2 + 3 * k3 + k4) / 8;

		return ynext;
	}

}
